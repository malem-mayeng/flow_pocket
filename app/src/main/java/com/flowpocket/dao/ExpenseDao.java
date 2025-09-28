package com.flowpocket.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.flowpocket.entities.Expense;
import java.util.Date;
import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    long insert(Expense expense);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    LiveData<List<Expense>> getAllExpenses();

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    List<Expense> getAllExpensesSync();

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    LiveData<List<Expense>> getExpensesByDateRange(Date startDate, Date endDate);

    @Query("SELECT * FROM expenses WHERE label_id = :labelId")
    LiveData<List<Expense>> getExpensesByLabel(int labelId);

    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalExpensesByDateRange(Date startDate, Date endDate);

    @Query("SELECT el.name as categoryName, el.color as categoryColor, SUM(e.amount) as totalAmount " +
           "FROM expenses e " +
           "INNER JOIN expense_labels el ON e.label_id = el.id " +
           "WHERE e.date BETWEEN :startDate AND :endDate " +
           "GROUP BY e.label_id, el.name, el.color " +
           "HAVING SUM(e.amount) > 0 " +
           "ORDER BY SUM(e.amount) DESC")
    LiveData<List<CategoryExpenseSummary>> getCategoryExpenseSummary(Date startDate, Date endDate);
}