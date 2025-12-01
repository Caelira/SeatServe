package org.example.angelsburgerpos.model;

public class CartItem {
    private Product product;
    private int quantity;
    private double total;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.total = product.getPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public String getName() {
        return product.getName();
    }

    public Double getPrice() {
        return product.getPrice();
    }

    public int getQuantity() {
        return quantity;
    }

    public Double getTotal() {
        return total;
    }

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
}