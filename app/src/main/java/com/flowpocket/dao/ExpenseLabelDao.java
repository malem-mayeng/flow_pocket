package com.flowpocket.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.flowpocket.entities.ExpenseLabel;
import java.util.List;

@Dao
public interface ExpenseLabelDao {
    @Insert
    long insert(ExpenseLabel label);

    @Insert
    long insertLabelSync(ExpenseLabel label);

    @Update
    void update(ExpenseLabel label);

    @Delete
    void delete(ExpenseLabel label);

    @Query("SELECT * FROM expense_labels")
    LiveData<List<ExpenseLabel>> getAllLabels();

    @Query("SELECT * FROM expense_labels WHERE id = :id")
    LiveData<ExpenseLabel> getLabelById(int id);

    @Query("SELECT * FROM expense_labels WHERE name = :name LIMIT 1")
    ExpenseLabel findByName(String name);

    @Query("SELECT COUNT(*) FROM expenses WHERE label_id = :labelId")
    int countExpensesWithLabel(int labelId);

    @Query("SELECT COUNT(*) FROM expense_labels")
    int getCount();

    @Query("INSERT INTO expense_labels (name, color, created_at, updated_at) VALUES (:name, :color, :createdAt, :updatedAt)")
    void insertDefault(String name, String color, long createdAt, long updatedAt);

    @Query("SELECT * FROM expense_labels")
    List<ExpenseLabel> getAllLabelsSync();
}