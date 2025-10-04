package com.flowpocket.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.flowpocket.entities.Loan;
import java.util.Date;
import java.util.List;

@Dao
public interface LoanDao {
    @Insert
    long insert(Loan loan);

    @Insert
    long insertLoanSync(Loan loan);

    @Update
    void update(Loan loan);

    @Delete
    void delete(Loan loan);

    @Query("SELECT * FROM loans ORDER BY is_settled ASC, transaction_date DESC")
    LiveData<List<Loan>> getAllLoans();

    @Query("SELECT * FROM loans WHERE type = :type ORDER BY is_settled ASC, transaction_date DESC")
    LiveData<List<Loan>> getLoansByType(String type);

    @Query("SELECT * FROM loans WHERE type = 'borrowed' ORDER BY is_settled ASC, transaction_date DESC")
    LiveData<List<Loan>> getBorrowedLoans();

    @Query("SELECT * FROM loans WHERE type = 'lent' ORDER BY is_settled ASC, transaction_date DESC")
    LiveData<List<Loan>> getLentLoans();

    @Query("SELECT * FROM loans WHERE is_settled = 0 ORDER BY transaction_date DESC")
    LiveData<List<Loan>> getPendingLoans();

    @Query("SELECT * FROM loans WHERE is_settled = 1 ORDER BY settled_date DESC")
    LiveData<List<Loan>> getSettledLoans();

    @Query("SELECT SUM(amount) FROM loans WHERE type = 'borrowed' AND is_settled = 0")
    LiveData<Double> getTotalBorrowedPending();

    @Query("SELECT SUM(amount) FROM loans WHERE type = 'lent' AND is_settled = 0")
    LiveData<Double> getTotalLentPending();

    @Query("SELECT * FROM loans ORDER BY created_at DESC")
    List<Loan> getAllLoansSync();
}
