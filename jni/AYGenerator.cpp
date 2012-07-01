#include <string.h>
#include <jni.h>

#include "AYGenerator.h"

int mReplayFrequency;
int mPSGFrequency;

/** The sample rate at which the sound is played (44100, 22050 etc.) in Hz. 0 means that is has not been set. */
int mSampleRate = 0;

const static int HARDWARE_CURVES_CYCLE_LENGTH = 16;					// Nb steps a cycle consists of.
const static int HARDWARE_CURVES_NB_CYCLES = 4;
const static int HARDWARE_CURVES_LENGTH = HARDWARE_CURVES_CYCLE_LENGTH * 4;
const static int HARDWARE_CURVES_LOOP_TO = HARDWARE_CURVES_NB_CYCLES / 2 * HARDWARE_CURVES_CYCLE_LENGTH;	// We always loop to the 2nd cycle.

const static int NB_NOISE_VALUES = 32;

const static float SHORT_MAX_VALUE = 32767; // (BitRate == 8 ? 127 : 32767);
const static float OUTPUT_VOLUME_RATE_STEREO_CHANNEL_AC = 0.687f;
const static float OUTPUT_VOLUME_RATE_STEREO_CHANNEL_B = 0.313f;
const static float OUTPUT_VOLUME_RATE_STEREO_CHANNEL_DIFFERENCE = OUTPUT_VOLUME_RATE_STEREO_CHANNEL_B / OUTPUT_VOLUME_RATE_STEREO_CHANNEL_AC;

/** The Atari-ST MFC frequency. It is used to calculate the replay frequency of the samples. */
const static int MFC_FREQUENCY = 2457600;

/** Pre-divisor table used for to know the replay frequency of the samples. */
const static int mPredivisorTable[] = { 0, 4, 10, 16, 50, 64, 100, 200 };

bool mIsMixerSoundAOpen = false;									// True is Mixer bit to 0 ! Which means "open".
bool mIsMixerSoundBOpen = false;
bool mIsMixerSoundCOpen = false;
bool mIsMixerNoiseAOpen = false;
bool mIsMixerNoiseBOpen = false;
bool mIsMixerNoiseCOpen = false;
bool mIsMixerNoiseOnAnyChannel = false;
bool mIsHardwareEnvelopeUsedOnA = false;
bool mIsHardwareEnvelopeUsedOnB = false;
bool mIsHardwareEnvelopeUsedOnC = false;

float mPeriodRatio = 1;					// Calculated to convert any given Period into a period to output.
int mPlayerPeriod;                       // Calculated to know exactly when to send the registers.
int mPlayerPeriodCounter;     			// Counter increasing till the playerPeriod is reached.

int mPeriodCounterA;						// Counters increasing till the Period has been reached.
int mPeriodCounterB;
int mPeriodCounterC;
int mHardwarePeriodCounter;
int mHardwareCurveCounter;				// Counter inside the hardware Curve. From 0 to 63. Loops at 32.

int mNoisePeriod;
int mNoisePeriodCounter;

bool mSoundStateA = true;				// High state (true) or Low state (false), according only to the square signal being produced, not the mixer.
bool mSoundStateB = true;
bool mSoundStateC = true;

static int mRandomSeed = 0x12345678;
int mVolumeNoise = 0;					// Volume calculated randomly, used for the Noise. We keep it here to use it from a buffer to the next.

float mStepCounter = 0;
float mNextStepCounter = 0;				// Indicates what stepCounter will be on the next iteration.
float mNextOutputVolumeASum = -1;		// Indicates if an iteration begins with a partial volume from the previous iteration. -1 is no.
float mNextOutputVolumeBSum = -1;
float mNextOutputVolumeCSum = -1;
bool mIsNextOutputVolumeSum = false;		// Indicates if an iteration begins with a partial volume from the previous iteration.

// Raw volumes level.
const static int mVolumesBase[] = { 0, 231, 695, 1158, 2084, 2779, 4168, 6716, 8105, 13200, 18294, 24315, 32189, 40757, 52799, 65535 };
const static int NB_VOLUMES = 16;

