package com.flowpocket.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "expense_labels")
public class ExpenseLabel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "name")
    private String name;
    
    @ColumnInfo(name = "color")
    private String color; // Hex color code
    
    @ColumnInfo(name = "created_at")
    private Date createdAt;
    
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public ExpenseLabel(String name, String color) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Label name cannot be empty");
        }
        if (!isValidHexColor(color)) {
            throw new IllegalArgumentException("Invalid hex color code");
        }
        
        this.name = name;
        this.color = color;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(int id) { this.id = id; }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Label name cannot be empty");
        }
        this.name = name;
        this.updatedAt = new Date();
    }
    
    public void setColor(String color) {
        if (!isValidHexColor(color)) {
            throw new IllegalArgumentException("Invalid hex color code");
        }
        this.color = color;
        this.updatedAt = new Date();
    }

    // Helper method to validate hex color code
    private boolean isValidHexColor(String colorStr) {
        if (colorStr == null) return false;
        return colorStr.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$");
    }

    @Override
    public String toString() {
        return "ExpenseLabel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseLabel that = (ExpenseLabel) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}