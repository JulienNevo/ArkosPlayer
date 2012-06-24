package aks.jnv.view3d;

import aks.jnv.accelerometer.IAccelerometerListener;
import aks.jnv.reader.ISongReader;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * GLSurface View used to display equalizers of the played song.
 *
 * It is very important to use the 2-argument constructor, as it is this one that is used by the system, as this view is declared in an XML layout file.
 * 
 * @author Julien Nevo.
 *
 */
public class EqualizerGLSurfaceView extends GLSurfaceView implements IAccelerometerListener {

	/** The renderer. */
	private EqualizerGLSurfaceRenderer renderer;
	
	/**
	 * Constructor of the GLSurfaceView. MUST be used in favor of the one-argument constructor,
	 * as it is this one that is used by the system, as this view is declared in an XML layout file.
	 * @param context a Context.
	 * @param attrs the attributes.
	 */
	public EqualizerGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Sets the renderer to use.
		renderer = new EqualizerGLSurfaceRenderer();
		setRenderer(renderer);
	}

	/**
	 * Sets the SongReader in order to get the information for the equalizer.
	 * @param songReader the SongReader.
	 */
	public void setSongReader(ISongReader songReader) {
		renderer.setSongReader(songReader);
	}

	@Override
	public void onAccelerationChanged(float x, float y, float z) {
		renderer.setAccelerometerValue(x, y, z);		
	}
}
