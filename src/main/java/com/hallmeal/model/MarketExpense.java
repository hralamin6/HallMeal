package com.hallmeal.model;

public class MarketExpense {
    private int id;
    private String date; // YYYY-MM-DD
    private String itemName;
    private double amount;

    public MarketExpense() {}

    public MarketExpense(int id, String date, String itemName, double amount) {
        this.id = id;
        this.date = date;
        this.itemName = itemName;
        this.amount = amount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
