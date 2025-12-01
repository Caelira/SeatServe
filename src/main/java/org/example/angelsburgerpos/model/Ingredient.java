package org.example.angelsburgerpos.model;

public class Ingredient {
    private String ID;
    private String name;
    private String unit;
    private Double stock;
    private int level;

    public Ingredient() {
    }

    public Ingredient(String ID, String name, String unit, Double stock, int level) {
        this.ID = ID;
        this.name = name;
        this.unit = unit;
        this.stock = stock;
        this.level = level;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getStock() {
        return stock;
    }

    public void setStock(Double stock) {
        this.stock = stock;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
