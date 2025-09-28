package com.flowpocket.dao;

public class CategoryExpenseSummary {
    public String categoryName;
    public String categoryColor;
    public double totalAmount;

    public CategoryExpenseSummary(String categoryName, String categoryColor, double totalAmount) {
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
        this.totalAmount = totalAmount;
    }

    // Getters
    public String getCategoryName() { return categoryName; }
    public String getCategoryColor() { return categoryColor; }
    public double getTotalAmount() { return totalAmount; }
}