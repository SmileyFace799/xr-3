package no.smileyface.xr3;

import com.jogamp.opengl.util.FPSAnimator;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*; // Classes JFrame og JPanel

import static com.jogamp.opengl.GL2.*;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;
import java.awt.Dimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main extends GLCanvas implements GLEventListener{
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	private static final float CAMERA_DISTANCE = 20;
	private static final int CUBE_SIZE = 5;

    	/* The interface GLEventListener contains the 5 methods that have to be implemented:
                - display(GLDrawable drawable)
	          	Called by the drawable to initiate OpenGL rendering by the client
                - displayChanged(GLDrawable drawable, boolean modeChanged, boolean deviceChanged)
	          	Called by the drawable when the display mode or the display device associated with the GLDrawable has changed.
                - init(GLDrawable drawable)
	          	Called by the drawable immediately after the OpenGL context is initialized.
                - reshape(GLDrawable drawable, int x, int y, int width, int height)
	          	Called by the drawable during the first repaint after the component has been resized.
                -dispose(GLAutoDrawable d)
      */

	private final transient GLU glu;
	private final transient Cube cubeModel;
	private final transient Random random;

	private transient RotationInfo rotationInfo;
	private double rotationAngle;

	/**
	 * Constructor.
	 */
	public Main(GLCapabilities c) {
		super(c);
		this.glu = new GLU();
		this.cubeModel = new Cube(CUBE_SIZE);
		this.random = new Random();

		this.rotationInfo = null;
		this.rotationAngle = 0;
		this.addGLEventListener(this);

		ScheduledExecutorService e = Executors.newSingleThreadScheduledExecutor();
		e.scheduleAtFixedRate(() -> {
			synchronized (this) {
				rotationInfo = new RotationInfo(Cube.RotationAxis.values()[random.nextInt(Cube.RotationAxis.values().length)], random.nextInt(CUBE_SIZE), random.nextInt(2) == 1);
				cubeModel.rotate(rotationInfo.axis(), rotationInfo.slice(), rotationInfo.clockwise());
				rotationAngle = rotationInfo.clockwise() ? 90 : -90;
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	public void init(GLAutoDrawable glDrawable) {
		LOGGER.info("Initializing...");
		GL2 gl = glDrawable.getGL().getGL2();

		gl.glClearColor(0, 0, 0, 1.0f); // Sets the background color to black

		gl.glMatrixMode(GL_PROJECTION);       // Select The Projection Matrix
		gl.glTranslated(0, 0, CAMERA_DISTANCE);
		gl.glLoadIdentity(); 					  // Reset the view matrix to the identity matrix
		glu.gluPerspective(45.0,1.25,0.1,15 + CAMERA_DISTANCE); // Specify the projection matrix (fov, w/h, near plane, far plane)

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		LOGGER.info("Initialized!");
	}

	@Override
	public void reshape(GLAutoDrawable glDrawable, int i, int i1, int width, int height) {
		LOGGER.info("Reshaping...");
		LOGGER.info("Reshaped!");
	}

	@Override
	public void dispose(GLAutoDrawable d) {
		LOGGER.info("Disposing...");
		LOGGER.info("Disposed!");
	}

	private static double num(boolean bool) {
		return bool ? 1 : 0;
	}

	private void drawSide(GL2 gl, Cube.RotationAxis axis1, Cube.RotationAxis axis2, Chunk.Color color, boolean topFrontOrRight) {
		boolean x = (axis1 == Cube.RotationAxis.X || axis2 == Cube.RotationAxis.X);
		boolean y = (axis1 == Cube.RotationAxis.Y || axis2 == Cube.RotationAxis.Y);
		boolean z = (axis1 == Cube.RotationAxis.Z || axis2 == Cube.RotationAxis.Z);
		gl.glColor3dv(color.getRgb(), 0);
		gl.glVertex3d(num(topFrontOrRight), num(topFrontOrRight), num(topFrontOrRight));
		gl.glVertex3d(num(topFrontOrRight ^ x), num(topFrontOrRight ^ (y && z)), num(topFrontOrRight));
		gl.glVertex3d(num(topFrontOrRight ^ x), num(topFrontOrRight ^ y), num(topFrontOrRight ^ z));
		gl.glVertex3d(num(topFrontOrRight), num(topFrontOrRight ^ (x && y)), num(topFrontOrRight ^ z));
	}

	private void drawTop(GL2 gl, Chunk chunk) {
		drawSide(gl, Cube.RotationAxis.X, Cube.RotationAxis.Z, chunk.getTop(), true);
	}

	private void drawBottom(GL2 gl, Chunk chunk) {
		drawSide(gl, Cube.RotationAxis.X, Cube.RotationAxis.Z, chunk.getBottom(), false);
	}

	private void drawFront(GL2 gl, Chunk chunk) {
		drawSide(gl, Cube.RotationAxis.X, Cube.RotationAxis.Y, chunk.getFront(), true);
	}

	private void drawBack(GL2 gl, Chunk chunk) {
		drawSide(gl, Cube.RotationAxis.X, Cube.RotationAxis.Y, chunk.getBack(), false);
	}

	private void drawLeft(GL2 gl, Chunk chunk) {
		drawSide(gl, Cube.RotationAxis.Y, Cube.RotationAxis.Z, chunk.getLeft(), false);
	}

	private void drawRight(GL2 gl, Chunk chunk) {
		drawSide(gl, Cube.RotationAxis.Y, Cube.RotationAxis.Z, chunk.getRight(), true);
	}

	private void drawCube(GL2 gl) {
		gl.glLoadIdentity();

		gl.glPointSize(5);
		gl.glLineWidth(2.5f);
		cubeModel
				.stream()
				.sorted((entry1, entry2) -> entry1.x() - entry2.x() + entry1.y() - entry2.y() + entry1.z() - entry2.z())
				.forEach(entry -> {
			gl.glLoadIdentity();
			glu.gluLookAt(10, 10, 10, 0, 0, 0, 0, 1, 0);
			gl.glTranslated((double) CUBE_SIZE / 2, (double) CUBE_SIZE / 2, (double) CUBE_SIZE / 2);
			double angleX = 0;
			double angleY = 0;
			double angleZ = 0;
			if (rotationInfo != null) {
				switch (rotationInfo.axis()) {
					case X -> angleX = entry.x() == rotationInfo.slice() ? (rotationAngle + 360) % 360 : 0;
					case Y -> angleY = entry.y() == rotationInfo.slice() ? (rotationAngle + 360) % 360 : 0;
					case Z -> angleZ = entry.z() == rotationInfo.slice() ? (rotationAngle + 360) % 360 : 0;
				}
				synchronized (this) {
					rotationAngle += rotationAngle > 0 ? -0.02 : 0.02;
					if (rotationAngle == 0) {
						rotationInfo = null;
					}
				}
			}
			gl.glRotated(angleX, 1, 0, 0);
			gl.glRotated(angleY, 0, 1, 0);
			gl.glRotated(angleZ, 0, 0, 1);
			gl.glTranslated(entry.x() - (double) CUBE_SIZE / 2, entry.y() - (double) CUBE_SIZE / 2, entry.z() - (double) CUBE_SIZE / 2);

			gl.glBegin(GL_QUADS);
			if (entry.x() == 0 && ((angleY > 45 && angleY < 225) || (angleZ > 135 && angleZ < 315))) {
				drawLeft(gl, entry.value());
			}
			if (entry.y() == 0 && ((angleX > 135 && angleX < 315) || (angleZ > 45 && angleZ < 225))) {
				drawBottom(gl, entry.value());
			}
			if (entry.z() == 0 && ((angleX > 45 && angleX < 225) || (angleY > 135 && angleY < 315))) {
				drawBack(gl, entry.value());
			}
			if (entry.x() == cubeModel.getSize() - 1 && !((angleY > 45 && angleY < 225) || (angleZ > 135 && angleZ < 315))) {
				drawRight(gl, entry.value());
			}
			if (entry.y() == cubeModel.getSize() - 1 && !((angleX > 135 && angleX < 315) || (angleZ > 45 && angleZ < 225))) {
				drawTop(gl, entry.value());
			}
			if (entry.z() == cubeModel.getSize() - 1 && !((angleX > 45 && angleX < 225) || (angleY > 135 && angleY < 315))) {
				drawFront(gl, entry.value());
			}
			gl.glEnd();
		});
	}

	/** void display() Draw to the canvas. */
	/* Purely a Java thing. Simple calls drawGLScene once GL is initialized */
	@Override
	public void display(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();//GL gl = glDrawable.getGL(); TOMAS
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //Clear The Screen And The Depth Buffer
		gl.glLoadIdentity();

		gl.glColor3f(1.0f, 0.0f, 0.0f);      // Set the color to red

		// Both cubes are drawn in the same location, with the same transformations, but in different order
		drawCube(gl);
	}

	public static void main(String[] args){
		GLCanvas canvas = new Main(null);//null => Assigns no properties for context
		canvas.setPreferredSize(new Dimension(800,600));

		final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Close app with "x" button
		frame.getContentPane().add(canvas);

		frame.setTitle("Exercise 3");
		frame.pack();
		frame.setVisible(true);

        final FPSAnimator animator = new FPSAnimator(canvas, 60, true);
        animator.start(); // start the animation loop
	}

	private record RotationInfo(Cube.RotationAxis axis, int slice, boolean clockwise) {}
}
