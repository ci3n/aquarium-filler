import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ci3n on 04/1/16.
 */
public class CubeRenderer implements GLEventListener, KeyListener, MouseMotionListener, MouseWheelListener, MouseListener {
    private GLU glu = new GLU();
    private GLUT glut = new GLUT();

    private float xRotation = 0f;
    private float yRotation = -30f;
    private float deltaX = 0f;
    private float deltaY = -4f;
    private float deltaZ = -20f;

    private final float sensitivity = 0.001f;
    private final float moveIncrement = 1.8f;
    private float oldMouseX;
    private float oldMouseY;
    // Display lists
    private int cubeDisplayList;
    // Textures
    private int rockTexture = -1;
    private int waterTexture = -1;
    // External data
    private int[] cubes;
    private int[] water;

    public CubeRenderer(final int[] cubes, final int[] water) {
        this.cubes = cubes;
        this.water = water;
    }

    /**
     * @param cubes int array defining solid cubes
     * @param water int array defining transparent cubes
     */
    public void setData(final int[] cubes, final int[] water) {
        this.cubes = cubes;
        this.water = water;
    }

    @Override
    public void init(final GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glClearColor(1f, 1f, 1f, 1f); // Set background

        // Depth settings
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_CULL_FACE); // Avoids artifacts
        gl.glCullFace(GL2.GL_BACK);

        // Enable Lighting
        gl.glShadeModel(GL2.GL_SMOOTH);

        // Enable coloring
        gl.glEnable(GL2.GL_COLOR_MATERIAL);

