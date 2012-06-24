package aks.jnv.audio;

import aks.jnv.reader.ISongReader;

/**
 * Generator of the audio buffer from an AY processor.
 * 
 * It fills an audio buffer according to the registers it has access to. It works as in a "pull" mode.
 * While the generator fills its buffer, it can detect, according to the song replay frequency, if new registers must be pulled from
 * the song.
 * 
 * We consider the output to be always 16 bits, stereo.
 * 
 * @author Julien Névo
 *
 */
public class AYBufferGenerator implements IAudioBufferGenerator {

	/** The Reader from which to get new registers when needed. */
	private ISongReader songReader;
	
	//private short currentFront = 32000;
	//private int currentStep = 0;

	private int replayFrequency;
	private int PSGFrequency;
	
	/** The sample rate at which the sound is played (44100, 22050 etc.) in Hz. 0 means that is has not been set. */
	private int sampleRate = 0;
	
	
	private final static int HARDWARE_CURVES_CYCLE_LENGTH = 16;					// Nb steps a cycle consists of.
	private final static int HARDWARE_CURVES_NB_CYCLES = 4;
	private final static int HARDWARE_CURVES_LENGTH = HARDWARE_CURVES_CYCLE_LENGTH * 4;
	private final static int HARDWARE_CURVES_LOOP_TO = HARDWARE_CURVES_NB_CYCLES / 2 * HARDWARE_CURVES_CYCLE_LENGTH;	// We always loop to the 2nd cycle.

	private final static int NB_NOISE_VALUES = 32;
	//private final static int NB_PSG_REGISTERS = 14;

	private final static double OUTPUT_VOLUME_RATE_STEREO_CHANNEL_AC = 0.687;
	private final static double OUTPUT_VOLUME_RATE_STEREO_CHANNEL_B = 0.313;
    private final static double OUTPUT_VOLUME_RATE_STEREO_CHANNEL_DIFFERENCE = OUTPUT_VOLUME_RATE_STEREO_CHANNEL_B / OUTPUT_VOLUME_RATE_STEREO_CHANNEL_AC;

	private static final int MFC_FREQUENCY = 2457600;
	
	/** Pre-divisor table used for samples. */
	private static int[] predivisorTable = new int[] { 0, 4, 10, 16, 50, 64, 100, 200 };
	
    private boolean isMixerSoundAOpen;									// True is Mixer bit to 0 ! Which means "open".
	private boolean isMixerSoundBOpen;
	private boolean isMixerSoundCOpen;
	private boolean isMixerNoiseAOpen;
	private boolean isMixerNoiseBOpen;
	private boolean isMixerNoiseCOpen;
	private boolean isMixerNoiseOnAnyChannel;
	private boolean isHardwareEnvelopeUsedOnA;
	private boolean isHardwareEnvelopeUsedOnB;
	private boolean isHardwareEnvelopeUsedOnC;
	
	private double periodRatio = 1;					// Calculated to convert any given Period into a period to output.
    private int playerPeriod;                       // Calculated to know exactly when to send the registers.
    private int playerPeriodCounter;     			// Counter increasing till the playerPeriod is reached.

	private int periodCounterA;						// Counters increasing till the Period has been reached.
	private int periodCounterB;
	private int periodCounterC;
	private int hardwarePeriodCounter;
	private int hardwareCurveCounter;				// Counter inside the hardware Curve. From 0 to 63. Loops at 32.

	private int noisePeriod;
	private int noisePeriodCounter;

	private boolean soundStateA = true;				// High state (true) or Low state (false), according only to the square signal being produced, not the mixer.
	private boolean soundStateB = true;
	private boolean soundStateC = true;

	private static int randomSeed = 0x12345678;
	private int volumeNoise = 0;					// Volume calculated randomly, used for the Noise. We keep it here to use it from a buffer to the next.

	private double stepCounter = 0;
	private double nextStepCounter = 0;				// Indicates what stepCounter will be on the next iteration.
	private double nextOutputVolumeASum = -1;		// Indicates if an iteration begins with a partial volume from the previous iteration. -1 is no.
    private double nextOutputVolumeBSum = -1;
    private double nextOutputVolumeCSum = -1;
	private boolean isNextOutputVolumeSum = false;	// Indicates if an iteration begins with a partial volume from the previous iteration.

