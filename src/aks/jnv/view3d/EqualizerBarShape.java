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

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * 3D Shape of an equalizer bar. It can be for either a volume or a noise. 
 * 
 * @author Julien Nevo
 *
 */
public class EqualizerBarShape implements IShape3d {

	/** The width of the bar. */
	public final static float BAR_WIDTH = 0.4f;
	/** The depth of the bar. */
	public final static float BAR_DEPTH = BAR_WIDTH * 10;
	/** The height of one volume unit. */
	private static final float ONE_VOLUME_HEIGHT = BAR_WIDTH / 4;
	/** The minimum height a bar can have. */
	private static final float MINIMUM_HEIGHT = 0.01f;

	/** The number of vertices. */
	private int mVerticesCount = 0;
	
	/** The buffer of the vertices. */
	private FloatBuffer mVertexBuffer;
	/** The buffer of the indices of the vertices to draw. */
	private ShortBuffer mIndexBuffer;
	/** The buffer of the colors of each vertices. */
	private FloatBuffer mColorBuffer;
	/** The buffer of the colors of each vertices for the hardware envelope. */
	private FloatBuffer mColorHardEnvelopeBuffer;

	/** The X position of the object. */
	private float mPositionX;
	/** The Y position of the object. */
	private float mPositionY;
	/** The Z position of the object. */
	private float mPositionZ;

	/** The value to be shown by the bar. */
	private int mValue;
	
	/** Indicates ifthe bar is a volume bar or a noise bar.*/
	private boolean mIsVolumeBar;

//	private float angleX;	//FIXME REMOVE ME TRY.
//	private float angleY;

	/** Red component of the color of the bar. */
	public final static float COLOR_R_VOLUME_BAR = 0.0f;
	/** Green component of the color of the bar. */
	public final static float COLOR_G_VOLUME_BAR = 1.0f;
	/** Blue component of the color of the bar. */
	public final static float COLOR_B_VOLUME_BAR = 0.0f;
	/** Alpha component of the color of the bar. */
	public final static float COLOR_A_VOLUME_BAR = 1.0f;
	
	/** Red component of the color of the bar. */
	public final static float COLOR_R_VOLUME_BAR2 = 0.0f;
	/** Green component of the color of the bar. */
	public final static float COLOR_G_VOLUME_BAR2 = 1.0f;
	/** Blue component of the color of the bar. */
	public final static float COLOR_B_VOLUME_BAR2 = 1.0f;
	/** Alpha component of the color of the bar. */
	public final static float COLOR_A_VOLUME_BAR2 = 1.0f;
	
	
	/** Red component of the color of the bar. */
	public final static float COLOR_R_NOISE_BAR = 1.0f;
	/** Green component of the color of the bar. */
	public final static float COLOR_G_NOISE_BAR = 0.0f;
	/** Blue component of the color of the bar. */
	public final static float COLOR_B_NOISE_BAR = 0.0f;
	/** Alpha component of the color of the bar. */
	public final static float COLOR_A_NOISE_BAR = 1.0f;
	
	/** Red component of the color of the bar. */
	public final static float COLOR_R_NOISE_BAR2 = 1.0f;
	/** Green component of the color of the bar. */
	public final static float COLOR_G_NOISE_BAR2 = 1.0f;
	/** Blue component of the color of the bar. */
	public final static float COLOR_B_NOISE_BAR2 = 1.0f;
	/** Alpha component of the color of the bar. */
	public final static float COLOR_A_NOISE_BAR2 = 1.0f;

	/** Red component of the color of the bar. */
	public final static float COLOR_R_HARD_ENVELOPE_BAR = 1.0f;
	/** Green component of the color of the bar. */
	public final static float COLOR_G_HARD_ENVELOPE_BAR = 1.0f;
	/** Blue component of the color of the bar. */
	public final static float COLOR_B_HARD_ENVELOPE_BAR = 0.0f;
	/** Alpha component of the color of the bar. */
	public final static float COLOR_A_HARD_ENVELOPE_BAR = 1.0f;
	
	/** Red component of the color of the bar. */
	public final static float COLOR_R_HARD_ENVELOPE_BAR2 = 1.0f;
	/** Green component of the color of the bar. */
	public final static float COLOR_G_HARD_ENVELOPE_BAR2 = 1.0f;
	/** Blue component of the color of the bar. */
	public final static float COLOR_B_HARD_ENVELOPE_BAR2 = 1.0f;
	/** Alpha component of the color of the bar. */
	public final static float COLOR_A_HARD_ENVELOPE_BAR2 = 1.0f;
	
