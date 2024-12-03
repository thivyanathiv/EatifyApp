package com.example.eatifyapp;

public class Order {

    private String foodName;
    private int quantity;
    private int totalAmount;

    // Default constructor (required for Firebase)
    public Order() {
    }

    // Parameterized constructor to initialize the order
    public Order(String foodName, int quantity, int totalAmount) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    // Getter and setter methods for foodName
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    // Getter and setter methods for quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter and setter methods for totalAmount
    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
}