	// Raw volumes level.
	private static int[] volumesBase = { 0, 231, 695, 1158, 2084, 2779, 4168, 6716, 8105, 13200, 18294, 24315, 32189, 40757, 52799, 65535 };
	// Volumes to be decreased according to the output (8/16 bits) but also the periodRatio, so that we don't have to divise the added "partial" volumes.
	private static double[] volumes;


	private static int[][] hardwareCurves;								// Points to an array of volumes, for one HardwareCurve given.

	// Conversion tables from VolumeBeforeNoise to VolumeAfterNoise, when noise=1, 2, 3, 4... Empirical tables, done according to signal recorded from hardware.
	private static int[][] noiseConverter;
	private static int[] noiseTable1 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
	private static int[] noiseTable2 = { 0, 0, 2, 2, 4, 4, 6, 6, 8, 8, 10, 10, 12, 12, 14, 15 };
	private static int[] noiseTable3 = { 0, 0, 0, 0, 7, 7, 7, 8, 8, 8, 9, 9, 9, 15, 15, 15 };
	private static int[] noiseTable4 = { 0, 0, 0, 0, 0, 8, 8, 8, 8, 8, 8, 15, 15, 15, 15, 15 };
	private static int[] noiseTableLast = { 0, 0, 0, 0, 0, 0, 8, 8, 8, 8, 15, 15, 15, 15, 15, 15 };

	
	
	public int periodA;
	public int periodB;
	public int periodC;
	public int noise;
	public int mixer;
	public int volumeA;
	public int volumeB;
	public int volumeC;
	public int hardwarePeriod;
	public int hardwareEnveloppe;			    // Hardware Envelope used by the Hardware volume. Value from 0 to 0xf. Never contains 0xff.


	private short[] sampleA;					// Pointers to the sample of channel A. Null means no sample is played.
	private short[] sampleB;					// This is how are detected that a sample must be played.
	private short[] sampleC;

	private int sampleAIndex;					// Index on the sample of channel A.
	private int sampleBIndex;
	private int sampleCIndex;

	private double sampleAStep;
	private double sampleBStep;
	private double sampleCStep;
	
	private int sampleALength;
	private int sampleBLength;
	private int sampleCLength;
	
	private int sampleAPeriod;					// Period of the samples. ?????????
	private int sampleBPeriod;
	private int sampleCPeriod;
	
	
	
	static {
		generateHardwareCurves();
		fillNoiseConverterTables();
	}

	
	// ***************************************
	// Public methods
	// ***************************************
	
	@Override
	public boolean isReady() {
		return ((songReader != null) && (sampleRate != 0));
	};
	
	
	@Override
	public void initialize(ISongReader songReader) {
		this.songReader = songReader;
		this.replayFrequency = songReader.getReplayFrequency();
		this.PSGFrequency = songReader.getPSGFrequency();

		periodRatio = ((double)(PSGFrequency)) / ((double)sampleRate) / 8.0;
		stepCounter = periodRatio;
		playerPeriod = (sampleRate / replayFrequency) - 1;
//        if (playerPeriod < 1) {
//        	playerPeriod = 1;
//         }
		
		// FIXME TRY
		periodRatio *= 1.5;			// Frequency increase.
		playerPeriod *= 0.65;		// Speed increase.
		
        playerPeriodCounter = playerPeriod + 1;	// Forces the getting of registers on first pass.
        
        generateVolumes();
        
        
        // FIXME Not sure.
//        sampleAStep = stepCounter;
//        sampleBStep = stepCounter;
//        sampleCStep = stepCounter;
	}


	@Override
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	
	@Override
	public void seek(int seconds) {
		if (songReader != null) {
			songReader.seek(seconds);
		}
		
	}

	
	// ***************************************
	// Private methods
	// ***************************************
	
	/**
	 * Generates the 16 levels of volumes, according to their base value, the output Bit Rate, the periodRatio and the maximum channel volume.
	 */
	private void generateVolumes() {
		int nbVolumes = volumesBase.length;
		volumes = new double[nbVolumes];

        double maxVolume = Short.MAX_VALUE; // (BitRate == 8 ? 127 : 32767);
        maxVolume *= OUTPUT_VOLUME_RATE_STEREO_CHANNEL_AC;  // The max volume is divised by the volume rate of the two louder channels (A and C).

		// Finds the ratio to convert the maximum volume value (= the last base volume) to 8 bits or 16 bits value.
		double bitRateRatio = volumesBase[nbVolumes - 1] / maxVolume;

		for (int i = 0; i < nbVolumes; ++i) {
			volumes[i] = (volumesBase[i] / bitRateRatio / periodRatio);
		}
		
	}
	