	/**
	 * Constructor of the EqualizerBarShape.
	 * @param x the X position.
	 * @param y the Y position.
	 * @param z the Z position.
	 * @param isVolumeBar true if the bar is about volume, false for a noise bar.
	 */
	public EqualizerBarShape(float x, float y, float z, boolean isVolumeBar) {
		mIsVolumeBar = isVolumeBar;
		setPosition(x, y, z);
		
		float[] vertices = new float[] {
				0, ONE_VOLUME_HEIGHT, BAR_DEPTH,			// 0 front top left
				0, 0, BAR_DEPTH,							// 1 front bottom left
				BAR_WIDTH, 0, BAR_DEPTH,					// 2 front bottom right
				BAR_WIDTH, ONE_VOLUME_HEIGHT, BAR_DEPTH,	// 3 front top right
				
				0, ONE_VOLUME_HEIGHT, 0,					// 4 back top left
				0, 0, 0,									// 5 back bottom left
				BAR_WIDTH, 0, 0,							// 6 back bottom right
				BAR_WIDTH, ONE_VOLUME_HEIGHT, 0,			// 7 back top right
			};
		
		short[] indices = {
				0,1,2,		// Front triangles
				2,3,0,
				2,7,3,		// Right triangles
				2,6,7,
				1,0,4,		// Left triangles
				1,4,5,
				0,7,4,		// Top triangles
				0,3,7,
				1,5,2,		// Bottom triangles
				2,5,6,
				5,4,6,		// Back triangles
				6,4,7
			};
			
		float[] colors;
		
		if (isVolumeBar) {
			colors = new float[] {
					COLOR_R_VOLUME_BAR, COLOR_G_VOLUME_BAR, COLOR_B_VOLUME_BAR, COLOR_A_VOLUME_BAR,
					COLOR_R_VOLUME_BAR, COLOR_G_VOLUME_BAR, COLOR_B_VOLUME_BAR, COLOR_A_VOLUME_BAR,
					COLOR_R_VOLUME_BAR, COLOR_G_VOLUME_BAR, COLOR_B_VOLUME_BAR, COLOR_A_VOLUME_BAR,
					COLOR_R_VOLUME_BAR, COLOR_G_VOLUME_BAR, COLOR_B_VOLUME_BAR, COLOR_A_VOLUME_BAR,
					
					COLOR_R_VOLUME_BAR2, COLOR_G_VOLUME_BAR2, COLOR_B_VOLUME_BAR2, COLOR_A_VOLUME_BAR2,
					COLOR_R_VOLUME_BAR2, COLOR_G_VOLUME_BAR2, COLOR_B_VOLUME_BAR2, COLOR_A_VOLUME_BAR2,
					COLOR_R_VOLUME_BAR2, COLOR_G_VOLUME_BAR2, COLOR_B_VOLUME_BAR2, COLOR_A_VOLUME_BAR2,
					COLOR_R_VOLUME_BAR2, COLOR_G_VOLUME_BAR2, COLOR_B_VOLUME_BAR2, COLOR_A_VOLUME_BAR2,
			};
		} else {
			colors = new float[] {
					COLOR_R_NOISE_BAR, COLOR_G_NOISE_BAR, COLOR_B_NOISE_BAR, COLOR_A_NOISE_BAR,
					COLOR_R_NOISE_BAR, COLOR_G_NOISE_BAR, COLOR_B_NOISE_BAR, COLOR_A_NOISE_BAR,
					COLOR_R_NOISE_BAR, COLOR_G_NOISE_BAR, COLOR_B_NOISE_BAR, COLOR_A_NOISE_BAR,
					COLOR_R_NOISE_BAR, COLOR_G_NOISE_BAR, COLOR_B_NOISE_BAR, COLOR_A_NOISE_BAR,
					
					COLOR_R_NOISE_BAR2, COLOR_G_NOISE_BAR2, COLOR_B_NOISE_BAR2, COLOR_A_NOISE_BAR2,
					COLOR_R_NOISE_BAR2, COLOR_G_NOISE_BAR2, COLOR_B_NOISE_BAR2, COLOR_A_NOISE_BAR2,
					COLOR_R_NOISE_BAR2, COLOR_G_NOISE_BAR2, COLOR_B_NOISE_BAR2, COLOR_A_NOISE_BAR2,
					COLOR_R_NOISE_BAR2, COLOR_G_NOISE_BAR2, COLOR_B_NOISE_BAR2, COLOR_A_NOISE_BAR2,
			};
		}
		
		float[] colorsHardEnvelope = new float[] {
				COLOR_R_HARD_ENVELOPE_BAR, COLOR_G_HARD_ENVELOPE_BAR, COLOR_B_HARD_ENVELOPE_BAR, COLOR_A_HARD_ENVELOPE_BAR,
				COLOR_R_HARD_ENVELOPE_BAR, COLOR_G_HARD_ENVELOPE_BAR, COLOR_B_HARD_ENVELOPE_BAR, COLOR_A_HARD_ENVELOPE_BAR,
				COLOR_R_HARD_ENVELOPE_BAR, COLOR_G_HARD_ENVELOPE_BAR, COLOR_B_HARD_ENVELOPE_BAR, COLOR_A_HARD_ENVELOPE_BAR,
				COLOR_R_HARD_ENVELOPE_BAR, COLOR_G_HARD_ENVELOPE_BAR, COLOR_B_HARD_ENVELOPE_BAR, COLOR_A_HARD_ENVELOPE_BAR,
				
				COLOR_R_HARD_ENVELOPE_BAR2, COLOR_G_HARD_ENVELOPE_BAR2, COLOR_B_HARD_ENVELOPE_BAR2, COLOR_A_HARD_ENVELOPE_BAR2,
				COLOR_R_HARD_ENVELOPE_BAR2, COLOR_G_HARD_ENVELOPE_BAR2, COLOR_B_HARD_ENVELOPE_BAR2, COLOR_A_HARD_ENVELOPE_BAR2,
				COLOR_R_HARD_ENVELOPE_BAR2, COLOR_G_HARD_ENVELOPE_BAR2, COLOR_B_HARD_ENVELOPE_BAR2, COLOR_A_HARD_ENVELOPE_BAR2,
				COLOR_R_HARD_ENVELOPE_BAR2, COLOR_G_HARD_ENVELOPE_BAR2, COLOR_B_HARD_ENVELOPE_BAR2, COLOR_A_HARD_ENVELOPE_BAR2,
		};
			
		mVerticesCount = indices.length;
		mVertexBuffer = GLBuffersUtils.getVertexBuffer(vertices);
		mIndexBuffer = GLBuffersUtils.getIndexBuffer(indices);
		mColorBuffer = GLBuffersUtils.getColorBuffer(colors);
		mColorHardEnvelopeBuffer = GLBuffersUtils.getColorBuffer(colorsHardEnvelope);
	}
	