        // Perspective calculations
        gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);

        // Texture loaders
        try {
            InputStream stream = getClass().getResourceAsStream("rock.png");
            Texture t = TextureIO.newTexture(stream, false, "png");
            rockTexture = t.getTextureObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream stream = getClass().getResourceAsStream("water.png");
            Texture t = TextureIO.newTexture(stream, false, "png");
            waterTexture = t.getTextureObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Make display list for cubes
        drawCube(gl);
    }

    @Override
    public void dispose(final GLAutoDrawable glAutoDrawable) {
        //do nothing
    }

    @Override
    public void display(final GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();

        gl.glTranslatef(deltaX, deltaY, deltaZ);
        gl.glRotatef(yRotation, 0f, 1f, 0f);
        gl.glRotatef(xRotation, 1f, 0f, 0f);

        float[] lightPos = {-200, -300, -200, 1};        // light position
        float[] noAmbient = {0.2f, 0.2f, 0.2f, 1f};     // low ambient light
        float[] diffuse = {1f, 1f, 1f, 1f};        // full diffuse colour

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, noAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);

        //TODO: fix boundaries
        boolean top = xRotation < 0;
        boolean left = yRotation > 0;

        gl.glTranslatef(-2f * (float) (cubes.length / 2), 0f, 0f);

        // Render solid cubes first
        textureReset(gl);
        if (rockTexture != -1) {
            gl.glBindTexture(GL2.GL_TEXTURE_2D, rockTexture);
        }
        gl.glDisable(GL2.GL_BLEND);

        renderSolidCubes(gl);
        // Transparent cubes next
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glAlphaFunc(GL2.GL_LESS, 1f);
        textureReset(gl);
        if (waterTexture != 1) {
            gl.glBindTexture(GL2.GL_TEXTURE_2D, waterTexture);
        }
        renderWater(gl, top, left);
    }

    /**
     * Resets texture parameters.
     */
    private void textureReset(final GL2 gl) {
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
    }

    /**
     * Renders solid rock cubes.
     */
    private void renderSolidCubes(final GL2 gl) {
        gl.glColor4f(0.5f, 0.5f, 0.5f, 1f);
        for (int column = 0; column < cubes.length; column++) {
            for (int cubeIndex = 0; cubeIndex < cubes[column]; cubeIndex++) {
                gl.glTranslatef(0f, 2f, 0f);
                gl.glCallList(cubeDisplayList);
            }
            // return to ground level and move to the next column
            gl.glTranslatef(2f, -(float) (cubes[column]) * 2f, 0f);
        }
        gl.glTranslatef(-2f * (float) cubes.length, 0f, 0f);
    }

    /**
     * Renders transparent water cubes from back to front
     * "approximately relative" to the camera.
     * Starts rendering from the opposite side (right or top)
     * when (left or bottom) part is closer to viewer.
     * May cause incorrect rendering, but is probably faster than manual z-sorting.
     *
     * @param top  -- is tilted towards
     * @param left -- is tilted to the side
     */
    private void renderWater(final GL2 gl, boolean top, boolean left) {
        gl.glColor4f(0f, 0f, 0.7f, 0.5f);
        float xShift = 2f;
        float yShift = top ? -2f : 2f;
        if (left) {
            gl.glTranslatef(2f * (float) cubes.length - 2f, 0f, 0f); // start rendering from the right side
            xShift = -2f; // move to the left
        }
        for (int column = 0; column < cubes.length; column++) {
            int columnIndex = left ? cubes.length - column - 1 : column;
            gl.glTranslatef(0f, (float) cubes[columnIndex] * 2f, 0f);
            if (top) {
                gl.glTranslatef(0f, (float) water[columnIndex] * 2f + 2f, 0f); // start rendering from top
            }
            for (int waterCube = 0; waterCube < water[columnIndex]; waterCube++) {
                gl.glTranslatef(0f, yShift, 0f);
                gl.glCallList(cubeDisplayList);
            }
            // return to ground level and move to the next column
            if (top) {
                gl.glTranslatef(xShift, -(float) cubes[columnIndex] * 2f - 2f, 0f);
            } else {
                gl.glTranslatef(xShift, -(float) (cubes[columnIndex] + water[columnIndex]) * 2f, 0f);
            }
        }

    }

    @Override
    public void reshape(final GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        height = (height == 0) ? 1 : height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45, (float) width / height, 1, 1000);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    /**
     * Makes a cube display list (with texture vertexes)
     */
    private void drawCube(final GL2 gl) {
        /*
          7-----4
         /|    /|
        0-----3 |
        | 6---|-5
        |/    |/
        1-----2
         */
        cubeDisplayList = gl.glGenLists(1); // generate new list
        gl.glNewList(cubeDisplayList, GL2.GL_COMPILE); //start new list

        int[][] cubeVertexes = new int[][]{
                {-1, 1, 1}, {-1, -1, 1}, {1, -1, 1}, {1, 1, 1},
                {1, 1, -1}, {1, -1, -1}, {-1, -1, -1}, {-1, 1, -1}
        };
        int[][] cubeSides = new int[][]{
                {3, 2, 5, 4}, {4, 5, 6, 7}, {7, 6, 1, 0}, {1, 6, 5, 2}, {0, 3, 4, 7}, {0, 1, 2, 3}
        };
        int[][] textureVertexes = new int[][]{
                {-1, 1}, {1, 1}, {1, -1}, {-1, -1}
        };

        gl.glBegin(GL2.GL_QUADS);
        for (int[] cubeSide : cubeSides) {
            for (int j = 0; j < cubeSide.length; j++) {
                int side = cubeSide[j];
                gl.glVertex3f(cubeVertexes[side][0], cubeVertexes[side][1], cubeVertexes[side][2]);
                gl.glTexCoord2f(textureVertexes[j][0], textureVertexes[j][1]);
            }
        }
        glut.glutWireCube(2.01f);
        gl.glEnd();
        gl.glEndList();
    }

    /**
     * Rotates camera
     */
    private void rotateCamera(final float x, final float y) {
        xRotation += x >= sensitivity ? -moveIncrement : (x <= -sensitivity) ? +moveIncrement : 0;
        yRotation += y >= sensitivity ? moveIncrement : (y <= -sensitivity) ? -moveIncrement : 0;
    }

    /**
     * Moves camera back and forth
     */
    private void moveCamera(final float mouseRotation) {
        deltaZ -= mouseRotation * moveIncrement;
        if (deltaZ > -10) deltaZ = -10;
        if (deltaZ < -150) deltaZ = -150;
    }

    /**
     * Moves camera parallel to the screen
     */
    private void moveCamera(final float dX, final float dY) {
        deltaX -= dX * moveIncrement * 3;
        if (deltaX > 50) deltaX = 50;
        if (deltaX < -50) deltaX = -50;
        deltaY -= dY * moveIncrement * 3;
        if (deltaX > 30) deltaX = 30;
        if (deltaX < -30) deltaX = -30;
    }

    // Event Handlers

    @Override
    public void keyPressed(final KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        float x = 0;
        float y = 0;
        switch (keyCode) {
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                y = -sensitivity;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                y = sensitivity;
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                x = sensitivity;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                x = -sensitivity;
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
        rotateCamera(x, y);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        Point point = e.getPoint();
        Dimension size = e.getComponent().getSize();
        if (e.isShiftDown()) {
            moveCamera((float) ((-point.getX() + oldMouseX) / size.getHeight()),
                    (float) ((point.getY() - oldMouseY) / size.getWidth()));
        } else {
            rotateCamera((float) ((-point.getY() + oldMouseY) / size.getHeight()),
                    (float) ((point.getX() - oldMouseX) / size.getWidth()));
        }
        oldMouseX = (float) point.getX();
        oldMouseY = (float) point.getY();
    }


    @Override
    public void mouseMoved(final MouseEvent e) {
        oldMouseX = (float) e.getPoint().getX();
        oldMouseY = (float) e.getPoint().getY();
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        float rotation = (float) e.getWheelRotation();
        moveCamera(rotation);
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        Point point = MouseInfo.getPointerInfo().getLocation();
        oldMouseX = (float) point.getX();
        oldMouseY = (float) point.getY();
    }

    // Empty handlers


    @Override
    public void keyReleased(final KeyEvent keyEvent) {
        //do nothing
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        //do nothing
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        //do nothing
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        //do nothing
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        //do nothing
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        //do nothing
    }
}
