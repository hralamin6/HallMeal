package com.hallmeal.model;

public class Transaction {
    private int id;
    private int userId;
    private String type; // 'DEPOSIT' or 'DEBIT'
    private double amount;
    private String description;
    private String date;

    public Transaction() {}

    public Transaction(int id, int userId, String type, double amount, String description, String date) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
