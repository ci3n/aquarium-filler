import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * Created by ci3n on 04/1/16.
 */
public class AquariumGLCanvas extends GLCanvas {
    private AquariumFiller aquariumFiller = new AquariumFiller();
    private CubeRenderer cubeRenderer;
    private FPSAnimator fpsAnimator;

    public AquariumGLCanvas() {
        super(new GLCapabilities(GLProfile.get("GL2")));
        cubeRenderer = new CubeRenderer(new int[]{3, 1, 1, 1, 1, 3}, new int[]{0, 2, 2, 2, 2, 0});
        this.addGLEventListener(cubeRenderer);
        this.addKeyListener(cubeRenderer);
        this.addMouseMotionListener(cubeRenderer);
        this.addMouseWheelListener(cubeRenderer);
        this.setSize(600, 400);
        this.requestFocusInWindow();

        fpsAnimator = new FPSAnimator(this, 60, true);
        fpsAnimator.start();
    }

    public void setAquarium(final int[] aquarium) {
        aquariumFiller.setAquarium(aquarium);
        cubeRenderer.setData(aquarium, aquariumFiller.getWater());
    }
}
