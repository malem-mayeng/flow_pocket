package com.flowpocket.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.flowpocket.database.FlowPocketDatabase;
import com.flowpocket.dao.LoanDao;
import com.flowpocket.entities.Loan;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoanRepository {
    private final LoanDao loanDao;
    private final ExecutorService executorService;

    public LoanRepository(Application application) {
        FlowPocketDatabase database = FlowPocketDatabase.getInstance(application);
        loanDao = database.loanDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Loan loan) {
        executorService.execute(() -> loanDao.insert(loan));
    }

    public void update(Loan loan) {
        executorService.execute(() -> loanDao.update(loan));
    }

    public void delete(Loan loan) {
        executorService.execute(() -> loanDao.delete(loan));
    }

    public LiveData<List<Loan>> getAllLoans() {
        return loanDao.getAllLoans();
    }

    public LiveData<List<Loan>> getBorrowedLoans() {
        return loanDao.getBorrowedLoans();
    }

    public LiveData<List<Loan>> getLentLoans() {
        return loanDao.getLentLoans();
    }

    public LiveData<List<Loan>> getPendingLoans() {
        return loanDao.getPendingLoans();
    }

    public LiveData<List<Loan>> getSettledLoans() {
        return loanDao.getSettledLoans();
    }

    public LiveData<Double> getTotalBorrowedPending() {
        return loanDao.getTotalBorrowedPending();
    }

    public LiveData<Double> getTotalLentPending() {
        return loanDao.getTotalLentPending();
    }

    public void toggleSettled(Loan loan) {
        executorService.execute(() -> {
            loan.setSettled(!loan.isSettled());
            loanDao.update(loan);
        });
    }
}
