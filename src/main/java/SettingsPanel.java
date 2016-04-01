import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.BorderUIResource;

/**
 * Created by ci3n on 04/1/16.
 */
public class SettingsPanel extends JPanel {
    final JButton resetButton;
    public SettingsPanel() {
        this.setBorder(new TitledBorder("Settings"));
        resetButton = new JButton("Reset");
        this.add(resetButton);
    }
}
