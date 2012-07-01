/*
 * AYGenerator.h
 *
 *  Created on: Jun 30, 2012
 *      Author: julien
 */

#ifndef AYGENERATOR_H_
#define AYGENERATOR_H_

void generateHardwareCurves();
void fillNoiseConverterTables();

/**
 * Generates the 16 levels of volumes, according to their base value, the output Bit Rate, the periodRatio and the maximum channel volume.
 */
void generateVolumes();

void generateAudioBuffer(JNIEnv* env, jobject thiz, short* buffer, int bufferSize);
void interpretRegisters(short* regs);

//class AYGeneratorJNI {
//
//};

#endif /* AYGENERATOR_H_ */
