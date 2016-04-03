import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

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
    public void init(final boolean animated) {
        final JPanel mainPanel = new JPanel();
        final AquariumGLPanel aquariumGLPanel = new AquariumGLPanel(animated);
        final SettingsPanel settingsPanel = new SettingsPanel(aquariumGLPanel);

        this.setContentPane(mainPanel);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        mainPanel.add(settingsPanel, BorderLayout.NORTH);
        mainPanel.add(aquariumGLPanel, BorderLayout.CENTER);
        this.setSize(getPreferredSize());
        this.setMinimumSize(getMinimumSize());
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
        boolean animated = args.length != 0 && Arrays.<String>asList(args).contains("--animated");
        mainFrame.init(animated);
    }
}