// FIXME A Test.
static short mRegsTry[] = { 0, 1, 2, 3, 4, 5, 10, 0x38, 10, 11, 12, 0, 0, 0, 0, 0 };

// Volumes to be decreased according to the output (8/16 bits) but also the periodRatio, so that we don't have to divide the added "partial" volumes.
static float* mVolumes = 0;

static int** mHardwareCurves;								// Points to an array of volumes, for one HardwareCurve given.

// Conversion tables from VolumeBeforeNoise to VolumeAfterNoise, when noise=1, 2, 3, 4... Empirical tables, done according to signal recorded from hardware.
static int** mNoiseConverter;
static int mNoiseTable1[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
static int mNoiseTable2[] = { 0, 0, 2, 2, 4, 4, 6, 6, 8, 8, 10, 10, 12, 12, 14, 15 };
static int mNoiseTable3[] = { 0, 0, 0, 0, 7, 7, 7, 8, 8, 8, 9, 9, 9, 15, 15, 15 };
static int mNoiseTable4[] = { 0, 0, 0, 0, 0, 8, 8, 8, 8, 8, 8, 15, 15, 15, 15, 15 };
static int mNoiseTableLast[] = { 0, 0, 0, 0, 0, 0, 8, 8, 8, 8, 15, 15, 15, 15, 15, 15 };



int mPeriodA;
int mPeriodB;
int mPeriodC;
int mNoise;
int mMixer;
int mVolumeA;
int mVolumeB;
int mVolumeC;
int mHardwarePeriod;
int mHardwareEnveloppe;			    // Hardware Envelope used by the Hardware volume. Value from 0 to 0xf. Never contains 0xff.

short* mSampleA = 0;				// Pointers to the sample of channel A. Null means no sample is played.
short* mSampleB = 0;				// This is how are detected that a sample must be played.
short* mSampleC = 0;

float mSampleAIndex;					// Index on the sample of channel A.
float mSampleBIndex;
float mSampleCIndex;

int mSampleALength;
int mSampleBLength;
int mSampleCLength;

float mSampleAPeriod;					// Period of the samples.
float mSampleBPeriod;
float mSampleCPeriod;

//JNIEnv* mEnv;
//jobject mThis;

//jclass mClass;

//static JavaVM   *svm;
//static jobject  io;

jclass mClassAyBufferGenerator;
jmethodID mMethodIdGetNextRegistersFromJNI;

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
	JNIEnv  *env;
	//jclass  cls;

	//svm = vm;
	if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
		return -1;
	}
	//mEnv = env;

	mClassAyBufferGenerator = env->FindClass("aks/jnv/audio/AYBufferGenerator");

	mMethodIdGetNextRegistersFromJNI = env->GetMethodID(mClassAyBufferGenerator, "getNextRegistersFromJNI", "()[S");
	//mMethodIdGetNextRegistersFromJNI = env->GetMethodID(mClassAyBufferGenerator, "getNextRegistersFromJNI", "()S");

    //io = (*env)->NewGlobalRef(env, (*env)->NewObject(env, cls, constr));




   return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT void JNICALL Java_aks_jnv_audio_AYBufferGenerator_initializeGeneratorJNI(JNIEnv* env, jobject thiz, jint replayFrequency, jint PSGFrequency)
{
	//jclass mClass = mEnv->FindClass("aks.jnv.audio.AYBufferGenerator");

	//mEnv = env;
	//mThis = thiz;

	mReplayFrequency = replayFrequency;
	mPSGFrequency = PSGFrequency;

	mSampleRate = 44100;			// FIXME This should come from Java.

	mPeriodRatio = PSGFrequency / mSampleRate / 8.0f;
	mStepCounter = mPeriodRatio;
	mPlayerPeriod = (mSampleRate / replayFrequency) - 1;

	mPlayerPeriodCounter = mPlayerPeriod + 1;	// Forces the getting of registers on first pass.

	generateHardwareCurves();
	fillNoiseConverterTables();
	generateVolumes();



}

