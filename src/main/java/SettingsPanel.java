import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;

/**
 * Created by ci3n on 04/1/16.
 */
public class SettingsPanel extends JPanel {
    private AquariumGLCanvas glCanvas;
    private JButton resetButton;
    private JButton fillButton;
    private JTextField aquariumTextField;

    private int[] aquarium;

    public SettingsPanel(final AquariumGLCanvas glCanvas) {
        this.glCanvas = glCanvas;

        resetButton = new JButton("Reset");
        fillButton = new JButton("Fill");
        aquariumTextField = new JTextField();

        PlainDocument numbersOnlyDocument = new PlainDocument();
        numbersOnlyDocument.setDocumentFilter(new IntDocumentFilter());
        aquariumTextField.setDocument(numbersOnlyDocument);
        aquariumTextField.setText("3 1 1 1 1 3");

        resetButton.addActionListener(e -> {
                    aquariumTextField.setText("3 1 1 1 1 3");
                    glCanvas.setAquarium(parseAquarium());
                }
        );

        fillButton.addActionListener(e -> glCanvas.setAquarium(parseAquarium()));

        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.setBorder(new TitledBorder("Settings"));
        this.add(aquariumTextField);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(resetButton);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(fillButton);
    }

    /**
     * Splites string (by whitespaces) to int array.
     *
     * @return int array (empty one if there's nothing to be parsed).
     */
    private int[] parseAquarium() {
        String[] tmp = aquariumTextField.getText().split("\\s++");
        if (tmp.length == 0 || tmp[0].equals("")) return new int[]{}; // avoids NumberFormatException
        aquarium = new int[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            aquarium[i] = Integer.parseInt(tmp[i]);
        }
        return aquarium;
    }

    /**
     * Allows only digits and whitespaces in string to make parsing error-free.
     */
    private class IntDocumentFilter extends DocumentFilter {
        private final static String REPLACE_PATTERN = "[^\\d\\s]++";

        @Override
        public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr) throws BadLocationException {
            fb.insertString(offset, string.replaceAll(REPLACE_PATTERN, ""), attr);
        }

        @Override
        public void replace(final FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs) throws BadLocationException {
            fb.replace(offset, length, text.replaceAll(REPLACE_PATTERN, ""), attrs);
        }
    }


}
