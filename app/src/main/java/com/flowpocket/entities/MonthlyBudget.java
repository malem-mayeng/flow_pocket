package com.flowpocket.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "monthly_budgets",
        foreignKeys = @ForeignKey(entity = ExpenseLabel.class,
                                parentColumns = "id",
                                childColumns = "category_id",
                                onDelete = ForeignKey.CASCADE))
public class MonthlyBudget implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "category_id")
    private int categoryId;

    @ColumnInfo(name = "month")
    private int month; // 1-12

    @ColumnInfo(name = "year")
    private int year;

    @ColumnInfo(name = "budget_amount")
    private double budgetAmount;

    @ColumnInfo(name = "spent_amount")
    private double spentAmount; // Cached for performance

    @ColumnInfo(name = "is_active")
    private boolean isActive; // For soft delete

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public MonthlyBudget(int categoryId, int month, int year, double budgetAmount) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year < 2020 || year > 2100) {
            throw new IllegalArgumentException("Year must be between 2020 and 2100");
        }
        if (budgetAmount < 0) {
            throw new IllegalArgumentException("Budget amount cannot be negative");
        }

        this.categoryId = categoryId;
        this.month = month;
        this.year = year;
        this.budgetAmount = budgetAmount;
        this.spentAmount = 0.0;
        this.isActive = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters
    public int getId() { return id; }
    public int getCategoryId() { return categoryId; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public double getBudgetAmount() { return budgetAmount; }
    public double getSpentAmount() { return spentAmount; }
    public boolean isActive() { return isActive; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        this.updatedAt = new Date();
    }
    public void setMonth(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        this.month = month;
        this.updatedAt = new Date();
    }
    public void setYear(int year) {
        this.year = year;
        this.updatedAt = new Date();
    }
    public void setBudgetAmount(double budgetAmount) {
        if (budgetAmount < 0) {
            throw new IllegalArgumentException("Budget amount cannot be negative");
        }
        this.budgetAmount = budgetAmount;
        this.updatedAt = new Date();
    }
    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
        this.updatedAt = new Date();
    }
    public void setActive(boolean active) {
        isActive = active;
        this.updatedAt = new Date();
    }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    // Utility methods
    public double getRemainingBudget() {
        return budgetAmount - spentAmount;
    }

    public double getBudgetUsagePercentage() {
        if (budgetAmount == 0) return 0;
        return (spentAmount / budgetAmount) * 100;
    }

    public boolean isOverBudget() {
        return spentAmount > budgetAmount;
    }
}