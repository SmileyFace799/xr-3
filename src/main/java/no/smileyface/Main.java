package no.smileyface;

import com.jogamp.opengl.util.gl2.GLUT;
import java.util.Arrays;
import java.util.stream.Stream;
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

	private static final double[][] POINTS = new double[][]{
			new double[]{0, 2, 0}, // p0
			new double[]{1.5, 1.5, 0}, // p1
			new double[]{2, 0, 0}, // p2
			new double[]{1.5, -1.5, 0}, // p3
			new double[]{0, -2, 0}, // p4
			new double[]{-1.5, -1.5, 0}, // p5
			new double[]{-2, 0, 0}, // p6
			new double[]{-1.5, 1.5, 0}, // p7
	};

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
	private final transient GLUT glut;

	/**
	 * Constructor.
	 */
	public Main(GLCapabilities c){
		super(c);
		this.glu = new GLU();
		this.glut = new GLUT();
		this.addGLEventListener(this);

	}

	public void init(GLAutoDrawable glDrawable) {
		LOGGER.info("init()");
		GL2 gl = glDrawable.getGL().getGL2();	//GL gl = glDrawable.getGL(); TOMAS	//Get the GL object from glDrawable

		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // Sets the background color to white

		gl.glMatrixMode(GL_PROJECTION);       // Select The Projection Matrix
		gl.glLoadIdentity(); 					  // Reset the view matrix to the identity matrix
		glu.gluPerspective(45.0,1.25,2.0,15 + CAMERA_DISTANCE); // Specify the projection matrix (fov, w/h, near plane, far plane)

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void reshape(GLAutoDrawable glDrawable, int i, int i1, int width, int height) {
		// Empty
	}

	@Override
	public void dispose(GLAutoDrawable d) {
		//lag til TOMAS
	}

	private static void drawPoints(GL2 gl, int mode) {
		gl.glBegin(mode);
		Arrays.stream(POINTS).forEach(point -> gl.glVertex3dv(point, 0));
		gl.glEnd();
	}

	private void resetGl(GL2 gl) {
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0.0f, -CAMERA_DISTANCE);
		glu.gluLookAt(1.5, 1, 1, 0.75, 0.25, -0.5, 0, 1, 0);
		gl.glPointSize(5);
		gl.glLineWidth(2.5f);
	}

	/* Draw one triangle   */
	public void drawShapes(GL2 gl)  {
		resetGl(gl);

		gl.glTranslatef(-7.5f, 5, 0);
		gl.glColor3f(0,0,1);
		drawPoints(gl, GL_POINTS);

		gl.glTranslatef(5, 0, 0);
		gl.glColor3f(0, 0.5f, 0);
		drawPoints(gl, GL_LINES);

		gl.glTranslatef(5, 0, 0);
		gl.glColor3f(1, 0, 0);
		drawPoints(gl, GL_LINE_STRIP);

		gl.glTranslatef(5, 0, 0);
		gl.glColor3f(0, 0, 0.5f);
		drawPoints(gl, GL_LINE_LOOP);

		gl.glTranslatef(-15, -5, 0);
		gl.glColor3f(0.5f, 0, 0);
		drawPoints(gl, GL_TRIANGLES);

		gl.glTranslatef(5, 0, 0);
		gl.glColor3f(0, 0.5f, 0.5f);
		drawPoints(gl, GL_TRIANGLE_STRIP);

		gl.glTranslatef(5, 0, 0);
		gl.glColor3f(0, 0, 0);
		drawPoints(gl, GL_TRIANGLE_FAN);

		gl.glTranslatef(5, 0, 0);
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		drawPoints(gl, GL_QUADS);

		gl.glTranslatef(-15, -5, 0);
		gl.glColor3f(0.5f, 0, 0.5f);
		drawPoints(gl, GL_QUAD_STRIP);

		gl.glTranslatef(5, 0, 0);
		gl.glColor3f(0.5f, 0.5f, 0);
		drawPoints(gl, GL_POLYGON);
	}

	private static void drawCubeSideA(GL2 gl, boolean directionAxisZ, boolean rightOrFront) {
		double direction = directionAxisZ ? 1 : -1;
		double side = rightOrFront ? 1 : -1;

		gl.glVertex3d(side, -1, side);
		gl.glVertex3d(side * direction,  -1, -side * direction);
		gl.glVertex3d(side * direction, 1, - side * direction);
		gl.glVertex3d(side, 1, side);
	}

	private void drawCubeA(GL2 gl) {
		resetGl(gl);
		gl.glTranslated(5, -5, 0);
		gl.glRotated(30, 0, 0, 1);
		gl.glScaled(0.75, 0.75, 0.75);
		gl.glColor3d(1, 0, 1);

		Stream.of(true, false).forEach(rightOrFront ->
				Stream.of(true, false).forEach(directionAxisZ -> {
					gl.glBegin(GL_LINE_LOOP);
					drawCubeSideA(gl, directionAxisZ, rightOrFront);

					gl.glEnd();
				})
		);

	}

	private void drawCubeB(GL2 gl) {
		resetGl(gl);
		gl.glScaled(0.75, 0.75, 0.75);
		gl.glRotated(30, 0, 0, 1);
		gl.glTranslated(5, -5, 0);
		gl.glColor3d(0, 1, 1);
		glut.glutWireCube(2);
	}

	/** void display() Draw to the canvas. */
	/* Purely a Java thing. Simple calls drawGLScene once GL is initialized */
	@Override
	public void display(GLAutoDrawable glDrawable) {
		LOGGER.info("display()");
		GL2 gl = glDrawable.getGL().getGL2();//GL gl = glDrawable.getGL(); TOMAS
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //Clear The Screen And The Depth Buffer

		drawShapes(gl);

		// Both cubes are drawn in the same location, with the same transformations, but in different order
		drawCubeA(gl);
		drawCubeB(gl);
	}

	public static void main(String[] args){
		GLCanvas canvas = new Main(null);//null => Assigns no properties for context
		canvas.setPreferredSize(new Dimension(800,600));

		final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Close app with "x" button
		frame.getContentPane().add(canvas);

		frame.setTitle("Exercise 1 + 2");
		frame.pack();
		frame.setVisible(true);
	}
}