	/**
	 * Sets the value to be displayed.
	 * @param value the value to be displayed.
	 */
	public void setValue(int value) {
		mValue = value;
	}
	
	
	@Override
	public void setPosition(float x, float y, float z) {
		mPositionX = x;
		mPositionY = y;
		mPositionZ = z;
	}
	
	@Override
	public void draw(GL10 gl) {
		
//		angleX += 1;
//		angleY += 0.5;
		//angleZ += 2;
		
		//positionZ -= 0.02;
		
		int maxValue = mIsVolumeBar ? 15 : 31;
		
		// Calculates the height according to the volume.
		boolean isHardEnvelope = false;
		float heightRatio;
		if (mValue <= 0) {
			heightRatio = MINIMUM_HEIGHT;
		} else {
			if (mValue <= maxValue) { 
				heightRatio = mValue;
			} else {
				isHardEnvelope = mIsVolumeBar;	// The hardware envelope flag can only be used for a volume bar.
				heightRatio = maxValue;
			}
			
			// Noise has twice more value. We need to shrink it.
			if (!mIsVolumeBar) {
				heightRatio /= 2;
			//heightRatio = 15;
			}
		}
		
		gl.glTranslatef(mPositionX, mPositionY, mPositionZ);

		gl.glScalef(1, 1 * heightRatio, 1);
		
//		gl.glRotatef(angleX, 1, 0, 0);
//		gl.glRotatef(angleY, 0, 1, 0);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		// Uses the color buffer according to whether the value is "normal" or represents a hardware envelope.
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, isHardEnvelope ? mColorHardEnvelopeBuffer : mColorBuffer);
		
		// Draw the vertices.
		gl.glDrawElements(GL10.GL_TRIANGLES, mVerticesCount, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
		
//		gl.glRotatef(-angleY, 0, 1, 0);
		//gl.glRotatef(-angleX, 1, 0, 0);
		
		gl.glScalef(1, 1 / heightRatio, 1);
		
		gl.glTranslatef(-mPositionX, -mPositionY, -mPositionZ);
		
	}


}
