package com.hallmeal;

import com.formdev.flatlaf.FlatDarkLaf;
import com.hallmeal.db.DatabaseHelper;
import com.hallmeal.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set FlatLaf Dark Look and Feel
        try {
            FlatDarkLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf look and feel. Defaulting to system L&F.");
        }

        // Initialize SQLite Database and create tables
        DatabaseHelper.initializeDatabase();

        // Launch Login UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
