package aks.jnv.view3d;

import javax.microedition.khronos.opengles.GL10;

public interface IShape3d {

	/**
	 * Draws the object.
	 * @param gl the OpenGL context.
	 */
	public void draw(GL10 gl);

	/**
	 * Sets the position of the object.
	 * @param x the x coordinate of the object.
	 * @param y the y coordinate of the object.
	 * @param z the z coordinate of the object.
	 */
	void setPosition(float x, float y, float z);

}