extern "C" JNIEXPORT void JNICALL Java_aks_jnv_audio_AYBufferGenerator_generateBufferJNI(JNIEnv* env, jobject thiz, jshortArray buffer)
{
	// Gets the buffer to fill.
	jshort* pBuffer = env->GetShortArrayElements(buffer, 0);
	jint size = env->GetArrayLength(buffer);

	generateAudioBuffer(env, thiz, pBuffer, size);

	// Releases the buffer.
	env->ReleaseShortArrayElements(buffer, pBuffer, 0);
}

void generateHardwareCurves()
{
	/**
	 * Generates all the Hardware Curves. Doing it once is enough.
	 */
	if (mHardwareCurves) {
		return;
	}

	mHardwareCurves = new int*[HARDWARE_CURVES_CYCLE_LENGTH];

	for (int i = 0; i < HARDWARE_CURVES_CYCLE_LENGTH; ++i) {
		int* curve = new int[HARDWARE_CURVES_LENGTH];

		bool hold = ((i & 1) != 0);
		bool alternate = ((i & 2) != 0);
		bool attack = ((i & 4) != 0);
		bool cont = ((i & 8) != 0);

		int x = 0;			// Position in the table. Always increases.
		int direction = 1, currentVolume = 0;

		for (int cycle = 0; cycle < HARDWARE_CURVES_NB_CYCLES; ++cycle) {
			if (direction != 0) {
				if (attack) {
					currentVolume = 0;
					direction = 1;
				} else {
					currentVolume = 15;
					direction = -1;
				}
			}

			// Performs one cycle.
			for (int cx = 0; cx < HARDWARE_CURVES_CYCLE_LENGTH; ++cx) {
				curve[x++] = currentVolume;
				currentVolume += direction;
				if (currentVolume < 0) {
					currentVolume = 0;
				} else if (currentVolume > 15) {
					currentVolume = 15;
				}

			}

			// If direction at 0, we don't do anything more, the curve is stuck forever.
			if (direction != 0) {
				// If NOT Continue, the generator stays/jump at 0 forever.
				if (!cont) {
					currentVolume = 0;
					direction = 0;
				} else {
					// Continue. If Hold, direction is forced to 0 forever.
					if (hold) {
						direction = 0;
						if (alternate) {
							currentVolume ^= 0xf;		// If Hold and Alternate, inverts the Volume and lets it this way forever.
						}
					} else {
						// No Hold. Alternate the curve if needed.
						if (alternate) {
							attack = !attack;
						}
					}
				}
			}
		}

		mHardwareCurves[i] = curve;
	}
}

void generateVolumes() {
	mVolumes = new float[NB_VOLUMES];

	float maxVolume = SHORT_MAX_VALUE; // (BitRate == 8 ? 127 : 32767);
	maxVolume *= OUTPUT_VOLUME_RATE_STEREO_CHANNEL_AC;  // The max volume is divided by the volume rate of the two louder channels (A and C).

	// Finds the ratio to convert the maximum volume value (= the last base volume) to 8 bits or 16 bits value.
	float bitRateRatio = mVolumesBase[NB_VOLUMES - 1] / maxVolume;

	for (int i = 0; i < NB_VOLUMES; ++i) {
		mVolumes[i] = (mVolumesBase[i] / bitRateRatio / mPeriodRatio);
	}

}


void fillNoiseConverterTables()
{
	// Links the Noise table conversion tables to their respective volume.
	mNoiseConverter = new int*[NB_NOISE_VALUES];
	mNoiseConverter[0] = mNoiseTable1;
	mNoiseConverter[1] = mNoiseTable1;
	mNoiseConverter[2] = mNoiseTable2;
	mNoiseConverter[3] = mNoiseTable3;
	mNoiseConverter[4] = mNoiseTable4;
	for (int i = 5; i < NB_NOISE_VALUES; ++i) {
		mNoiseConverter[i] = mNoiseTableLast;
	}
}












// ***************************************
// Main generation method
// ***************************************

