package org.example.angelsburgerpos.model;

public class Ingredient {
    private String id;
    private String name;
    private int stock;
    private String unit;
    private boolean isCritical;

    public Ingredient(String id, String name, int stock, String unit, boolean isCritical) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.unit = unit;
        this.isCritical = isCritical;
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

    public boolean isCritical() {
        return isCritical;
    }

    public void reduceStock(int quantity) {
        stock -= quantity;
    }
}