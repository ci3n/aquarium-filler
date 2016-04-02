import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        setActionListeners();

        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.setBorder(new TitledBorder("Settings"));
        this.add(aquariumTextField);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(resetButton);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(fillButton);
    }

    private void setActionListeners() {
        resetButton.addActionListener(e -> {
                    aquariumTextField.setText("3 1 1 1 1 3");
                    glCanvas.setAquarium(parseAquarium());
                }
        );

        fillButton.addActionListener(e -> glCanvas.setAquarium(parseAquarium()));
    }

    private int[] parseAquarium() {
        String[] tmp = aquariumTextField.getText().split("\\s++");
        aquarium = new int[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            aquarium[i] = Integer.parseInt(tmp[i]);
        }
        return aquarium;
    }

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