void generateAudioBuffer(JNIEnv* env, jobject thiz, short* buffer, int bufferSize)
{
	int cVolumeA, cVolumeB, cVolumeC;
	float outputVolumeASum, outputVolumeBSum, outputVolumeCSum;
	float outputVolumeA, outputVolumeB, outputVolumeC;

	int i = 0;
	while (i < bufferSize) {

		// Calculates if we need new PSG registers according to the replay frequency of the song.
		if (++mPlayerPeriodCounter > mPlayerPeriod) {
			jshortArray array = (jshortArray)env->CallObjectMethod(thiz, mMethodIdGetNextRegistersFromJNI);
			jshort* regs = env->GetShortArrayElements(array, 0);
			interpretRegisters(regs);
			env->ReleaseShortArrayElements(array, regs, 0);

			mPlayerPeriodCounter = 0;
		}


		// Before beginning the iteration, we set the volume and step according the "previously unmanaged" volume and value.
		if (mIsNextOutputVolumeSum) {
			outputVolumeASum = mNextOutputVolumeASum;		// Previous value stored. We use it.
			outputVolumeBSum = mNextOutputVolumeBSum;
			outputVolumeCSum = mNextOutputVolumeCSum;
			mStepCounter = mNextStepCounter;
			mIsNextOutputVolumeSum = false;
		} else {
			outputVolumeASum = 0;							// No previous value stored. We start from scratch.
			outputVolumeBSum = 0;
			outputVolumeCSum = 0;
			mStepCounter = mPeriodRatio;
		}

		while (mStepCounter > 0) {
			// Manage Frequencies
			if (++mPeriodCounterA >= mPeriodA) {
				mSoundStateA = !mSoundStateA;
				mPeriodCounterA = 0;
			}

			if (++mPeriodCounterB >= mPeriodB) {
				mSoundStateB = !mSoundStateB;
				mPeriodCounterB = 0;
			}

			if (++mPeriodCounterC >= mPeriodC) {
				mSoundStateC = !mSoundStateC;
				mPeriodCounterC = 0;
			}


			// Manage Hardware Curve on the background. Even if not used, still moving.
			if (++mHardwarePeriodCounter >= mHardwarePeriod) {
				mHardwarePeriodCounter = 0;
				if (++mHardwareCurveCounter > (HARDWARE_CURVES_LENGTH - 1)) {
					mHardwareCurveCounter = HARDWARE_CURVES_LOOP_TO;
				}
			}


			// Manage Volume A.
			if (mIsHardwareEnvelopeUsedOnA) {				// Hardware volume ?
				// Hardware volume.
				if (mIsMixerSoundAOpen) {
					// Mixer is on. The volume depends on the High/Low shelf of the software envelope, but the high shelf is defined by the Hardware Curve.
					cVolumeA = (mSoundStateA ? mHardwareCurves[mHardwareEnveloppe][mHardwareCurveCounter] : 0);
				} else {
					// Mixer is off. The volume is only given by the Hardware Curve. No more High/low shelf.
					cVolumeA = mHardwareCurves[mHardwareEnveloppe][mHardwareCurveCounter];
				}
			} else {
				// Normal volume.
				// If Mixer is on, we manage the square wave volume according to the alternating sound state.
				if (mIsMixerSoundAOpen) {
					// Mixer On.
					cVolumeA = (mSoundStateA ? mVolumeA : 0);
				} else {
					// If Mixer is off, the volume is the only given, and doesn't move (useful for Noise with no sound, but also for samples (not used here) !)
					cVolumeA = mVolumeA;
				}
			}



			// Manage Volume B.
			if (mIsHardwareEnvelopeUsedOnB) {
				if (mIsMixerSoundBOpen) {
					cVolumeB = (mSoundStateB ? mHardwareCurves[mHardwareEnveloppe][mHardwareCurveCounter] : 0);
				} else {
					cVolumeB = mHardwareCurves[mHardwareEnveloppe][mHardwareCurveCounter];
				}
			} else {
				if (mIsMixerSoundBOpen) {
					cVolumeB = (mSoundStateB ? mVolumeB : 0);
				} else {
					cVolumeB = mVolumeB;
				}
			}



			// Manage Volume C.
			if (mIsHardwareEnvelopeUsedOnC) {
				if (mIsMixerSoundCOpen) {
					cVolumeC = (mSoundStateC ? mHardwareCurves[mHardwareEnveloppe][mHardwareCurveCounter] : 0);
				} else {
					cVolumeC = mHardwareCurves[mHardwareEnveloppe][mHardwareCurveCounter];
				}
			} else {
				if (mIsMixerSoundCOpen) {
					cVolumeC = (mSoundStateC ? mVolumeC : 0);
				} else {
					cVolumeC = mVolumeC;
				}
			}




			// Manage Noise, only if is used by a channel.
			if (mIsMixerNoiseOnAnyChannel) {
				if (++mNoisePeriodCounter >= mNoisePeriod) {
					mNoisePeriodCounter = 0;
					mRandomSeed = mRandomSeed ^ (mRandomSeed >> 7 ^ (mRandomSeed << 3)) + (int)outputVolumeASum;
					mVolumeNoise = mNoiseConverter[mNoise][mRandomSeed & 0xf];
				}

				// Noise on Channel A, B, C ?
				// FIXME could be better.
				if (mIsMixerNoiseAOpen) {
					cVolumeA = (mVolumeNoise > cVolumeA ? cVolumeA : mVolumeNoise);
				}

				if (mIsMixerNoiseBOpen) {
					cVolumeB = (mVolumeNoise > cVolumeB ? cVolumeB : mVolumeNoise);
				}

				if (mIsMixerNoiseCOpen) {
					cVolumeC = (mVolumeNoise > cVolumeC ? cVolumeC : mVolumeNoise);
				}
			}

			// Converts the 4-bit volume to output volume. Samples have priority.
			outputVolumeA = ((!mSampleA) ? mVolumes[cVolumeA] : mVolumes[mSampleA[(int)mSampleAIndex]]);
			outputVolumeB = ((!mSampleB) ? mVolumes[cVolumeB] : mVolumes[mSampleB[(int)mSampleBIndex]]);
			outputVolumeC = ((!mSampleC) ? mVolumes[cVolumeC] : mVolumes[mSampleC[(int)mSampleCIndex]]);

			// Adds the volume found to the sum. If the step is inside the "integer" part of the Steps, the full value written.
			// Else, only a "decimal" of it.
			if (mStepCounter >= 1) {
				outputVolumeASum += outputVolumeA;
				outputVolumeBSum += outputVolumeB;
				outputVolumeCSum += outputVolumeC;
				mStepCounter--;
			} else {
				// A "partial" volume is added. It is the end of the iteration.
				outputVolumeASum += outputVolumeA * mStepCounter;
				outputVolumeBSum += outputVolumeB * mStepCounter;
				outputVolumeCSum += outputVolumeC * mStepCounter;

				// We have to remember that we have to write the remaining part of the volume !
				float partialStepCounter = 1 - mStepCounter;
				mNextStepCounter = mPeriodRatio - partialStepCounter;
				mNextOutputVolumeASum = outputVolumeA * partialStepCounter;
				mNextOutputVolumeBSum = outputVolumeB * partialStepCounter;
				mNextOutputVolumeCSum = outputVolumeC * partialStepCounter;

				mIsNextOutputVolumeSum = true;
				mStepCounter = 0;

			}

		}

		// Make the index move forward inside the samples.
		if (mSampleA) {
			mSampleAIndex += mSampleAPeriod;
			if (mSampleAIndex >= mSampleALength) {
				mSampleA = 0;
			}
		}
		if (mSampleB) {
			mSampleBIndex += mSampleBPeriod;
			if (mSampleBIndex >= mSampleBLength) {
				mSampleB = 0;
			}
		}
		if (mSampleC) {
			mSampleCIndex += mSampleCPeriod;
			if (mSampleCIndex >= mSampleCLength) {
				mSampleC = 0;
			}
		}

		// Write the output data in the buffer, as 16 bits stereo values.
		buffer[i++] = (short)(outputVolumeASum + OUTPUT_VOLUME_RATE_STEREO_CHANNEL_DIFFERENCE * outputVolumeBSum);
		buffer[i++] = (short)(OUTPUT_VOLUME_RATE_STEREO_CHANNEL_DIFFERENCE * outputVolumeBSum + outputVolumeCSum);
	}
}



