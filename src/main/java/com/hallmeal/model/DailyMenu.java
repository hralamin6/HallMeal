package com.hallmeal.model;

public class DailyMenu {
    private String date; // YYYY-MM-DD
    private String breakfastItem;
    private String lunchItem;
    private String dinnerItem;
    private double breakfastRate;
    private double lunchRate;
    private double dinnerRate;

    public DailyMenu() {
        this.breakfastItem = "Standard Breakfast";
        this.lunchItem = "Standard Lunch";
        this.dinnerItem = "Standard Dinner";
        this.breakfastRate = 25.0;
        this.lunchRate = 50.0;
        this.dinnerRate = 45.0;
    }

    public DailyMenu(String date, String breakfastItem, String lunchItem, String dinnerItem,
                     double breakfastRate, double lunchRate, double dinnerRate) {
        this.date = date;
        this.breakfastItem = breakfastItem;
        this.lunchItem = lunchItem;
        this.dinnerItem = dinnerItem;
        this.breakfastRate = breakfastRate;
        this.lunchRate = lunchRate;
        this.dinnerRate = dinnerRate;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getBreakfastItem() { return breakfastItem; }
    public void setBreakfastItem(String breakfastItem) { this.breakfastItem = breakfastItem; }

    public String getLunchItem() { return lunchItem; }
    public void setLunchItem(String lunchItem) { this.lunchItem = lunchItem; }

    public String getDinnerItem() { return dinnerItem; }
    public void setDinnerItem(String dinnerItem) { this.dinnerItem = dinnerItem; }

    public double getBreakfastRate() { return breakfastRate; }
    public void setBreakfastRate(double breakfastRate) { this.breakfastRate = breakfastRate; }

    public double getLunchRate() { return lunchRate; }
    public void setLunchRate(double lunchRate) { this.lunchRate = lunchRate; }

    public double getDinnerRate() { return dinnerRate; }
    public void setDinnerRate(double dinnerRate) { this.dinnerRate = dinnerRate; }
}
