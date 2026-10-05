package com.hallmeal.ui;

import com.hallmeal.dao.MealDAO;
import com.hallmeal.dao.MenuDAO;
import com.hallmeal.dao.TransactionDAO;
import com.hallmeal.dao.UserDAO;
import com.hallmeal.model.DailyMenu;
import com.hallmeal.model.Meal;
import com.hallmeal.model.Transaction;
import com.hallmeal.model.User;
import com.hallmeal.util.MealCalculator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class StudentDashboardFrame extends JFrame {
    private User currentUser;
    private final UserDAO userDAO = new UserDAO();
    private final MealDAO mealDAO = new MealDAO();
    private final MenuDAO menuDAO = new MenuDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final MealCalculator calculator = new MealCalculator();

    // UI Controls
    private JLabel lblWelcome;
    private JLabel lblBalance;
    private JLabel lblStatusBadge;
    private JCheckBox chkBreakfast;
    private JCheckBox chkLunch;
    private JCheckBox chkDinner;
    private JButton btnSaveMeal;
    private JComboBox<String> comboDates;

    // History Table Controls
    private JTable tableHistory;
    private DefaultTableModel modelHistory;

    // Ledger Controls
    private JTable tableLedger;
    private DefaultTableModel modelLedger;
    private JLabel lblMealsCount;
    private JLabel lblTotalBill;
    private JLabel lblTotalDeposits;
    private JLabel lblNetStatus;

    // Edit Profile Controls
    private JTextField txtProfileName;
    private JTextField txtProfileStudentId;
    private JPasswordField txtProfilePassword;

    public StudentDashboardFrame(User user) {
        this.currentUser = user;
        setTitle("PSTU HallMeal - Student Dashboard (" + user.getName() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 700);
        setLocationRelativeTo(null);

        initComponents();
        loadStudentData();
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        lblWelcome = new JLabel("Welcome, " + currentUser.getName() + " (ID: " + currentUser.getStudentId() + ")");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblWelcome.setForeground(Color.WHITE);

        lblBalance = new JLabel("Balance: " + String.format("%.2f", currentUser.getBalance()) + " Tk");
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBalance.setForeground(new Color(241, 196, 15));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeader.setOpaque(false);
        rightHeader.add(lblBalance);
        rightHeader.add(btnLogout);

        headerPanel.add(lblWelcome, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("🍽️ Meal ON/OFF Switch", createMealSwitchPanel());
        tabbedPane.addTab("📅 Meal Taken History", createMealHistoryPanel());
        tabbedPane.addTab("💳 Account Ledger & Bill", createLedgerPanel());
        tabbedPane.addTab("👤 Edit Profile", createProfilePanel());

        rootPanel.add(tabbedPane, BorderLayout.CENTER);
        add(rootPanel);
    }

    private JPanel createMealSwitchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Date selection top bar
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        JLabel lblSel = new JLabel("Select Meal Date:");
        lblSel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        datePanel.add(lblSel);

        LocalDate today = LocalDate.now();
        String[] dates = new String[]{
            MealCalculator.formatDate(today.plusDays(1)) + " (Tomorrow)",
            MealCalculator.formatDate(today.plusDays(2)),
            MealCalculator.formatDate(today.plusDays(3)),
            MealCalculator.formatDate(today) + " (Today - Locked)"
        };
        comboDates = new JComboBox<>(dates);
        comboDates.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboDates.addActionListener(e -> onDateSelected());
        datePanel.add(comboDates);

        lblStatusBadge = new JLabel("STATUS");
        lblStatusBadge.setOpaque(true);
        lblStatusBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatusBadge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        datePanel.add(lblStatusBadge);

        panel.add(datePanel, BorderLayout.NORTH);

        // Meal Checkbox Container
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Daily Menu & Meal Selection"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 25, 14, 25);
        gbc.anchor = GridBagConstraints.WEST;

        chkBreakfast = new JCheckBox(" Breakfast");
        chkBreakfast.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(chkBreakfast, gbc);

        chkLunch = new JCheckBox(" Lunch");
        chkLunch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 1;
        centerPanel.add(chkLunch, gbc);

        chkDinner = new JCheckBox(" Dinner");
        chkDinner.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 2;
        centerPanel.add(chkDinner, gbc);

        btnSaveMeal = new JButton("Save Meal Status");
        btnSaveMeal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSaveMeal.setBackground(new Color(46, 204, 113));
        btnSaveMeal.setForeground(Color.WHITE);
        btnSaveMeal.addActionListener(e -> saveMealStatus());
        gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(btnSaveMeal, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Bottom Info Notice
        JLabel lblNotice = new JLabel("<html>📌 <b>Cut-off Rule:</b> Tomorrow's meals can be modified until <b>10:00 PM</b> today. Same-day & past meals are locked automatically.</html>");
        lblNotice.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNotice.setBorder(BorderFactory.createEmptyBorder(12, 5, 12, 5));
        panel.add(lblNotice, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMealHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        modelHistory = new DefaultTableModel(new String[]{
            "Date", "Breakfast (Item & Status)", "Lunch (Item & Status)", "Dinner (Item & Status)", "Meals Count", "Day Bill (Tk)"
        }, 0);
        tableHistory = new JTable(modelHistory);
        tableHistory.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableHistory.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(tableHistory);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 My Meal Consumption History"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLedgerPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Summary Cards (4 Cards)
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 12, 0));

        JPanel p1 = createStatCard("Total Meals Taken", "0", new Color(52, 152, 219));
        lblMealsCount = (JLabel) p1.getComponent(1);

        JPanel p2 = createStatCard("Total Meal Bill", "0.00 Tk", new Color(230, 126, 34));
        lblTotalBill = (JLabel) p2.getComponent(1);

        JPanel p3 = createStatCard("Total Deposits", "0.00 Tk", new Color(46, 204, 113));
        lblTotalDeposits = (JLabel) p3.getComponent(1);

        JPanel p4 = createStatCard("Current Balance", "0.00 Tk", new Color(155, 89, 182));
        lblNetStatus = (JLabel) p4.getComponent(1);

        summaryPanel.add(p1);
        summaryPanel.add(p2);
        summaryPanel.add(p3);
        summaryPanel.add(p4);

        panel.add(summaryPanel, BorderLayout.NORTH);

        // Transactions Table
        modelLedger = new DefaultTableModel(new String[]{"ID", "Type", "Amount (Tk)", "Description", "Date"}, 0);
        tableLedger = new JTable(modelLedger);
        tableLedger.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableLedger.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(tableLedger);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transaction History & Deposit Ledger"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("👤 Edit Student Profile"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 18, 12, 18);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel l1 = new JLabel("Full Name:");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(l1, gbc);

        txtProfileName = new JTextField(currentUser.getName());
        txtProfileName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(txtProfileName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel l2 = new JLabel("Student ID:");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(l2, gbc);

        txtProfileStudentId = new JTextField(currentUser.getStudentId());
        txtProfileStudentId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtProfileStudentId.setEditable(false);
        gbc.gridx = 1;
        panel.add(txtProfileStudentId, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel l3 = new JLabel("New Password:");
        l3.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(l3, gbc);

        txtProfilePassword = new JPasswordField(currentUser.getPassword());
        txtProfilePassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(txtProfilePassword, gbc);

        JButton btnSaveProfile = new JButton("Update Profile");
        btnSaveProfile.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSaveProfile.setBackground(new Color(41, 128, 185));
        btnSaveProfile.setForeground(Color.WHITE);
        btnSaveProfile.addActionListener(e -> saveProfile());

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(22, 18, 12, 18);
        panel.add(btnSaveProfile, gbc);

        return panel;
    }

    private JPanel createStatCard(String title, String initialVal, Color bg) {
        JPanel card = new JPanel(new GridLayout(2, 1, 5, 5));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblVal = new JLabel(initialVal);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblVal.setForeground(Color.WHITE);

        card.add(lblTitle);
        card.add(lblVal);
        return card;
    }

    private String getSelectedDateString() {
        String selected = (String) comboDates.getSelectedItem();
        if (selected != null) {
            return selected.split(" ")[0]; // Extract YYYY-MM-DD
        }
        return MealCalculator.formatDate(LocalDate.now().plusDays(1));
    }

    private void onDateSelected() {
        String dateStr = getSelectedDateString();
        LocalDate date = LocalDate.parse(dateStr);

        // Load configured Daily Menu & Meal Rates
        DailyMenu menu = menuDAO.getMenuForDate(dateStr);
        chkBreakfast.setText(String.format(" Breakfast: %s (%.2f Tk)", menu.getBreakfastItem(), menu.getBreakfastRate()));
        chkLunch.setText(String.format(" Lunch: %s (%.2f Tk)", menu.getLunchItem(), menu.getLunchRate()));
        chkDinner.setText(String.format(" Dinner: %s (%.2f Tk)", menu.getDinnerItem(), menu.getDinnerRate()));

        boolean editable = MealCalculator.isMealEditable(date);

        if (editable) {
            lblStatusBadge.setText(" EDITABLE ");
            lblStatusBadge.setBackground(new Color(46, 204, 113));
            lblStatusBadge.setForeground(Color.WHITE);
            chkBreakfast.setEnabled(true);
            chkLunch.setEnabled(true);
            chkDinner.setEnabled(true);
            btnSaveMeal.setEnabled(true);
        } else {
            lblStatusBadge.setText(" LOCKED (CUTOFF EXPIRED) ");
            lblStatusBadge.setBackground(new Color(231, 76, 60));
            lblStatusBadge.setForeground(Color.WHITE);
            chkBreakfast.setEnabled(false);
            chkLunch.setEnabled(false);
            chkDinner.setEnabled(false);
            btnSaveMeal.setEnabled(false);
        }

        // Load existing meal preferences for this date
        Meal meal = mealDAO.getMeal(currentUser.getId(), dateStr);
        chkBreakfast.setSelected(meal.isBreakfast());
        chkLunch.setSelected(meal.isLunch());
        chkDinner.setSelected(meal.isDinner());
    }

    private void saveMealStatus() {
        String dateStr = getSelectedDateString();
        LocalDate date = LocalDate.parse(dateStr);

        if (!MealCalculator.isMealEditable(date)) {
            JOptionPane.showMessageDialog(this, "Meal modification for " + dateStr + " is locked!", "Cut-off Expired", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Meal meal = new Meal(
            0,
            currentUser.getId(),
            dateStr,
            chkBreakfast.isSelected(),
            chkLunch.isSelected(),
            chkDinner.isSelected()
        );

        boolean success = mealDAO.saveOrUpdateMeal(meal);
        if (success) {
            JOptionPane.showMessageDialog(this, "Meal preferences saved for " + dateStr, "Success", JOptionPane.INFORMATION_MESSAGE);
            loadStudentData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save meal preferences.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveProfile() {
        String newName = txtProfileName.getText().trim();
        String newPass = new String(txtProfilePassword.getPassword()).trim();

        if (newName.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Password cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean updated = userDAO.updateProfile(currentUser.getId(), newName, newPass);
        if (updated) {
            currentUser.setName(newName);
            currentUser.setPassword(newPass);
            lblWelcome.setText("Welcome, " + currentUser.getName() + " (ID: " + currentUser.getStudentId() + ")");
            setTitle("HallMeal - Student Dashboard (" + currentUser.getName() + ")");
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudentData() {
        // Refresh User object for updated balance
        currentUser = userDAO.getUserById(currentUser.getId());
        lblBalance.setText("Balance: " + String.format("%.2f", currentUser.getBalance()) + " Tk");
        lblWelcome.setText("Welcome, " + currentUser.getName() + " (ID: " + currentUser.getStudentId() + ")");

        // Trigger meal status reload
        onDateSelected();

        // 1. Load History Table
        modelHistory.setRowCount(0);
        List<Meal> mealsHistory = mealDAO.getUserMealHistory(currentUser.getId());
        for (Meal m : mealsHistory) {
            DailyMenu menu = menuDAO.getMenuForDate(m.getDate());
            
            String bStr = (m.isBreakfast() ? "🟢 ON: " : "🔴 OFF: ") + menu.getBreakfastItem() + " (" + menu.getBreakfastRate() + " Tk)";
            String lStr = (m.isLunch() ? "🟢 ON: " : "🔴 OFF: ") + menu.getLunchItem() + " (" + menu.getLunchRate() + " Tk)";
            String dStr = (m.isDinner() ? "🟢 ON: " : "🔴 OFF: ") + menu.getDinnerItem() + " (" + menu.getDinnerRate() + " Tk)";

            double dayCost = 0.0;
            if (m.isBreakfast()) dayCost += menu.getBreakfastRate();
            if (m.isLunch()) dayCost += menu.getLunchRate();
            if (m.isDinner()) dayCost += menu.getDinnerRate();

            modelHistory.addRow(new Object[]{
                m.getDate(),
                bStr,
                lStr,
                dStr,
                m.getTotalMealsCount(),
                String.format("%.2f", dayCost)
            });
        }

        // 2. Load Stats for current month
        String currentMonth = MealCalculator.currentMonthString();
        int userMeals = mealDAO.getUserTotalMealsForMonth(currentUser.getId(), currentMonth);
        double totalBill = mealDAO.calculateUserMonthlyBill(currentUser.getId(), currentMonth);
        double totalDeposits = transactionDAO.getTotalDepositsForUser(currentUser.getId());
        double netBalance = totalDeposits - totalBill;

        lblMealsCount.setText(String.valueOf(userMeals));
        lblTotalBill.setText(String.format("%.2f Tk", totalBill));
        lblTotalDeposits.setText(String.format("%.2f Tk", totalDeposits));
        
        if (netBalance >= 0) {
            lblNetStatus.setText(String.format("%.2f Tk (OK)", netBalance));
        } else {
            lblNetStatus.setText(String.format("-%.2f Tk (DUE)", Math.abs(netBalance)));
        }

        // 3. Load Transactions Ledger
        modelLedger.setRowCount(0);
        List<Transaction> txns = transactionDAO.getTransactionsForUser(currentUser.getId());
        for (Transaction t : txns) {
            modelLedger.addRow(new Object[]{
                t.getId(),
                t.getType(),
                String.format("%.2f", t.getAmount()),
                t.getDescription(),
                t.getDate()
            });
        }
    }
}
