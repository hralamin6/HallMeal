package com.hallmeal.util;

import com.hallmeal.dao.ExpenseDAO;
import com.hallmeal.dao.MealDAO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MealCalculator {

    private final MealDAO mealDAO = new MealDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    public static final int CUTOFF_HOUR_24 = 22; // 10:00 PM cutoff for next day meals

    public double calculateMonthlyMealRate(String yearMonth) {
        int totalMeals = mealDAO.getTotalMealsForMonth(yearMonth);
        double totalExpense = expenseDAO.getTotalExpenseForMonth(yearMonth);

        if (totalMeals == 0) {
            return 0.0;
        }
        return totalExpense / totalMeals;
    }

    public static boolean isMealEditable(LocalDate targetDate) {
        LocalDate today = LocalDate.now();

        if (targetDate.isBefore(today) || targetDate.isEqual(today)) {
            return false; // Cannot edit past or current day meals
        }

        if (targetDate.isEqual(today.plusDays(1))) {
            LocalTime currentTime = LocalTime.now();
            return currentTime.getHour() < CUTOFF_HOUR_24;
        }

        return targetDate.isAfter(today.plusDays(1));
    }

    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String currentMonthString() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
}
