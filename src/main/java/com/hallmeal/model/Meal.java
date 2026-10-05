package com.hallmeal.model;

public class Meal {
    private int id;
    private int userId;
    private String date; // YYYY-MM-DD
    private boolean breakfast;
    private boolean lunch;
    private boolean dinner;

    public Meal() {}

    public Meal(int id, int userId, String date, boolean breakfast, boolean lunch, boolean dinner) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public boolean isBreakfast() { return breakfast; }
    public void setBreakfast(boolean breakfast) { this.breakfast = breakfast; }

    public boolean isLunch() { return lunch; }
    public void setLunch(boolean lunch) { this.lunch = lunch; }

    public boolean isDinner() { return dinner; }
    public void setDinner(boolean dinner) { this.dinner = dinner; }

    public int getTotalMealsCount() {
        int count = 0;
        if (breakfast) count++;
        if (lunch) count++;
        if (dinner) count++;
        return count;
    }
}
