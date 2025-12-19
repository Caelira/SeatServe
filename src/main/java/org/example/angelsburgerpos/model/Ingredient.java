package org.example.angelsburgerpos.model;

public class Ingredient {
    private String id;
    private String name;
    private int stock;
    private String unit;

    public Ingredient(String id, String name, int stock, String unit) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.unit = unit;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getUnit() {
        return unit;
    }

    public void reduceStock(int quantity) {
        stock -= quantity;
    }
}