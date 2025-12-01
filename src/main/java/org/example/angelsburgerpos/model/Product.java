package org.example.angelsburgerpos.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Product {
    private String ID;
    private String name;
    private Double price;
    private String imagePath;
    private ArrayList<RecipeItem> recipe;
    private String category;
    public Product() {
    }

    public Product(String ID, String name, Double price, String category, String imagePath) {
        this.ID = ID;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
        this.recipe = recipe;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList<RecipeItem> getRecipe() {
        return recipe;
    }

    public void setRecipe(ArrayList<RecipeItem> recipe) {
        this.recipe = recipe;
    }
}
