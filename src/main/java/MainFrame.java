import javax.swing.*;
import java.awt.*;

/**
 * Created by ci3n on 04/1/16.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        super("Aquarium Filler");
    }

    /**
     * Initialises GUI components
     */
    public void init() {
        final JPanel mainPanel = new JPanel();
        final AquariumGLCanvas aquariumGLCanvas = new AquariumGLCanvas();
        final SettingsPanel settingsPanel = new SettingsPanel(aquariumGLCanvas);

        this.setContentPane(mainPanel);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(aquariumGLCanvas, BorderLayout.CENTER);
        mainPanel.add(settingsPanel, BorderLayout.NORTH);

        this.setSize(getPreferredSize());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // tries to make it nicer
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