	// ***************************************
	// Static methods
	// ***************************************
	
	/**
	 * Fills the Noise conversion tables to their respective volume.
	 */
	private static void fillNoiseConverterTables() {
		// Links the Noise table conversion tables to their respective volume.
		noiseConverter = new int[NB_NOISE_VALUES][];
		noiseConverter[0] = noiseTable1;
		noiseConverter[1] = noiseTable1;
		noiseConverter[2] = noiseTable2;
		noiseConverter[3] = noiseTable3;
		noiseConverter[4] = noiseTable4;
		for (int i = 5; i < NB_NOISE_VALUES; i++) {
			noiseConverter[i] = noiseTableLast;
		}
	}

	/**
	 * Generates all the Hardware Curves. Doing it once is enough.
	 */
	private static void generateHardwareCurves() {
		if (hardwareCurves != null) {
			return;
		}
		
		hardwareCurves = new int[HARDWARE_CURVES_CYCLE_LENGTH][];

		for (int i = 0; i < HARDWARE_CURVES_CYCLE_LENGTH; ++i) {
			int[] curve = new int[HARDWARE_CURVES_LENGTH];

			boolean hold = ((i & 1) != 0);
			boolean alternate = ((i & 2) != 0);
			boolean attack = ((i & 4) != 0);
			boolean cont = ((i & 8) != 0);

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

			hardwareCurves[i] = curve;
		}
	}


	// ***************************************
	// Main generation method
	// ***************************************

