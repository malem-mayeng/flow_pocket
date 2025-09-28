package com.flowpocket.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "expenses")
public class Expense implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "label_id")
    private int labelId;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public Expense(String name, double amount, Date date, int labelId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Expense name cannot be empty");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Expense amount cannot be negative");
        }

        this.name = name;
        this.amount = amount;
        this.date = date;
        this.labelId = labelId;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
    public int getLabelId() { return labelId; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(int id) { this.id = id; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Expense name cannot be empty");
        }
        this.name = name;
        this.updatedAt = new Date();
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Expense amount cannot be negative");
        }
        this.amount = amount;
        this.updatedAt = new Date();
    }

    public void setDate(Date date) {
        this.date = date;
        this.updatedAt = new Date();
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
        this.updatedAt = new Date();
    }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}