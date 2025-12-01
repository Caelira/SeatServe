package org.example.angelsburgerpos.model;

public class Product {
    private String id;
    private String name;
    private double price;
    private String imagePath;
    private String category;
    private int stock;

    public Product(String id, String name, double price, String imagePath, String category, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.category = category;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCategory() {
        return category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}