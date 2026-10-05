package com.hallmeal.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.hallmeal.dao.UserDAO;
import com.hallmeal.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtStudentId;
    private JPasswordField txtPassword;
    private JComboBox<String> comboQuickLogin;
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("PSTU HallMeal - Login Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 480);
        setLocationRelativeTo(null);
        setResizable(true);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("PSTU HallMeal Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(41, 128, 185));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("Residence Hall Dining Portal", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 1;
        mainPanel.add(lblSubtitle, gbc);

        // Quick Demo Selector
        gbc.gridwidth = 1;
        gbc.gridy = 2; gbc.gridx = 0;
        JLabel lblQuick = new JLabel("Quick Demo Login:");
        lblQuick.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(lblQuick, gbc);

        comboQuickLogin = new JComboBox<>(new String[] {
            "-- Select Demo User --",
            "manager (Dining Manager)",
            "CS-101 (Rahim Ahmed)",
            "EE-204 (Karim Hasan)",
            "ME-305 (Tanvir Alam)",
            "BBA-402 (Nusrat Jahan)"
        });
        comboQuickLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboQuickLogin.addActionListener(e -> fillQuickLogin());
        gbc.gridx = 1;
        mainPanel.add(comboQuickLogin, gbc);

        // Student ID
        gbc.gridy = 3; gbc.gridx = 0;
        JLabel lblId = new JLabel("Student ID / User ID:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(lblId, gbc);

        txtStudentId = new JTextField();
        txtStudentId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtStudentId.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g., CS-101 or manager");
        gbc.gridx = 1;
        mainPanel.add(txtStudentId, gbc);

        // Password
        gbc.gridy = 4; gbc.gridx = 0;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter password");
        gbc.gridx = 1;
        mainPanel.add(txtPassword, gbc);

        // Login Button
        JButton btnLogin = new JButton("Login to Portal");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.addActionListener(e -> performLogin());

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 10, 10, 10);
        mainPanel.add(btnLogin, gbc);

        add(mainPanel);
    }

    private void fillQuickLogin() {
        int index = comboQuickLogin.getSelectedIndex();
        if (index == 1) {
            txtStudentId.setText("manager");
            txtPassword.setText("admin123");
        } else if (index == 2) {
            txtStudentId.setText("CS-101");
            txtPassword.setText("1234");
        } else if (index == 3) {
            txtStudentId.setText("EE-204");
            txtPassword.setText("1234");
        } else if (index == 4) {
            txtStudentId.setText("ME-305");
            txtPassword.setText("1234");
        } else if (index == 5) {
            txtStudentId.setText("BBA-402");
            txtPassword.setText("1234");
        }
    }

    private void performLogin() {
        String studentId = txtStudentId.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (studentId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Student ID and Password.", "Login Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(studentId, password);
        if (user != null) {
            this.dispose();
            if (user.isManager()) {
                new ManagerDashboardFrame(user).setVisible(true);
            } else {
                new StudentDashboardFrame(user).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Student ID or Password! Please try demo credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
