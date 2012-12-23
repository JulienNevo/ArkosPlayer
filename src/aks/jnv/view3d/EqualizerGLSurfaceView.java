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

import aks.jnv.accelerometer.IAccelerometerListener;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * GLSurface View used to display equalizers of the played song.
 *
 * It is very important to use the 2-argument constructor, as it is this one that is used by the system, as this view is declared in an XML layout file.
 * 
 * @author Julien Névo.
 *
 */
public class EqualizerGLSurfaceView extends GLSurfaceView implements IAccelerometerListener {

	/** The renderer. */
	private EqualizerGLSurfaceRenderer mRenderer;
	
	/**
	 * Constructor of the GLSurfaceView. MUST be used in favor of the one-argument constructor,
	 * as it is this one that is used by the system, as this view is declared in an XML layout file.
	 * @param context a Context.
	 * @param attrs the attributes.
	 */
	public EqualizerGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Sets the renderer to use.
		mRenderer = new EqualizerGLSurfaceRenderer();
		setRenderer(mRenderer);
	}

	/**
	 * Sets the equalizer values.
	 * @param channel1Volume The volume for the channel 1.
	 * @param channel2Volume The volume for the channel 2.
	 * @param channel3Volume The volume for the channel 3.
	 * @param noise The noise.
	 */
	public void setEqualizerValues(int channel1Volume, int channel2Volume, int channel3Volume, int noise) {
		mRenderer.setEqualizerValues(channel1Volume, channel2Volume, channel3Volume, noise);
	}

	@Override
	public void onAccelerationChanged(float x, float y, float z) {
		mRenderer.setAccelerometerValue(x, y, z);		
	}
}
