package com.hallmeal.dao;

import com.hallmeal.db.DatabaseHelper;
import com.hallmeal.model.MarketExpense;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    public boolean addExpense(MarketExpense expense) {
        String sql = "INSERT INTO market_expenses (date, item_name, amount) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, expense.getDate());
            pstmt.setString(2, expense.getItemName());
            pstmt.setDouble(3, expense.getAmount());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<MarketExpense> getExpensesForMonth(String yearMonth) {
        List<MarketExpense> list = new ArrayList<>();
        String sql = "SELECT * FROM market_expenses WHERE date LIKE ? ORDER BY date DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, yearMonth + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new MarketExpense(
                    rs.getInt("id"),
                    rs.getString("date"),
                    rs.getString("item_name"),
                    rs.getDouble("amount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getTotalExpenseForMonth(String yearMonth) {
        String sql = "SELECT SUM(amount) FROM market_expenses WHERE date LIKE ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, yearMonth + "%");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
