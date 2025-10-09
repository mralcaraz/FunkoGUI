package org.agrosoft.funkogui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.agrosoft.funkogui.gui.windows.MainWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
                JFrame mainWindow = new MainWindow();
                mainWindow.setVisible(true);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
