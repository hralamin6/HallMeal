package com.hallmeal.model;

public class User {
    private int id;
    private String studentId; // Unique Student ID / Account ID (e.g. CS-101, EE-204, manager)
    private String password;
    private String name;
    private String role; // 'STUDENT' or 'MANAGER'
    private double balance;

    public User() {}

    public User(int id, String studentId, String password, String name, String role, double balance) {
        this.id = id;
        this.studentId = studentId;
        this.password = password;
        this.name = name;
        this.role = role;
        this.balance = balance;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public boolean isManager() {
        return "MANAGER".equalsIgnoreCase(role);
    }
}
