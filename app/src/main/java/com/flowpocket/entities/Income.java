package com.flowpocket.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import java.util.Date;

@Entity(tableName = "incomes")
public class Income {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "month")
    private Date month;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public Income(double amount, Date month) {
        if (amount < 0) {
            throw new IllegalArgumentException("Income amount cannot be negative");
        }
        this.amount = amount;
        this.month = month;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public Date getMonth() { return month; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    
    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Income amount cannot be negative");
        }
        this.amount = amount;
        this.updatedAt = new Date();
    }

    public void setMonth(Date month) {
        this.month = month;
        this.updatedAt = new Date();
    }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}