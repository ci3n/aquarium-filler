import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import javafx.scene.layout.Border;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ci3n on 04/1/16.
 */
public class AquariumGLPanel extends GLJPanel {
    private AquariumFiller aquariumFiller = new AquariumFiller();
    private CubeRenderer cubeRenderer;
    private FPSAnimator fpsAnimator;

    public AquariumGLPanel() {
        super(new GLCapabilities(GLProfile.get("GL2")));
        cubeRenderer = new CubeRenderer(new int[]{3, 1, 1, 1, 1, 3}, new int[]{0, 2, 2, 2, 2, 0});
        this.addGLEventListener(cubeRenderer);
        this.addKeyListener(cubeRenderer);
        this.addMouseMotionListener(cubeRenderer);
        this.addMouseWheelListener(cubeRenderer);
        this.setMinimumSize(new Dimension(300,300));
        this.setPreferredSize(new Dimension(600,400));
        this.setBorder(BorderFactory.createEtchedBorder(0));
        fpsAnimator = new FPSAnimator(this, 60, true);
        fpsAnimator.start();
    }

    /**
     * Sets given int array as aquarium
     */
    public void setAquarium(final int[] aquarium) {
        aquariumFiller.setAquarium(aquarium);
        cubeRenderer.setData(aquarium, aquariumFiller.getWater());
    }
}
