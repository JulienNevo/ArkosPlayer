package aks.jnv.view3d;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import aks.jnv.reader.ISongReader;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

/**
 * The Renderer used to display the equalizer of the played song.
 * 
 * @author Julien Névo
 *
 */
public class EqualizerGLSurfaceRenderer implements Renderer {

	/** Count of the Equalizer bar. 3 plus one for the noise. */
	private static final int EQUALIZER_BAR_COUNT = 4;
	
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
	private int viewPortWidth = 1;
	/** The view height. */
	private int viewPortHeight = 1;
	
	/** Indicates if the shapes have been initialized. */
	private boolean areShapesInitialized = false;
	
	/** Array of the equalizer bar shapes. */
	private IShape3d[] equalizerBarShapes;
	
	/** Provider of information about the sound produces, in order to display the equalizer. */
	private ISongReader songReader;

	/** The accelerometer value in X. */
	private float accelerometerValueX;
	/** The accelerometer value in Y. */
	private float accelerometerValueY;
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		viewPortWidth = width;
		viewPortHeight = height;
		
		// TODO : calculates the size of the bar according to the screen size.
		
		// Called on the first time.
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, NEAR_CLIPPING, FAR_CLIPPING);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	/**
	 * Sets the SongReader in order to get the information for the equalizer.
	 * @param songReader the SongReader.
	 */
	public void setSongReader(ISongReader songReader) {
		this.songReader = songReader;
	}
	
	/**
	 * Initializes the shapes. Must be done once.
	 */
	private void initializeShapes() {
		if (areShapesInitialized) {
			return;
		}
		
		areShapesInitialized = true;
		
		// Creates and initializes the bars.
		equalizerBarShapes = new EqualizerBarShape[EQUALIZER_BAR_COUNT];
		float barPositionX = EQUALIZER_BAR_INITIAL_X;
		float barPositionY = EQUALIZER_BAR_INITIAL_Y;
		float barPositionZ = EQUALIZER_BAR_INITIAL_Z;
		for (int i = 0; i < EQUALIZER_BAR_COUNT; i++) {
			// Sets the bars as volume bars, except the last one.
			equalizerBarShapes[i] = new EqualizerBarShape(barPositionX, barPositionY, barPositionZ, i < (EQUALIZER_BAR_COUNT - 1));
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
		
		if (!areShapesInitialized) {
			return;
		}
		
		gl.glLoadIdentity();
		
		gl.glTranslatef(-accelerometerValueX / 6, 1, accelerometerValueY - 8.5f);
		
		
		
		// Gets the information for the equalizers.
		if (songReader != null) {
			// Reads the volume.
			for (int channel = 1; channel <= 3; channel++) {
				int volume = songReader.getVolumeChannel(channel);
				((EqualizerBarShape)equalizerBarShapes[channel - 1]).setValue(volume);
			}
			
			// Reads the noise for the last bar. If no channel use the noise, we set its value to 0.
			boolean isNoiseUsed = songReader.getNoiseChannels() != 0;
			int noise = isNoiseUsed ? songReader.getNoiseValue() : 0;
			((EqualizerBarShape)equalizerBarShapes[equalizerBarShapes.length - 1]).setValue(noise);
		}
		
		// Displays the Equalizer Bars.
		for (IShape3d shape : equalizerBarShapes) {
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
		accelerometerValueX = x;
		accelerometerValueY = y;
	}



}