	@Override
	public void generateAudioBuffer(short[] buffer) {
		
		if (songReader == null) {
			return;
		}
		
		int cvolumeA, cvolumeB, cvolumeC;
		double outputVolumeASum, outputVolumeBSum, outputVolumeCSum;
		double outputVolumeA, outputVolumeB, outputVolumeC;
		int bufferSize = buffer.length;
		
		// FIXME TEST.
//		if (true) {
//			return;
//		}

		for (int i = 0; i < bufferSize; ++i) {
			
			// Calculates if we need new PSG registers according to the replay frequency of the song.
			if (++playerPeriodCounter > playerPeriod) {
				short[] regs = songReader.getNextRegisters();
				interpretRegisters(regs);
				
				playerPeriodCounter = 0;
			}
			
			
			// Before beginning the iteration, we set the volume and step according the "previously unmanaged" volume and value.
			if (isNextOutputVolumeSum) {
				outputVolumeASum = nextOutputVolumeASum;		// Previous value stored. We use it.
				outputVolumeBSum = nextOutputVolumeBSum;
				outputVolumeCSum = nextOutputVolumeCSum;
				stepCounter = nextStepCounter;
				isNextOutputVolumeSum = false;
			} else {
				outputVolumeASum = 0;							// No previous value stored. We start from scratch.
				outputVolumeBSum = 0;
				outputVolumeCSum = 0;
				stepCounter = periodRatio;
			}

			while (stepCounter > 0) {
				// Manage Frequencies
				if (++periodCounterA >= periodA) {
					soundStateA = !soundStateA;
					periodCounterA = 0;
				}
				
				if (++periodCounterB >= periodB) {
					soundStateB = !soundStateB;
					periodCounterB = 0;
				}
				
				if (++periodCounterC >= periodC) {
					soundStateC = !soundStateC;
					periodCounterC = 0;
				}


				// Manage Hardware Curve on the background. Even if not used, still moving.
				if (++hardwarePeriodCounter >= hardwarePeriod) {
					hardwarePeriodCounter = 0;
					if (++hardwareCurveCounter > (HARDWARE_CURVES_LENGTH - 1)) {
						hardwareCurveCounter = HARDWARE_CURVES_LOOP_TO;
					}
				}


				// Manage Volume A.
				if (isHardwareEnvelopeUsedOnA) {				// Hardware volume ?
					// Hardware volume.
					if (isMixerSoundAOpen) {
						// Mixer is on. The volume depends on the High/Low shelf of the software envelope, but the high shelf is defined by the Hardware Curve.
						cvolumeA = (soundStateA ? hardwareCurves[hardwareEnveloppe][hardwareCurveCounter] : 0);
					} else {
						// Mixer is off. The volume is only given by the Hardware Curve. No more High/low shelf.
						cvolumeA = hardwareCurves[hardwareEnveloppe][hardwareCurveCounter];
					}
				} else {
					// Normal volume.
					// If Mixer is on, we manage the square wave volume according to the alternating sound state.
					if (isMixerSoundAOpen) {
						// Mixer On.
						cvolumeA = (soundStateA ? volumeA : 0);
					} else {
						// If Mixer is off, the volume is the only given, and doesn't move (useful for Noise with no sound, but also for samples (not used here) !)
						cvolumeA = volumeA;
					}
				}



				// Manage Volume B.
				if (isHardwareEnvelopeUsedOnB) {
					if (isMixerSoundBOpen) {
						cvolumeB = (soundStateB ? hardwareCurves[hardwareEnveloppe][hardwareCurveCounter] : 0);
					} else {
						cvolumeB = hardwareCurves[hardwareEnveloppe][hardwareCurveCounter];
					}
				} else {
					if (isMixerSoundBOpen) {
						cvolumeB = (soundStateB ? volumeB : 0);
					} else {
						cvolumeB = volumeB;
					}
				}



				// Manage Volume C.
				if (isHardwareEnvelopeUsedOnC) {
					if (isMixerSoundCOpen) {
						cvolumeC = (soundStateC ? hardwareCurves[hardwareEnveloppe][hardwareCurveCounter] : 0);
					} else {
						cvolumeC = hardwareCurves[hardwareEnveloppe][hardwareCurveCounter];
					}
				} else {
					if (isMixerSoundCOpen) {
						cvolumeC = (soundStateC ? volumeC : 0);
					} else {
						cvolumeC = volumeC;
					}
				}




				// Manage Noise, only if is used by a channel.
				if (isMixerNoiseOnAnyChannel) {
					if (++noisePeriodCounter >= noisePeriod) {
						noisePeriodCounter = 0;
						randomSeed = randomSeed ^ (randomSeed >> 7 ^ (randomSeed << 3)) + (int)outputVolumeASum;
						volumeNoise = noiseConverter[noise][randomSeed & 0xf];
					}
					
					// Noise on Channel A, B, C ?
					// FIXME could be better.
					if (isMixerNoiseAOpen) {
						cvolumeA = (volumeNoise > cvolumeA ? cvolumeA : volumeNoise);
					}
					
					if (isMixerNoiseBOpen) {
						cvolumeB = (volumeNoise > cvolumeB ? cvolumeB : volumeNoise);
					}
					
					if (isMixerNoiseCOpen) {
						cvolumeC = (volumeNoise > cvolumeC ? cvolumeC : volumeNoise);
					}
				}

				// Converts the 4-bit volume to output volume. Samples have priority.
				outputVolumeA = (sampleA == null ? volumes[cvolumeA] : volumes[sampleA[sampleAIndex]]);
				outputVolumeB = (sampleB == null ? volumes[cvolumeB] : volumes[sampleB[sampleBIndex]]);
				outputVolumeC = (sampleC == null ? volumes[cvolumeC] : volumes[sampleC[sampleCIndex]]);

				// Adds the volume found to the sum. If the step is inside the "integer" part of the Steps, the full value written.
				// Else, only a "decimal" of it.
				if (stepCounter >= 1) {
					outputVolumeASum += outputVolumeA;
					outputVolumeBSum += outputVolumeB;
					outputVolumeCSum += outputVolumeC;
					stepCounter--;
				} else {
					// A "partial" volume is added. It is the end of the iteration.
					outputVolumeASum += outputVolumeA * stepCounter;
					outputVolumeBSum += outputVolumeB * stepCounter;
					outputVolumeCSum += outputVolumeC * stepCounter;
					
					// We have to remember that we have to write the remaining part of the volume !
					double partialStepCounter = 1 - stepCounter;
					nextStepCounter = periodRatio - partialStepCounter;
                    nextOutputVolumeASum = outputVolumeA * partialStepCounter;
					nextOutputVolumeBSum = outputVolumeB * partialStepCounter;
					nextOutputVolumeCSum = outputVolumeC * partialStepCounter;

					isNextOutputVolumeSum = true;
					stepCounter = 0;
					
				}
				
			}
			
			// Make the index move forward inside the samples.
			// FIXME improve the calculus, just a try for now. Use the sample frequency (MFC).
			if ((sampleA != null) && (++sampleAStep > 5)) {
				sampleAStep = 0;
				if (++sampleAIndex >= sampleALength) {
					sampleA = null;
				}
			}
			if ((sampleB != null) && (++sampleBStep > 5)) {
				sampleBStep = 0;
				if (++sampleBIndex >= sampleBLength) {
					sampleB = null;
				}
			}
			if ((sampleC != null) && (++sampleCStep > 5)) {
				sampleCStep = 0;
				if (++sampleCIndex >= sampleCLength) {
					sampleC = null;
				}
			}
		
			// FIXME HACK because the volume seems no more related to the cursor !
			outputVolumeASum *= 0.1;
			outputVolumeBSum *= 0.1;
			outputVolumeCSum *= 0.1;

            // Write the output data in the buffer, as 16 bits stereo values.
			buffer[i++] = (short)(outputVolumeASum + OUTPUT_VOLUME_RATE_STEREO_CHANNEL_DIFFERENCE * outputVolumeBSum);
			buffer[i++] = (short)(OUTPUT_VOLUME_RATE_STEREO_CHANNEL_DIFFERENCE * outputVolumeBSum + outputVolumeCSum);
		}
	}
	
