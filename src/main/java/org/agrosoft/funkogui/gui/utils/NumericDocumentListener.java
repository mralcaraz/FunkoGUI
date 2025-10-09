package org.agrosoft.funkogui.gui.utils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class NumericDocumentListener implements DocumentListener {
    private final JTextField textField;

    public NumericDocumentListener(JTextField textField) {
        this.textField = textField;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        validateInput();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        validateInput();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        validateInput();
    }

    private void validateInput() {
        String text = textField.getText();
        if (!text.matches("-?(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)")) {
            String validText = text
                    .replaceAll("[^-0-9.]", "")
                    .replaceAll("(?<!^)-", "")
                    .replaceAll("(\\..*?)\\.", "$1");
            if (!text.equals(validText)) {
                SwingUtilities.invokeLater(() -> textField.setText(validText));
            }
        }
    }
}
