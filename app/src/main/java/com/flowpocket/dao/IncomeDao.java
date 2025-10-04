package com.flowpocket.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.flowpocket.entities.Income;
import java.util.Date;
import java.util.List;

@Dao
public interface IncomeDao {
    @Insert
    long insert(Income income);

    @Insert
    long insertIncomeSync(Income income);

    @Update
    void update(Income income);

    @Delete
    void delete(Income income);

    @Query("SELECT * FROM incomes ORDER BY created_at DESC LIMIT 1")
    LiveData<Income> getLatestIncome();

    @Query("SELECT * FROM incomes WHERE month BETWEEN :startDate AND :endDate ORDER BY created_at DESC LIMIT 1")
    LiveData<Income> getIncomeByMonth(Date startDate, Date endDate);

    @Query("SELECT * FROM incomes ORDER BY created_at DESC")
    List<Income> getAllIncomeSync();
}