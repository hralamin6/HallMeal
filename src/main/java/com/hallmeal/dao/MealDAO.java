package com.hallmeal.dao;

import com.hallmeal.db.DatabaseHelper;
import com.hallmeal.model.Meal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MealDAO {

    public Meal getMeal(int userId, String date) {
        String sql = "SELECT * FROM meals WHERE user_id = ? AND date = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, date);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Meal(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("date"),
                    rs.getInt("breakfast") == 1,
                    rs.getInt("lunch") == 1,
                    rs.getInt("dinner") == 1
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Meal(0, userId, date, false, false, false);
    }

    public boolean saveOrUpdateMeal(Meal meal) {
        String sql = """
            INSERT INTO meals (user_id, date, breakfast, lunch, dinner)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(user_id, date) DO UPDATE SET
                breakfast = excluded.breakfast,
                lunch = excluded.lunch,
                dinner = excluded.dinner
        """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, meal.getUserId());
            pstmt.setString(2, meal.getDate());
            pstmt.setInt(3, meal.isBreakfast() ? 1 : 0);
            pstmt.setInt(4, meal.isLunch() ? 1 : 0);
            pstmt.setInt(5, meal.isDinner() ? 1 : 0);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalMealsForMonth(String yearMonth) { // e.g. '2026-10'
        String sql = "SELECT SUM(breakfast + lunch + dinner) FROM meals WHERE date LIKE ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, yearMonth + "%");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getUserTotalMealsForMonth(int userId, String yearMonth) {
        String sql = "SELECT SUM(breakfast + lunch + dinner) FROM meals WHERE user_id = ? AND date LIKE ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, yearMonth + "%");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int[] getDailyMealCounts(String date) { // returns [breakfast, lunch, dinner]
        int[] counts = new int[3];
        String sql = "SELECT SUM(breakfast), SUM(lunch), SUM(dinner) FROM meals WHERE date = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                counts[0] = rs.getInt(1);
                counts[1] = rs.getInt(2);
                counts[2] = rs.getInt(3);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counts;
    }

    public double calculateUserMonthlyBill(int userId, String yearMonth) {
        String sql = """
            SELECT SUM(
                m.breakfast * COALESCE(d.breakfast_rate, 25.0) +
                m.lunch * COALESCE(d.lunch_rate, 50.0) +
                m.dinner * COALESCE(d.dinner_rate, 45.0)
            )
            FROM meals m
            LEFT JOIN daily_menu d ON m.date = d.date
            WHERE m.user_id = ? AND m.date LIKE ?
        """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, yearMonth + "%");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public List<Meal> getUserMealHistory(int userId) {
        List<Meal> list = new ArrayList<>();
        String sql = "SELECT * FROM meals WHERE user_id = ? ORDER BY date DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Meal(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("date"),
                    rs.getInt("breakfast") == 1,
                    rs.getInt("lunch") == 1,
                    rs.getInt("dinner") == 1
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