/**
 * Interprets the given registers to fill usable variables.
 * @param regs the PSG registers to interpret.
 */
void interpretRegisters(short* regs) {
	int r3 = regs[3];
	int r8 = regs[8];
	int r9 = regs[9];
	int r10 = regs[10];

	mPeriodA = (regs[0] + ((regs[1] & 0xf) << 8));
	mPeriodB = (regs[2] + ((r3 & 0xf) << 8));
	mPeriodC = (regs[4] + ((regs[5] & 0xf) << 8));

	mNoise = (regs[6] & 0x1f);
	mNoisePeriod = mNoise << 1;

	mMixer = regs[7];
	mIsMixerSoundAOpen = ((mMixer & 0x1) == 0);
	mIsMixerSoundBOpen = ((mMixer & 0x2) == 0);
	mIsMixerSoundCOpen = ((mMixer & 0x4) == 0);
	mIsMixerNoiseAOpen = ((mMixer & 0x8) == 0);
	mIsMixerNoiseBOpen = ((mMixer & 0x10) == 0);
	mIsMixerNoiseCOpen = ((mMixer & 0x20) == 0);
	mIsMixerNoiseOnAnyChannel = mIsMixerNoiseAOpen | mIsMixerNoiseBOpen | mIsMixerNoiseCOpen;

	mVolumeA = (r8 & 0x1f);
	mVolumeB = (r9 & 0x1f);
	mVolumeC = (r10 & 0x1f);
	mHardwarePeriod = (regs[11] + (regs[12] << 8)) << 1;

	// Samples ? Detected thanks to bit 5-4 of r3.
	int r3sample = (r3 & 0x30);
	if (r3sample != 0) {
		// SampleFrequency = MFC_FREQUENCY / TP / TC
		// TP = R8 (b7-b5) -> through prediv table.
		// TC = R15.
		int timerPredivisor = mPredivisorTable[(r8 >> 5) & 0x7];
		float sampleFrequency = MFC_FREQUENCY / (float)timerPredivisor / regs[15];

		float samplePeriod = sampleFrequency / mSampleRate;

		// FIXME Manage sample.

//		switch (r3sample) {
//		case 0x10:
//			mSampleA = songReader.getSample(r8);
//			if (mSampleA) {										// Some buggy YM (LedStorm2) may use this value without
//				mSampleALength = sampleA.length;					// actually having any samples.
//				mSampleAIndex = 0;
//				mSampleAPeriod = samplePeriod;
//			}
//			break;
//		case 0x20:
//			mSampleB = songReader.getSample(r9);
//			if (mSampleB) {
//				mSampleBLength = sampleB.length;
//				mSampleBIndex = 0;
//				mSampleBPeriod = samplePeriod;
//			}
//			break;
//		case 0x30:
//			mSampleC = songReader.getSample(r10);
//			if (mSampleC) {
//				mSampleCLength = sampleC.length;
//				mSampleCIndex = 0;
//				mSampleCPeriod = samplePeriod;
//			}
//			break;
//		}
	}

	mIsHardwareEnvelopeUsedOnA = ((mVolumeA & 0x10) > 0);
	mIsHardwareEnvelopeUsedOnB = ((mVolumeB & 0x10) > 0);
	mIsHardwareEnvelopeUsedOnC = ((mVolumeC & 0x10) > 0);

	// If Given R13 equals 0xff, nothing is done. Else, resets the Envelope counter.
	int tempHardwareEnveloppe = regs[13];
	if (tempHardwareEnveloppe != 0xff) {
		mHardwareEnveloppe = tempHardwareEnveloppe & 0xf;
		mHardwarePeriodCounter = 0;
		mHardwareCurveCounter = 0;
	}
}




