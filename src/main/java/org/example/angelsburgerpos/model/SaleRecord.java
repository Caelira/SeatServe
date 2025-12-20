package org.example.angelsburgerpos.model;

public class SaleRecord {
    private String date;
    private String time;
    private double amount;
    private String cashier;

    public SaleRecord(String date, String time, double amount, String cashier) {
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.cashier = cashier;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public double getAmount() {
        return amount;
    }

    public String getCashier() {
        return cashier;
    }
}