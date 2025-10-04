package com.flowpocket.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.flowpocket.repository.LoanRepository;
import com.flowpocket.entities.Loan;
import java.util.List;

public class LoanViewModel extends AndroidViewModel {
    private final LoanRepository repository;

    public LoanViewModel(Application application) {
        super(application);
        repository = new LoanRepository(application);
    }

    public void insertLoan(Loan loan) {
        repository.insert(loan);
    }

    public void updateLoan(Loan loan) {
        repository.update(loan);
    }

    public void deleteLoan(Loan loan) {
        repository.delete(loan);
    }

    public LiveData<List<Loan>> getAllLoans() {
        return repository.getAllLoans();
    }

    public LiveData<List<Loan>> getBorrowedLoans() {
        return repository.getBorrowedLoans();
    }

    public LiveData<List<Loan>> getLentLoans() {
        return repository.getLentLoans();
    }

    public LiveData<List<Loan>> getPendingLoans() {
        return repository.getPendingLoans();
    }

    public LiveData<List<Loan>> getSettledLoans() {
        return repository.getSettledLoans();
    }

    public LiveData<Double> getTotalBorrowedPending() {
        return repository.getTotalBorrowedPending();
    }

    public LiveData<Double> getTotalLentPending() {
        return repository.getTotalLentPending();
    }

    public void toggleSettled(Loan loan) {
        repository.toggleSettled(loan);
    }
}
