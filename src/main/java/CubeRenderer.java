import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;
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
    private int cubeDisplayList;
    private int[] cubes;
    private int[] water;

    private float xRotation = 0f;
    private float yRotation = 0f;
    private final float delta = 1.8f;
    private float depth = -20f;
    private float sensitivity = 0.001f;
    private float oldMouseX;
    private float oldMouseY;

    private int rockTexture = -1;
    private int waterTexture = -1;

    public CubeRenderer(final int[] cubes, final int[] water) {
        this.cubes = cubes;
        this.water = water;
    }

    public void setData(final int[] cubes, final int[] water) {
        this.cubes = cubes;
        this.water = water;
    }

    @Override
    public void init(final GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        // gl.glEnable(GL2.GL_TEXTURE_2D); // Enable texture mapping
        gl.glClearColor(1f, 1f, 1f, 1f); // Set background

        //Depth settings
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);

        // Enable Lighting
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_DIFFUSE);
//        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glShadeModel(GL2.GL_SMOOTH); // Enable smooth shading

        // Enable transparency
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        // Enable coloring
        gl.glEnable(GL2.GL_COLOR_MATERIAL);

        // Perspective calculations
        gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);

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
        drawCube(gl);
    }

    @Override
    public void dispose(final GLAutoDrawable glAutoDrawable) {
        //do nothing
    }

    private void textureSet(final GL2 gl) {
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
    }

    @Override
    public void display(final GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();
        gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);

        float[] rgba = {1f, 1f, 1f};
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);


        gl.glTranslatef(0.0f, -4.0f, depth);
        gl.glRotatef(yRotation, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(xRotation, 1.0f, 0.0f, 0.0f);
        gl.glTranslatef(-2f * (float) (cubes.length / 2), 0f, 0f);


        //Draw solid cubes first
        for (int column = 0; column < cubes.length; column++) {
            textureSet(gl);
            for (int cubeIndex = 0; cubeIndex < cubes[column]; cubeIndex++) {
                gl.glTranslatef(0f, 2f, 0f);
                if (rockTexture != -1) {
                    gl.glBindTexture(GL2.GL_TEXTURE_2D, rockTexture);
                }
                gl.glColor4f(0.2f, 0.2f, 0.2f, 1f);
                gl.glCallList(cubeDisplayList);
            }
            gl.glTranslatef(2f, -(float) (cubes[column]) * 2f, 0f);
        }
        gl.glDisable(GL2.GL_TEXTURE_2D);

        //Water cubes next (to avoid strange color blending)
        gl.glTranslatef(-2f * (float) cubes.length, 0f, 0f);
        for (int column = 0; column < cubes.length; column++) {
            textureSet(gl);
            gl.glTranslatef(0f, (float) cubes[column] * 2, 0f);
            for (int waterCubeIndex = 0; waterCubeIndex < water[column]; waterCubeIndex++) {
                gl.glTranslatef(0f, 2f, 0f);
                if (waterTexture != 1) {
                    gl.glBindTexture(GL2.GL_TEXTURE_2D, waterTexture);
                }

                gl.glColor4f(0f, 0f, 0.5f, 0.5f);
                gl.glCallList(cubeDisplayList);
            }
            gl.glTranslatef(2f, -(float) (cubes[column] + water[column]) * 2f, 0f);
        }
        gl.glDisable(GL2.GL_TEXTURE_2D);
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
        gl.glBegin(GL2.GL_QUADS);
        int[][] cubeVertexes = new int[][]{
                {-1, 1, 1}, {-1, -1, 1}, {1, -1, 1}, {1, 1, 1},
                {1, 1, -1}, {1, -1, -1}, {-1, -1, -1}, {-1, 1, -1}
        };
        int[][] cubeSides = new int[][]{
                {0, 1, 2, 3}, {3, 2, 5, 4}, {4, 5, 6, 7}, {7, 6, 1, 0}, {0, 3, 4, 7}, {1, 2, 5, 6}
        };
        int[][] textureVertexes = new int[][]{
                {-1, 1}, {1, 1}, {1, -1}, {-1, -1}
        };

        for (int i = 0; i < cubeSides.length; i++) {
//            float texX = 0;
//            float texY = 0;
            for (int j = 0; j < cubeSides[i].length; j++) {
                int side = cubeSides[i][j];
//                texX += j;
//                texY += j + 1;
//                gl.glTexCoord2f((texX % 2) * 2 - 1, (texY % 2) * 2 - 1);
                gl.glTexCoord2f(textureVertexes[j][0], textureVertexes[j][1]);
                gl.glVertex3f(cubeVertexes[side][0], cubeVertexes[side][1], cubeVertexes[side][2]);
            }
        }
        gl.glEnd();
        gl.glEndList();
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        //do nothing
    }

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
        moveCamera(x, y);
    }

    @Override
    public void keyReleased(final KeyEvent keyEvent) {
        //do nothing
    }


    @Override
    public void mouseDragged(final MouseEvent e) {
        Point point = e.getPoint();
        Dimension size = e.getComponent().getSize();
        moveCamera((float) ((-point.getY() + oldMouseY) / size.getHeight()),
                (float) ((point.getX() - oldMouseX) / size.getWidth()));
        oldMouseX = (float) point.getX();
        oldMouseY = (float) point.getY();
    }


    @Override
    public void mouseMoved(final MouseEvent e) {
        //do nothing
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        float rotation = (float) e.getWheelRotation();
        moveCamera(rotation);
    }

    private void moveCamera(final float x, final float y) {
        xRotation += x >= sensitivity ? -delta : (x <= -sensitivity) ? +delta : 0;
        yRotation += y >= sensitivity ? delta : (y <= -sensitivity) ? -delta : 0;
    }

    private void moveCamera(final float rotation) {
        depth -= rotation * delta;
        if (depth > -10) depth = -10;
        if (depth < -150) depth = -150;
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        //do nothing
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        Point point = MouseInfo.getPointerInfo().getLocation();
        oldMouseX = (float) point.getX();
        oldMouseY = (float) point.getY();
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
