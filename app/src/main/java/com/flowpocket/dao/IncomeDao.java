package com.flowpocket.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.flowpocket.entities.Income;
import java.util.Date;

@Dao
public interface IncomeDao {
    @Insert
    long insert(Income income);

    @Update
    void update(Income income);

    @Delete
    void delete(Income income);

    @Query("SELECT * FROM incomes ORDER BY created_at DESC LIMIT 1")
    LiveData<Income> getLatestIncome();
}