	/**
	 * Interprets the given registers to fill usable variables.
	 * @param regs the PSG registers to interpret.
	 */
	private void interpretRegisters(short[] regs) {
		int r3 = regs[3];
		int r8 = regs[8];
		int r9 = regs[9];
		int r10 = regs[10];
		
		periodA = (regs[0] + ((regs[1] & 0xf) << 8));
		periodB = (regs[2] + ((r3 & 0xf) << 8));
		periodC = (regs[4] + ((regs[5] & 0xf) << 8));

		noise = (regs[6] & 0x1f);
		noisePeriod = noise << 1;

		mixer = regs[7];
		isMixerSoundAOpen = ((mixer & 0x1) == 0);
		isMixerSoundBOpen = ((mixer & 0x2) == 0);
		isMixerSoundCOpen = ((mixer & 0x4) == 0);
		isMixerNoiseAOpen = ((mixer & 0x8) == 0);
		isMixerNoiseBOpen = ((mixer & 0x10) == 0);
		isMixerNoiseCOpen = ((mixer & 0x20) == 0);
		isMixerNoiseOnAnyChannel = isMixerNoiseAOpen | isMixerNoiseBOpen | isMixerNoiseCOpen;
		
		volumeA = (r8 & 0x1f);
		volumeB = (r9 & 0x1f);
		volumeC = (r10 & 0x1f);
		hardwarePeriod = (regs[11] + (regs[12] << 8)) << 1;
		
		// Samples ? Detected thanks to bit 5-4 of r3.
		int r3sample = (r3 & 0x30);
		if (r3sample != 0) {
			// SampleFrequency = MFC_FREQUENCY / TP / TC
			// TP = R8 (b7-b5) -> through prediv table.
			// TC = R15.
			int timerPredivisor = predivisorTable[(r8 >>> 5)];
			int sampleFrequency = (int)(((float)MFC_FREQUENCY / timerPredivisor) / regs[15]);
			
			switch (r3sample) {
			case 0x10:
				sampleA = songReader.getSample(r8);
				if (sampleA != null) {								// Some buggy YM (LedStorm2) may use this value without
					sampleALength = sampleA.length;					// actually having any samples.
					sampleAIndex = 0;
					sampleAPeriod = sampleFrequency;
				}
				break;
			case 0x20:
				sampleB = songReader.getSample(r9);
				if (sampleB != null) {
					sampleBLength = sampleB.length;
					sampleBIndex = 0;
					sampleBPeriod = sampleFrequency;
				}
				break;
			case 0x30:
				sampleC = songReader.getSample(r10);
				if (sampleC != null) {
					sampleCLength = sampleC.length;
					sampleCIndex = 0;
					sampleCPeriod = sampleFrequency;
				}
				break;
			}
			
			
		}

		isHardwareEnvelopeUsedOnA = ((volumeA & 0x10) > 0);
		isHardwareEnvelopeUsedOnB = ((volumeB & 0x10) > 0);
		isHardwareEnvelopeUsedOnC = ((volumeC & 0x10) > 0);

		// If Given R13 equals 0xff, nothing is done. Else, reset the Envelope counter.
		int tempHardwareEnveloppe = regs[13];
		if (tempHardwareEnveloppe != 0xff)
		{
			hardwareEnveloppe = tempHardwareEnveloppe & 0xf;
			hardwarePeriodCounter = 0;
            hardwareCurveCounter = 0;
		}
	}



}
