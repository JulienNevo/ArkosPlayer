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
