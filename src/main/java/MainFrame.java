import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

/**
 * Created by ci3n on 04/1/16.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        super("Aquarium Filler");
    }

    public void init() {
        final JPanel mainPanel = new JPanel();
        final SettingsPanel settingsPanel = new SettingsPanel();
        GLCanvas glCanvas = new GLCanvas(new GLCapabilities(GLProfile.get("GL2")));

        CubeRenderer cubeRenderer = new CubeRenderer(new int[]{3, 1, 0, 3, 1, 4, 1, 2}, new int[]{0, 0, 0, 0, 2, 0, 1, 0});
        glCanvas.addGLEventListener(cubeRenderer);
        glCanvas.addKeyListener(cubeRenderer);
        glCanvas.addMouseMotionListener(cubeRenderer);
        glCanvas.addMouseWheelListener(cubeRenderer);
        glCanvas.setSize(600, 400);
        glCanvas.requestFocusInWindow();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(glCanvas, BorderLayout.CENTER);
        mainPanel.add(settingsPanel, BorderLayout.EAST);
        this.setContentPane(mainPanel);
        this.setSize(getPreferredSize());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        chandgeLookAndFeel();

        FPSAnimator fpsAnimator = new FPSAnimator(glCanvas, 60, true);
        fpsAnimator.start();
    }

    private void chandgeLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.init();
    }
}
