package com.flowpocket.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "loans")
public class Loan implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "type")
    private String type; // "borrowed" or "lent"

    @ColumnInfo(name = "person_name")
    private String personName;

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "transaction_date")
    private Date transactionDate;

    @ColumnInfo(name = "due_date")
    private Date dueDate; // Optional

    @ColumnInfo(name = "is_settled")
    private boolean isSettled;

    @ColumnInfo(name = "settled_date")
    private Date settledDate;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public Loan(String type, String personName, double amount, Date transactionDate) {
        if (type == null || (!type.equals("borrowed") && !type.equals("lent"))) {
            throw new IllegalArgumentException("Type must be 'borrowed' or 'lent'");
        }
        if (personName == null || personName.trim().isEmpty()) {
            throw new IllegalArgumentException("Person name cannot be empty");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }

        this.type = type;
        this.personName = personName;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.isSettled = false;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters
    public int getId() { return id; }
    public String getType() { return type; }
    public String getPersonName() { return personName; }
    public double getAmount() { return amount; }
    public Date getTransactionDate() { return transactionDate; }
    public Date getDueDate() { return dueDate; }
    public boolean isSettled() { return isSettled; }
    public Date getSettledDate() { return settledDate; }
    public String getNotes() { return notes; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(int id) { this.id = id; }

    public void setType(String type) {
        if (type == null || (!type.equals("borrowed") && !type.equals("lent"))) {
            throw new IllegalArgumentException("Type must be 'borrowed' or 'lent'");
        }
        this.type = type;
        this.updatedAt = new Date();
    }

    public void setPersonName(String personName) {
        if (personName == null || personName.trim().isEmpty()) {
            throw new IllegalArgumentException("Person name cannot be empty");
        }
        this.personName = personName;
        this.updatedAt = new Date();
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
        this.updatedAt = new Date();
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
        this.updatedAt = new Date();
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
        this.updatedAt = new Date();
    }

    public void setSettled(boolean settled) {
        this.isSettled = settled;
        if (settled && this.settledDate == null) {
            this.settledDate = new Date();
        }
        this.updatedAt = new Date();
    }

    public void setSettledDate(Date settledDate) {
        this.settledDate = settledDate;
        this.updatedAt = new Date();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = new Date();
    }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
