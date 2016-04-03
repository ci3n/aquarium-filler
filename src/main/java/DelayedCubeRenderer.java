import com.jogamp.opengl.GL2;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ci3n on 04/3/16.
 */

/**
 * Alternative version of CubeRenderer, which renders
 * transparent cubes one by one with a short delay.
 *
 * @see CubeRenderer
 */
public class DelayedCubeRenderer extends CubeRenderer {
    private Timer timer = new Timer();
    private int maxHeight;
    private int currentHeight;

    public DelayedCubeRenderer(final int[] cubes, final int[] water) {
        super(cubes, water);
        setTimer();
    }

    @Override
    public void setData(final int[] cubes, final int[] water) {
        super.setData(cubes, water);
        setTimer();
    }

    /**
     * Sets animation timer to make transparent cubes appear gradually
     */
    private void setTimer() {
        maxHeight = 0;
        currentHeight = Integer.MAX_VALUE;
        for (int i = 0; i < water.length; i++) {
            if (maxHeight < water[i] + cubes[i]) maxHeight = water[i] + cubes[i];
            if (currentHeight > cubes[i] && water[i] != 0) currentHeight = cubes[i];
        }
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                currentHeight++;
                if (currentHeight > maxHeight) {
                    timer.cancel();
                    timer.purge();
                    System.err.println("Stopped!");
                }
            }
        };
        timer.scheduleAtFixedRate(task, 300, 300);
    }

    @Override
    protected void renderWater(final GL2 gl, boolean tiltedAway, boolean tiltedLeft) {
        gl.glColor4f(0f, 0f, 0.7f, 0.5f);
        float xShift = 2f;
        float yShift = tiltedAway ? -2f : 2f;
        if (tiltedLeft) {
            gl.glTranslatef(2f * (float) cubes.length - 2f, 0f, 0f); // start rendering from the right side
            xShift = -2f; // move to the left
        }

        for (int column = 0; column < cubes.length; column++) {
            int columnIndex = tiltedLeft ? cubes.length - column - 1 : column;
            gl.glTranslatef(0f, (float) cubes[columnIndex] * 2f, 0f);
            // will render water if current height is bigger than solid cubes height
            int currentWaterHeight = 0;
            if (this.currentHeight - cubes[columnIndex] > 0) {
                currentWaterHeight = Math.min(this.currentHeight - cubes[columnIndex], water[columnIndex]);
            }
            if (tiltedAway) {
                gl.glTranslatef(0f, (float) currentWaterHeight * 2f + 2f, 0f); // start rendering from top
            }
            for (int waterCube = 0; waterCube < currentWaterHeight; waterCube++) {
                gl.glTranslatef(0f, yShift, 0f);
                gl.glCallList(cubeDisplayList);
            }
            // return to ground level and move to the next column
            if (tiltedAway) {
                gl.glTranslatef(xShift, -(float) cubes[columnIndex] * 2f - 2f, 0f);
            } else {
                gl.glTranslatef(xShift, -(float) (cubes[columnIndex] + currentWaterHeight) * 2f, 0f);
            }
        }
    }
}
