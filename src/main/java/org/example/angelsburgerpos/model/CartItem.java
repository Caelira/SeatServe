package org.example.angelsburgerpos.model;

import java.util.HashSet;
import java.util.Set;

public class CartItem {
    private Product product;
    private int quantity;
    private double total;

    private Set<String> removedIngredients = new HashSet<>();
    private String customizationNote = "";

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.total = product.getPrice() * quantity;
    }

    public Product getProduct() { return product; }

    public String getName() {
        if (customizationNote.isEmpty()) return product.getName();
        return product.getName() + " (" + customizationNote + ")";
    }

    public Double getPrice() { return product.getPrice(); }
    public int getQuantity() { return quantity; }
    public Double getTotal() { return total; }

    public void increaseQuantity() {
        this.quantity++;
        this.total = this.product.getPrice() * this.quantity;
    }

    public void decreaseQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
            this.total = this.product.getPrice() * this.quantity;
        }
    }

    public void setRemovedIngredients(Set<String> removed, String note) {
        this.removedIngredients = removed;
        this.customizationNote = note;
    }

    public Set<String> getRemovedIngredients() {
        return removedIngredients;
    }
}