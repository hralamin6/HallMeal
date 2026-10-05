package com.hallmeal.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:hallmeal.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Enable foreign keys
            stmt.execute("PRAGMA foreign_keys = ON;");

            // Check if legacy table structure exists and upgrade if needed
            boolean needsUpgrade = false;
            try {
                stmt.executeQuery("SELECT student_id FROM users LIMIT 1");
            } catch (SQLException e) {
                // Legacy column 'username' exists, drop table to recreate clean schema
                needsUpgrade = true;
            }

            if (needsUpgrade) {
                stmt.execute("DROP TABLE IF EXISTS meals");
                stmt.execute("DROP TABLE IF EXISTS transactions");
                stmt.execute("DROP TABLE IF EXISTS users");
            }

            // Users Table (student_id used for login & identification)
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    name TEXT NOT NULL,
                    role TEXT NOT NULL, -- 'STUDENT' or 'MANAGER'
                    balance REAL DEFAULT 0.0
                );
            """;
            stmt.execute(createUsersTable);

            // Daily Meals Table (ON/OFF)
            String createMealsTable = """
                CREATE TABLE IF NOT EXISTS meals (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    date TEXT NOT NULL, -- YYYY-MM-DD
                    breakfast INTEGER DEFAULT 0,
                    lunch INTEGER DEFAULT 0,
                    dinner INTEGER DEFAULT 0,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    UNIQUE(user_id, date)
                );
            """;
            stmt.execute(createMealsTable);

            // Market Expenses Table
            String createExpensesTable = """
                CREATE TABLE IF NOT EXISTS market_expenses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    date TEXT NOT NULL, -- YYYY-MM-DD
                    item_name TEXT NOT NULL,
                    amount REAL NOT NULL
                );
            """;
            stmt.execute(createExpensesTable);

            // Transactions Table (Deposits & Deductions)
            String createTransactionsTable = """
                CREATE TABLE IF NOT EXISTS transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    type TEXT NOT NULL, -- 'DEPOSIT' or 'DEBIT'
                    amount REAL NOT NULL,
                    description TEXT,
                    date TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
            """;
            stmt.execute(createTransactionsTable);

            // Daily Menu & Custom Meal Rates Table
            String createDailyMenuTable = """
                CREATE TABLE IF NOT EXISTS daily_menu (
                    date TEXT PRIMARY KEY,
                    breakfast_item TEXT NOT NULL DEFAULT 'Standard Breakfast',
                    lunch_item TEXT NOT NULL DEFAULT 'Standard Lunch',
                    dinner_item TEXT NOT NULL DEFAULT 'Standard Dinner',
                    breakfast_rate REAL NOT NULL DEFAULT 25.0,
                    lunch_rate REAL NOT NULL DEFAULT 50.0,
                    dinner_rate REAL NOT NULL DEFAULT 45.0
                );
            """;
            stmt.execute(createDailyMenuTable);

            // Seed default users and essential visualization data if empty
            seedDefaultData(stmt);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void seedDefaultData(Statement stmt) throws SQLException {
        var rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
        if (rs.next() && rs.getInt(1) == 0) {
            // Seed 1 Manager and 4 Demo Students with Student IDs
            stmt.execute("INSERT INTO users (student_id, password, name, role, balance) VALUES ('manager', 'admin123', 'Hall Dining Manager', 'MANAGER', 0.0)");
            stmt.execute("INSERT INTO users (student_id, password, name, role, balance) VALUES ('CS-101', '1234', 'Rahim Ahmed', 'STUDENT', 2000.0)");
            stmt.execute("INSERT INTO users (student_id, password, name, role, balance) VALUES ('EE-204', '1234', 'Karim Hasan', 'STUDENT', 2500.0)");
            stmt.execute("INSERT INTO users (student_id, password, name, role, balance) VALUES ('ME-305', '1234', 'Tanvir Alam', 'STUDENT', 1800.0)");
            stmt.execute("INSERT INTO users (student_id, password, name, role, balance) VALUES ('BBA-402', '1234', 'Nusrat Jahan', 'STUDENT', 3000.0)");

            // Log initial deposit transactions
            LocalDate today = LocalDate.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String todayStr = today.format(fmt);
            String d1 = today.minusDays(4).format(fmt);
            String d2 = today.minusDays(3).format(fmt);
            String d3 = today.minusDays(2).format(fmt);
            String d4 = today.minusDays(1).format(fmt);
            String tomStr = today.plusDays(1).format(fmt);

            stmt.execute("INSERT INTO transactions (user_id, type, amount, description, date) VALUES (2, 'DEPOSIT', 2000.0, 'Initial Counter Deposit', '" + d1 + "')");
            stmt.execute("INSERT INTO transactions (user_id, type, amount, description, date) VALUES (3, 'DEPOSIT', 2500.0, 'Initial Counter Deposit', '" + d1 + "')");
            stmt.execute("INSERT INTO transactions (user_id, type, amount, description, date) VALUES (4, 'DEPOSIT', 1800.0, 'Initial Counter Deposit', '" + d1 + "')");
            stmt.execute("INSERT INTO transactions (user_id, type, amount, description, date) VALUES (5, 'DEPOSIT', 3000.0, 'Initial Counter Deposit', '" + d1 + "')");

            // Seed Daily Menus & Rates for past & upcoming days
            stmt.execute("INSERT OR IGNORE INTO daily_menu (date, breakfast_item, lunch_item, dinner_item, breakfast_rate, lunch_rate, dinner_rate) VALUES ('" + d1 + "', 'Khichuri & Egg', 'Chicken Biryani', 'Beef Curry & Rice', 30.0, 60.0, 50.0)");
            stmt.execute("INSERT OR IGNORE INTO daily_menu (date, breakfast_item, lunch_item, dinner_item, breakfast_rate, lunch_rate, dinner_rate) VALUES ('" + d2 + "', 'Paratha & Dal', 'Fish Curry & Rice', 'Chicken Roast & Rice', 25.0, 50.0, 55.0)");
            stmt.execute("INSERT OR IGNORE INTO daily_menu (date, breakfast_item, lunch_item, dinner_item, breakfast_rate, lunch_rate, dinner_rate) VALUES ('" + d3 + "', 'Luchi & Alur Dom', 'Mutton Kacchi', 'Egg Curry & Rice', 35.0, 80.0, 40.0)");
            stmt.execute("INSERT OR IGNORE INTO daily_menu (date, breakfast_item, lunch_item, dinner_item, breakfast_rate, lunch_rate, dinner_rate) VALUES ('" + d4 + "', 'Halwa & Puri', 'Beef Tehari', 'Ruhi Fish Curry', 30.0, 70.0, 50.0)");
            stmt.execute("INSERT OR IGNORE INTO daily_menu (date, breakfast_item, lunch_item, dinner_item, breakfast_rate, lunch_rate, dinner_rate) VALUES ('" + todayStr + "', 'Bread & Jam', 'Chicken Polao', 'Mixed Veg & Rice', 20.0, 65.0, 45.0)");
            stmt.execute("INSERT OR IGNORE INTO daily_menu (date, breakfast_item, lunch_item, dinner_item, breakfast_rate, lunch_rate, dinner_rate) VALUES ('" + tomStr + "', 'Paratha & Omelette', 'Special Kacchi Biryani', 'Prawn Malai Curry', 30.0, 90.0, 60.0)");

            // Seed Meal Preferences / Taken History for students
            // Student 1 (Rahim)
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (2, '" + d1 + "', 1, 1, 1)");
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (2, '" + d2 + "', 1, 1, 0)");
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (2, '" + d3 + "', 0, 1, 1)");
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (2, '" + d4 + "', 1, 1, 1)");
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (2, '" + todayStr + "', 1, 1, 1)");

            // Student 2 (Karim)
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (3, '" + d1 + "', 0, 1, 1)");
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (3, '" + d2 + "', 1, 1, 1)");
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (3, '" + d3 + "', 1, 0, 1)");
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (3, '" + d4 + "', 1, 1, 0)");

            // Student 3 (Tanvir)
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (4, '" + d1 + "', 1, 1, 1)");
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (4, '" + d2 + "', 1, 1, 1)");
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (4, '" + d3 + "', 1, 1, 1)");

            // Student 4 (Nusrat)
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (5, '" + d1 + "', 1, 0, 1)");
            stmt.execute("INSERT OR IGNORE INTO meals (user_id, date, breakfast, lunch, dinner) VALUES (5, '" + d2 + "', 0, 1, 1)");

            // Seed Market Expenses
            stmt.execute("INSERT INTO market_expenses (date, item_name, amount) VALUES ('" + d1 + "', 'Rice (2 Bags 50kg)', 3200.0)");
            stmt.execute("INSERT INTO market_expenses (date, item_name, amount) VALUES ('" + d2 + "', 'Broiler Chicken (20kg)', 3600.0)");
            stmt.execute("INSERT INTO market_expenses (date, item_name, amount) VALUES ('" + d3 + "', 'Ruhi Fish & Spices', 2500.0)");
            stmt.execute("INSERT INTO market_expenses (date, item_name, amount) VALUES ('" + d4 + "', 'Cooking Oil & Vegetables', 1800.0)");
            stmt.execute("INSERT INTO market_expenses (date, item_name, amount) VALUES ('" + todayStr + "', 'Egg & Milk Stock', 1400.0)");
        }
    }
}
