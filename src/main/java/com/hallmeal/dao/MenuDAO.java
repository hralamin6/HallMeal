package com.hallmeal.dao;

import com.hallmeal.db.DatabaseHelper;
import com.hallmeal.model.DailyMenu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    public DailyMenu getMenuForDate(String date) {
        String sql = "SELECT * FROM daily_menu WHERE date = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new DailyMenu(
                    rs.getString("date"),
                    rs.getString("breakfast_item"),
                    rs.getString("lunch_item"),
                    rs.getString("dinner_item"),
                    rs.getDouble("breakfast_rate"),
                    rs.getDouble("lunch_rate"),
                    rs.getDouble("dinner_rate")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Return default menu if not found
        DailyMenu defaultMenu = new DailyMenu();
        defaultMenu.setDate(date);
        return defaultMenu;
    }

    public boolean saveOrUpdateMenu(DailyMenu menu) {
        String sql = """
            INSERT INTO daily_menu (date, breakfast_item, lunch_item, dinner_item, breakfast_rate, lunch_rate, dinner_rate)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(date) DO UPDATE SET
                breakfast_item = excluded.breakfast_item,
                lunch_item = excluded.lunch_item,
                dinner_item = excluded.dinner_item,
                breakfast_rate = excluded.breakfast_rate,
                lunch_rate = excluded.lunch_rate,
                dinner_rate = excluded.dinner_rate
        """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, menu.getDate());
            pstmt.setString(2, menu.getBreakfastItem());
            pstmt.setString(3, menu.getLunchItem());
            pstmt.setString(4, menu.getDinnerItem());
            pstmt.setDouble(5, menu.getBreakfastRate());
            pstmt.setDouble(6, menu.getLunchRate());
            pstmt.setDouble(7, menu.getDinnerRate());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<DailyMenu> getAllMenusForMonth(String yearMonth) {
        List<DailyMenu> list = new ArrayList<>();
        String sql = "SELECT * FROM daily_menu WHERE date LIKE ? ORDER BY date ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, yearMonth + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new DailyMenu(
                    rs.getString("date"),
                    rs.getString("breakfast_item"),
                    rs.getString("lunch_item"),
                    rs.getString("dinner_item"),
                    rs.getDouble("breakfast_rate"),
                    rs.getDouble("lunch_rate"),
                    rs.getDouble("dinner_rate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
