package com.hallmeal.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.hallmeal.dao.ExpenseDAO;
import com.hallmeal.dao.MealDAO;
import com.hallmeal.dao.MenuDAO;
import com.hallmeal.dao.TransactionDAO;
import com.hallmeal.dao.UserDAO;
import com.hallmeal.model.DailyMenu;
import com.hallmeal.model.MarketExpense;
import com.hallmeal.model.Transaction;
import com.hallmeal.model.User;
import com.hallmeal.util.MealCalculator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ManagerDashboardFrame extends JFrame {
    private final User managerUser;
    private final UserDAO userDAO = new UserDAO();
    private final MealDAO mealDAO = new MealDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final MenuDAO menuDAO = new MenuDAO();
    private final MealCalculator calculator = new MealCalculator();

    // Market Expense UI Components
    private JTextField txtItemName;
    private JTextField txtAmount;
    private JTextField txtExpenseDate;
    private JTable tableExpenses;
    private DefaultTableModel modelExpenses;

    // Rates & Stats UI
    private JLabel lblTotalMonthMeals;
    private JLabel lblTotalMonthExpenses;
    private JLabel lblDynamicMealRate;
    private JComboBox<String> comboSummaryDate;
    private JLabel lblBreakfastCount;
    private JLabel lblLunchCount;
    private JLabel lblDinnerCount;

    // Menu & Meal Rates Manager UI
    private JTextField txtMenuDate;
    private JTextField txtBreakfastItem;
    private JTextField txtBreakfastRate;
    private JTextField txtLunchItem;
    private JTextField txtLunchRate;
    private JTextField txtDinnerItem;
    private JTextField txtDinnerRate;
    private JTable tableMenus;
    private DefaultTableModel modelMenus;

    // Student Deposit & Add Student UI
    private JComboBox<UserItem> comboStudents;
    private JTextField txtDepositAmount;
    private JTextField txtDepositNote;

    // Register New Student Form Controls
    private JTextField txtNewStudentId;
    private JPasswordField txtNewPassword;
    private JTextField txtNewName;
    private JTextField txtNewInitialDeposit;

    // Student Overview Table
    private JTable tableStudentSummary;
    private DefaultTableModel modelStudentSummary;

    public ManagerDashboardFrame(User user) {
        this.managerUser = user;
        setTitle("PSTU HallMeal - Dining Manager Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 750);
        setLocationRelativeTo(null);

        initComponents();
        loadAllManagerData();
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblWelcome = new JLabel("PSTU Dining Manager Portal (" + managerUser.getName() + ")");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblWelcome.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        headerPanel.add(lblWelcome, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("👥 Student Directory & Accounts", createStudentManagementPanel());
        tabbedPane.addTab("🍱 Menu & Meal Rates", createMenuAndRatesPanel());
        tabbedPane.addTab("🛒 Daily Market Expenses", createMarketExpensePanel());
        tabbedPane.addTab("📊 Meal Rate & Kitchen Stats", createStatsPanel());

        rootPanel.add(tabbedPane, BorderLayout.CENTER);
        add(rootPanel);
    }

    private JPanel createStudentManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Split: Register Form & Deposit Form side-by-side
        JPanel topContainer = new JPanel(new GridLayout(1, 2, 15, 0));

        // 1. Register New Student Form
        JPanel regPanel = new JPanel(new GridBagLayout());
        regPanel.setBorder(BorderFactory.createTitledBorder("➕ Add New Student"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel l1 = new JLabel("Student ID:");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        regPanel.add(l1, gbc);

        txtNewStudentId = new JTextField();
        txtNewStudentId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNewStudentId.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g., CS-105");
        gbc.gridx = 1;
        regPanel.add(txtNewStudentId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel l2 = new JLabel("Full Name:");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        regPanel.add(l2, gbc);

        txtNewName = new JTextField();
        txtNewName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        regPanel.add(txtNewName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel l3 = new JLabel("Password:");
        l3.setFont(new Font("Segoe UI", Font.BOLD, 13));
        regPanel.add(l3, gbc);

        txtNewPassword = new JPasswordField();
        txtNewPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        regPanel.add(txtNewPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel l4 = new JLabel("Initial Deposit (Tk):");
        l4.setFont(new Font("Segoe UI", Font.BOLD, 13));
        regPanel.add(l4, gbc);

        txtNewInitialDeposit = new JTextField("1000.00");
        txtNewInitialDeposit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        regPanel.add(txtNewInitialDeposit, gbc);

        JButton btnRegister = new JButton("Register Student");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setBackground(new Color(41, 128, 185));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.addActionListener(e -> registerStudent());

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 8, 6, 8);
        regPanel.add(btnRegister, gbc);

        // 2. Deposit Balance Form
        JPanel depPanel = new JPanel(new GridBagLayout());
        depPanel.setBorder(BorderFactory.createTitledBorder("💵 Credit Student Balance"));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(6, 8, 6, 8);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        gbc2.gridx = 0; gbc2.gridy = 0;
        JLabel ld1 = new JLabel("Select Student:");
        ld1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        depPanel.add(ld1, gbc2);

        comboStudents = new JComboBox<>();
        comboStudents.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc2.gridx = 1;
        depPanel.add(comboStudents, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 1;
        JLabel ld2 = new JLabel("Deposit (Tk):");
        ld2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        depPanel.add(ld2, gbc2);

        txtDepositAmount = new JTextField();
        txtDepositAmount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc2.gridx = 1;
        depPanel.add(txtDepositAmount, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 2;
        JLabel ld3 = new JLabel("Deposit Note:");
        ld3.setFont(new Font("Segoe UI", Font.BOLD, 13));
        depPanel.add(ld3, gbc2);

        txtDepositNote = new JTextField("Cash Deposit at Counter");
        txtDepositNote.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc2.gridx = 1;
        depPanel.add(txtDepositNote, gbc2);

        JButton btnSubmitDeposit = new JButton("Process Deposit");
        btnSubmitDeposit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmitDeposit.setBackground(new Color(39, 174, 96));
        btnSubmitDeposit.setForeground(Color.WHITE);
        btnSubmitDeposit.addActionListener(e -> processDeposit());

        gbc2.gridx = 0; gbc2.gridy = 3; gbc2.gridwidth = 2;
        gbc2.insets = new Insets(16, 8, 6, 8);
        depPanel.add(btnSubmitDeposit, gbc2);

        topContainer.add(regPanel);
        topContainer.add(depPanel);
        panel.add(topContainer, BorderLayout.NORTH);

        // Student Summary Table (Taken Meals, Deposit, Bill, Due)
        modelStudentSummary = new DefaultTableModel(new String[]{
            "Student ID", "Student Name", "Meals Taken", "Meal Bill (Tk)", "Total Deposit (Tk)", "Net Balance (Tk)", "Status"
        }, 0);
        tableStudentSummary = new JTable(modelStudentSummary);
        tableStudentSummary.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableStudentSummary.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(tableStudentSummary);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 Student-Wise Meal & Account Summary (Current Month)"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMenuAndRatesPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Top Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Edit Daily Menu Items & Meal Rates"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date selection
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel ld = new JLabel("Target Date (YYYY-MM-DD):");
        ld.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(ld, gbc);

        txtMenuDate = new JTextField(MealCalculator.formatDate(LocalDate.now().plusDays(1)));
        txtMenuDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMenuDate.addActionListener(e -> loadSelectedMenuData());
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(txtMenuDate, gbc);

        // Breakfast
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Breakfast Item:"), gbc);

        txtBreakfastItem = new JTextField("Paratha & Egg");
        txtBreakfastItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        formPanel.add(txtBreakfastItem, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Rate (Tk):"), gbc);

        txtBreakfastRate = new JTextField("25.0");
        txtBreakfastRate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 3;
        formPanel.add(txtBreakfastRate, gbc);

        // Lunch
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Lunch Item:"), gbc);

        txtLunchItem = new JTextField("Chicken Biryani");
        txtLunchItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        formPanel.add(txtLunchItem, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Rate (Tk):"), gbc);

        txtLunchRate = new JTextField("55.0");
        txtLunchRate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 3;
        formPanel.add(txtLunchRate, gbc);

        // Dinner
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Dinner Item:"), gbc);

        txtDinnerItem = new JTextField("Fish Curry & Rice");
        txtDinnerItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        formPanel.add(txtDinnerItem, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Rate (Tk):"), gbc);

        txtDinnerRate = new JTextField("45.0");
        txtDinnerRate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 3;
        formPanel.add(txtDinnerRate, gbc);

        // Save Button
        JButton btnSaveMenu = new JButton("Save Menu Items & Meal Rates");
        btnSaveMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSaveMenu.setBackground(new Color(39, 174, 96));
        btnSaveMenu.setForeground(Color.WHITE);
        btnSaveMenu.addActionListener(e -> saveMenuData());

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.insets = new Insets(12, 10, 8, 10);
        formPanel.add(btnSaveMenu, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        // Table Panel for Monthly Overview
        modelMenus = new DefaultTableModel(new String[]{
            "Date", "Breakfast Item", "B.Rate (Tk)", "Lunch Item", "L.Rate (Tk)", "Dinner Item", "D.Rate (Tk)"
        }, 0);
        tableMenus = new JTable(modelMenus);
        tableMenus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableMenus.setRowHeight(26);

        JScrollPane scrollPane = new JScrollPane(tableMenus);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Configured Daily Menus & Rates"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMarketExpensePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Log Market Purchase"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);

        txtExpenseDate = new JTextField(MealCalculator.formatDate(LocalDate.now()));
        txtExpenseDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        formPanel.add(txtExpenseDate, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Item Name / Description:"), gbc);

        txtItemName = new JTextField();
        txtItemName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtItemName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g., Rice, Vegetables, Fish");
        gbc.gridx = 1;
        formPanel.add(txtItemName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Amount (Tk):"), gbc);

        txtAmount = new JTextField();
        txtAmount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAmount.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0.00");
        gbc.gridx = 1;
        formPanel.add(txtAmount, gbc);

        JButton btnAddExpense = new JButton("Add Expense Item");
        btnAddExpense.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAddExpense.setBackground(new Color(52, 152, 219));
        btnAddExpense.setForeground(Color.WHITE);
        btnAddExpense.addActionListener(e -> addExpenseItem());

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(btnAddExpense, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        // Table Panel
        modelExpenses = new DefaultTableModel(new String[]{"ID", "Date", "Item Description", "Amount (Tk)"}, 0);
        tableExpenses = new JTable(modelExpenses);
        tableExpenses.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableExpenses.setRowHeight(26);

        JScrollPane scrollPane = new JScrollPane(tableExpenses);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Month Expenses"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Rate Cards Header
        JPanel cardPanel = new JPanel(new GridLayout(1, 3, 15, 0));

        JPanel c1 = createCard("Total Meals (Month)", "0", new Color(41, 128, 185));
        lblTotalMonthMeals = (JLabel) c1.getComponent(1);

        JPanel c2 = createCard("Total Expenses (Month)", "0.00 Tk", new Color(192, 57, 43));
        lblTotalMonthExpenses = (JLabel) c2.getComponent(1);

        JPanel c3 = createCard("Avg Market Rate / Meal", "0.00 Tk / meal", new Color(39, 174, 96));
        lblDynamicMealRate = (JLabel) c3.getComponent(1);

        cardPanel.add(c1);
        cardPanel.add(c2);
        cardPanel.add(c3);

        panel.add(cardPanel, BorderLayout.NORTH);

        // Kitchen Prep Daily Counts Panel
        JPanel prepPanel = new JPanel(new BorderLayout(10, 10));
        prepPanel.setBorder(BorderFactory.createTitledBorder("👨‍🍳 Daily Kitchen Prep Counts"));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topBar.add(new JLabel("Select Date:"));

        LocalDate today = LocalDate.now();
        String[] dates = new String[]{
            MealCalculator.formatDate(today) + " (Today)",
            MealCalculator.formatDate(today.plusDays(1)) + " (Tomorrow)",
            MealCalculator.formatDate(today.plusDays(2))
        };
        comboSummaryDate = new JComboBox<>(dates);
        comboSummaryDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboSummaryDate.addActionListener(e -> updateDailyCounts());
        topBar.add(comboSummaryDate);

        prepPanel.add(topBar, BorderLayout.NORTH);

        JPanel countsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        countsGrid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel pB = createCountBox("🥐 Breakfast Meals", "0");
        lblBreakfastCount = (JLabel) pB.getComponent(1);

        JPanel pL = createCountBox("🍲 Lunch Meals", "0");
        lblLunchCount = (JLabel) pL.getComponent(1);

        JPanel pD = createCountBox("🍱 Dinner Meals", "0");
        lblDinnerCount = (JLabel) pD.getComponent(1);

        countsGrid.add(pB);
        countsGrid.add(pL);
        countsGrid.add(pD);

        prepPanel.add(countsGrid, BorderLayout.CENTER);

        panel.add(prepPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCard(String title, String val, Color bg) {
        JPanel card = new JPanel(new GridLayout(2, 1, 5, 5));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblVal = new JLabel(val);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblVal.setForeground(Color.WHITE);

        card.add(lblTitle);
        card.add(lblVal);
        return card;
    }

    private JPanel createCountBox(String title, String val) {
        JPanel box = new JPanel(new GridLayout(2, 1, 5, 5));
        box.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        box.setBackground(new Color(245, 247, 250));

        JLabel lblT = new JLabel(title, SwingConstants.CENTER);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblV = new JLabel(val, SwingConstants.CENTER);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblV.setForeground(new Color(44, 62, 80));

        box.add(lblT);
        box.add(lblV);
        return box;
    }

    private void registerStudent() {
        String name = txtNewName.getText().trim();
        String studentId = txtNewStudentId.getText().trim();
        String password = new String(txtNewPassword.getPassword()).trim();
        String depositStr = txtNewInitialDeposit.getText().trim();

        if (name.isEmpty() || studentId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Student ID, Name, and Password.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double deposit = depositStr.isEmpty() ? 0.0 : Double.parseDouble(depositStr);
            User student = new User(0, studentId, password, name, "STUDENT", deposit);

            boolean success = userDAO.addStudent(student);
            if (success) {
                if (deposit > 0) {
                    Transaction txn = new Transaction(0, student.getId(), "DEPOSIT", deposit, "Initial Student Registration Deposit", MealCalculator.formatDate(LocalDate.now()));
                    transactionDAO.addTransaction(txn);
                }
                JOptionPane.showMessageDialog(this, "New Student Registered Successfully!\nStudent ID: " + studentId, "Success", JOptionPane.INFORMATION_MESSAGE);
                txtNewName.setText("");
                txtNewStudentId.setText("");
                txtNewPassword.setText("");
                txtNewInitialDeposit.setText("1000.00");

                loadAllManagerData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to register student. Student ID might already exist.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid deposit amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedMenuData() {
        String date = txtMenuDate.getText().trim();
        DailyMenu menu = menuDAO.getMenuForDate(date);
        txtBreakfastItem.setText(menu.getBreakfastItem());
        txtBreakfastRate.setText(String.valueOf(menu.getBreakfastRate()));
        txtLunchItem.setText(menu.getLunchItem());
        txtLunchRate.setText(String.valueOf(menu.getLunchRate()));
        txtDinnerItem.setText(menu.getDinnerItem());
        txtDinnerRate.setText(String.valueOf(menu.getDinnerRate()));
    }

    private void saveMenuData() {
        String date = txtMenuDate.getText().trim();
        String bItem = txtBreakfastItem.getText().trim();
        String lItem = txtLunchItem.getText().trim();
        String dItem = txtDinnerItem.getText().trim();

        try {
            double bRate = Double.parseDouble(txtBreakfastRate.getText().trim());
            double lRate = Double.parseDouble(txtLunchRate.getText().trim());
            double dRate = Double.parseDouble(txtDinnerRate.getText().trim());

            DailyMenu menu = new DailyMenu(date, bItem, lItem, dItem, bRate, lRate, dRate);
            boolean saved = menuDAO.saveOrUpdateMenu(menu);

            if (saved) {
                JOptionPane.showMessageDialog(this, "Daily Menu & Meal Rates updated for " + date, "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllManagerData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save menu data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid meal rate format. Enter numeric values.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addExpenseItem() {
        String date = txtExpenseDate.getText().trim();
        String item = txtItemName.getText().trim();
        String amtStr = txtAmount.getText().trim();

        if (item.isEmpty() || amtStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter item name and amount.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amtStr);
            MarketExpense exp = new MarketExpense(0, date, item, amount);
            boolean success = expenseDAO.addExpense(exp);
            if (success) {
                JOptionPane.showMessageDialog(this, "Market expense logged successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtItemName.setText("");
                txtAmount.setText("");
                loadAllManagerData();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid expense amount format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processDeposit() {
        UserItem selectedItem = (UserItem) comboStudents.getSelectedItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "Select a valid student.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String amtStr = txtDepositAmount.getText().trim();
        String note = txtDepositNote.getText().trim();

        try {
            double amount = Double.parseDouble(amtStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Deposit amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User student = selectedItem.getUser();
            double newBalance = student.getBalance() + amount;
            boolean updated = userDAO.updateBalance(student.getId(), newBalance);

            if (updated) {
                Transaction txn = new Transaction(0, student.getId(), "DEPOSIT", amount, note, MealCalculator.formatDate(LocalDate.now()));
                transactionDAO.addTransaction(txn);

                JOptionPane.showMessageDialog(this, "Successfully deposited " + amount + " Tk for " + student.getName() + "\nNew Balance: " + newBalance + " Tk", "Deposit Processed", JOptionPane.INFORMATION_MESSAGE);
                txtDepositAmount.setText("");
                loadAllManagerData();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid deposit amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDailyCounts() {
        String selected = (String) comboSummaryDate.getSelectedItem();
        if (selected == null) return;

        String dateStr = selected.split(" ")[0];
        int[] counts = mealDAO.getDailyMealCounts(dateStr);

        lblBreakfastCount.setText(String.valueOf(counts[0]));
        lblLunchCount.setText(String.valueOf(counts[1]));
        lblDinnerCount.setText(String.valueOf(counts[2]));
    }

    private void loadAllManagerData() {
        String currentMonth = MealCalculator.currentMonthString();

        // 1. Student Summary Table
        modelStudentSummary.setRowCount(0);
        List<User> students = userDAO.getAllStudents();
        comboStudents.removeAllItems();

        for (User s : students) {
            comboStudents.addItem(new UserItem(s));

            int mealsTaken = mealDAO.getUserTotalMealsForMonth(s.getId(), currentMonth);
            double mealBill = mealDAO.calculateUserMonthlyBill(s.getId(), currentMonth);
            double totalDeposit = transactionDAO.getTotalDepositsForUser(s.getId());
            double netBalance = totalDeposit - mealBill;

            String status = netBalance >= 0 ? "PAID (Balance)" : "DUE (Clear Bill)";

            modelStudentSummary.addRow(new Object[]{
                s.getStudentId(),
                s.getName(),
                mealsTaken,
                String.format("%.2f", mealBill),
                String.format("%.2f", totalDeposit),
                String.format("%.2f", netBalance),
                status
            });
        }

        // 2. Menu Table
        modelMenus.setRowCount(0);
        List<DailyMenu> menus = menuDAO.getAllMenusForMonth(currentMonth);
        for (DailyMenu m : menus) {
            modelMenus.addRow(new Object[]{
                m.getDate(),
                m.getBreakfastItem(), String.format("%.2f", m.getBreakfastRate()),
                m.getLunchItem(), String.format("%.2f", m.getLunchRate()),
                m.getDinnerItem(), String.format("%.2f", m.getDinnerRate())
            });
        }

        // 3. Expenses Table
        modelExpenses.setRowCount(0);
        List<MarketExpense> expenses = expenseDAO.getExpensesForMonth(currentMonth);
        for (MarketExpense e : expenses) {
            modelExpenses.addRow(new Object[]{
                e.getId(),
                e.getDate(),
                e.getItemName(),
                String.format("%.2f", e.getAmount())
            });
        }

        // 4. Meal Stats
        int totalMeals = mealDAO.getTotalMealsForMonth(currentMonth);
        double totalExpense = expenseDAO.getTotalExpenseForMonth(currentMonth);
        double rate = calculator.calculateMonthlyMealRate(currentMonth);

        lblTotalMonthMeals.setText(String.valueOf(totalMeals));
        lblTotalMonthExpenses.setText(String.format("%.2f Tk", totalExpense));
        lblDynamicMealRate.setText(String.format("%.2f Tk / meal", rate));

        // Daily Prep counts
        updateDailyCounts();
    }

    // Helper wrapper class for JComboBox
    private static class UserItem {
        private final User user;

        public UserItem(User user) {
            this.user = user;
        }

        public User getUser() { return user; }

        @Override
        public String toString() {
            return user.getName() + " (" + user.getStudentId() + ") - Balance: " + String.format("%.2f Tk", user.getBalance());
        }
    }
}
