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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Helper class that takes care of creating the vertex, index and color buffers from the arrays given.
 * 
 * @author Julien Névo
 */
public class GLBuffersUtils {
	
	/**
	 * Returns the Vertex buffer from the given vertices.
	 * @param vertices the vertices.
	 * @return a FloatBuffer containing the Vertex buffer.
	 */
	public static FloatBuffer getVertexBuffer(float[] vertices) {
		// FIXME 3 or 4???
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4); // Each vertex has 3 coordinates.
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer vertexBuffer = vbb.asFloatBuffer();
		
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		return vertexBuffer;
	}

	/**
	 * Returns the Index buffer from the given vertices.
	 * @param indices the indices.
	 * @return a FloatBuffer containing the Index buffer.
	 */
	public static ShortBuffer getIndexBuffer(short[] indices) {
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		ShortBuffer indexBuffer = ibb.asShortBuffer();
		
		indexBuffer.put(indices);
		indexBuffer.position(0);
		
		return indexBuffer;
	}
	
	/**
	 * Returns the Color buffer from the given vertices.
	 * @param vertices the vertices.
	 * @return a FloatBuffer containing the Color buffer.
	 */
	public static FloatBuffer getColorBuffer(float[] colors) {
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4); // Each vertex has 4 color components.
		cbb.order(ByteOrder.nativeOrder());
		FloatBuffer colorBuffer = cbb.asFloatBuffer();
		
		colorBuffer.put(colors);
		colorBuffer.position(0);
		
		return colorBuffer;
	}
}
