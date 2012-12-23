/*
 * Copyright (c) 2012 Julien Névo. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *  * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *  * The names of the authors or their institutions shall not
 * be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package aks.jnv.view3d;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

/**
 * The Renderer used to display the equalizer of the played song.
 * 
 * @author Julien Névo
 *
 */
public class EqualizerGLSurfaceRenderer implements Renderer {

	/** The Log tag of this class. */
	//private static final String LOG_TAG = EqualizerGLSurfaceRenderer.class.getSimpleName();
	
	/** Count of the channels. */
	private static final int CHANNEL_COUNT = 3;
	/** Count of the Equalizer bar. Channel count plus one for the noise. */
	private static final int EQUALIZER_BAR_COUNT = CHANNEL_COUNT + 1;
	
	/** The far clipping value. */
	private static final float FAR_CLIPPING = 100.0f;
	/** The near clipping value. */
	private static final float NEAR_CLIPPING = 0.01f;
	
	/** The background red value. */
	private static final float BACKGROUND_RED = 0.2f;
	/** The background green value. */
	private static final float BACKGROUND_GREEN = 0.3f;
	/** The background blue value. */
	private static final float BACKGROUND_BLUE = 0.4f;

	/** The position in X of the first equalizer bar. */
	private static final float EQUALIZER_BAR_INITIAL_X = -1f;
	/** The position in Y of the first equalizer bar. */
	private static final float EQUALIZER_BAR_INITIAL_Y = -2; //1;
	/** The position in Z of the first equalizer bar. */
	private static final float EQUALIZER_BAR_INITIAL_Z = -7;
	/** The width in X between two equalizer bars. */
	private static final float EQUALIZER_BAR_X_SEPARATION = EqualizerBarShape.BAR_WIDTH + EqualizerBarShape.BAR_WIDTH * 0.5f;

	/** The view width. */
	//private int viewPortWidth = 1;
	/** The view height. */
	//private int viewPortHeight = 1;
	
	/** Indicates if the shapes have been initialized. */
	private boolean mAreShapesInitialized = false;
	
	/** Array of the equalizer bar shapes. */
	private IShape3d[] mEqualizerBarShapes;
	
	/** Provider of information about the sound produces, in order to display the equalizer. */
	//private ISongReader mSongReader;

	/** The accelerometer value in X. */
	private float mAccelerometerValueX;
	/** The accelerometer value in Y. */
	private float mAccelerometerValueY;
	
	/** The volume of the channels. */
	private int[] mVolumes = new int[CHANNEL_COUNT];
	/** The noise value. */
	private int mNoise;
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		//viewPortWidth = width;
		//viewPortHeight = height;
		
		// TODO : calculates the size of the bar according to the screen size.
		
		// Called on the first time.
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, NEAR_CLIPPING, FAR_CLIPPING);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
//	/**
//	 * Sets the SongReader in order to get the information for the equalizer.
//	 * @param songReader the SongReader.
//	 */
//	public void setSongReader(ISongReader songReader) {
//		mSongReader = songReader;
//	}
	
	/**
	 * Initializes the shapes. Must be done once.
	 */
	private void initializeShapes() {
		if (mAreShapesInitialized) {
			return;
		}
		
		mAreShapesInitialized = true;
		
		// Creates and initializes the bars.
		mEqualizerBarShapes = new EqualizerBarShape[EQUALIZER_BAR_COUNT];
		float barPositionX = EQUALIZER_BAR_INITIAL_X;
		float barPositionY = EQUALIZER_BAR_INITIAL_Y;
		float barPositionZ = EQUALIZER_BAR_INITIAL_Z;
		for (int i = 0; i < EQUALIZER_BAR_COUNT; i++) {
			// Sets the bars as volume bars, except the last one.
			mEqualizerBarShapes[i] = new EqualizerBarShape(barPositionX, barPositionY, barPositionZ, i < (EQUALIZER_BAR_COUNT - 1));
			barPositionX += EQUALIZER_BAR_X_SEPARATION;
		}
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Preparation of the scene.
		gl.glMatrixMode(GL10.GL_PROJECTION);
//		float size = 0.01f * (float)Math.tan(Math.toRadians(45.0f) / 2);
//		float ratio = viewPortWidth / viewPortHeight;
		//gl.glFrustumf(-size, size, -size / ratio, size / ratio, NEAR_CLIPPING, FAR_CLIPPING);
		

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glFrontFace(GL10.GL_CCW);	// The CCW face is shown.
		gl.glCullFace(GL10.GL_BACK);	// The other is not shown.
		
		// Enable the elements to be rendered.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		initializeShapes();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClearColor(BACKGROUND_RED, BACKGROUND_GREEN, BACKGROUND_BLUE, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		if (!mAreShapesInitialized) {
			return;
		}
		
		gl.glLoadIdentity();
		
		gl.glTranslatef(-mAccelerometerValueX / 6, 1, mAccelerometerValueY - 8.5f);
		
		
		
		// Gets the information for the equalizers.
		// Reads the volume.
		for (int channel = 0; channel < CHANNEL_COUNT; channel++) {
			int volume = mVolumes[channel];
			((EqualizerBarShape)mEqualizerBarShapes[channel]).setValue(volume);
		}
		
		// Reads the noise for the last bar. If no channel use the noise, we set its value to 0.
		((EqualizerBarShape)mEqualizerBarShapes[mEqualizerBarShapes.length - 1]).setValue(mNoise);
		
		// Displays the Equalizer Bars.
		for (IShape3d shape : mEqualizerBarShapes) {
			shape.draw(gl);
		}
	}

	/**
	 * Sets the Accelerometer values.
	 * @param x the X value.
	 * @param y the Y value.
	 * @param z the Z value.
	 */
	public void setAccelerometerValue(float x, float y, float z) {
		mAccelerometerValueX = x;
		mAccelerometerValueY = y;
	}

	/**
	 * Sets the equalizer values.
	 * @param channel1Volume The volume for the channel 1.
	 * @param channel2Volume The volume for the channel 2.
	 * @param channel3Volume The volume for the channel 3.
	 * @param noise The noise.
	 */
	public void setEqualizerValues(int channel1Volume, int channel2Volume, int channel3Volume, int noise) {
		mVolumes[0] = channel1Volume;
		mVolumes[1] = channel2Volume;
		mVolumes[2] = channel3Volume;
		mNoise = noise;
	}



}
