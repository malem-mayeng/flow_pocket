package com.flowpocket.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import com.flowpocket.repository.ExpenseRepository;
import com.flowpocket.entities.Expense;
import com.flowpocket.entities.ExpenseLabel;
import com.flowpocket.dao.CategoryExpenseSummary;
import java.util.Date;
import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {
    private final ExpenseRepository repository;
    private final MediatorLiveData<Double> monthlyBalance;

    public ExpenseViewModel(Application application) {
        super(application);
        repository = new ExpenseRepository(application);
        monthlyBalance = new MediatorLiveData<>();
    }

    public void insertExpense(Expense expense) {
        repository.insert(expense);
    }

    public void updateExpense(Expense expense) {
        repository.update(expense);
    }

    public void deleteExpense(Expense expense) {
        repository.delete(expense);
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return repository.getAllExpenses();
    }

    public LiveData<List<Expense>> getMonthlyExpenses(Date startDate, Date endDate) {
        return repository.getExpensesByDateRange(startDate, endDate);
    }

    public LiveData<Double> getMonthlyTotal(Date startDate, Date endDate) {
        return repository.getTotalExpensesByDateRange(startDate, endDate);
    }

    public LiveData<Double> getMonthlyBalance() {
        return monthlyBalance;
    }

    public void calculateMonthlyBalance(double income, double expenses) {
        monthlyBalance.setValue(income - expenses);
    }

    public LiveData<List<CategoryExpenseSummary>> getCategoryExpenseSummary(Date startDate, Date endDate) {
        return repository.getCategoryExpenseSummary(startDate, endDate);
    }

    // Category management methods
    public LiveData<List<ExpenseLabel>> getAllCategories() {
        return repository.getAllCategories();
    }

    public void insertCategory(ExpenseLabel category) {
        new Thread(() -> {
            repository.insertCategory(category);
        }).start();
    }

    public ExpenseLabel findCategoryByName(String name) {
        return repository.findCategoryByName(name);
    }

    public ExpenseRepository getRepository() {
        return repository;
    }

    public void deleteCategory(ExpenseLabel category) {
        new Thread(() -> {
            repository.deleteCategory(category);
        }).start();
    }

    public void updateCategory(ExpenseLabel category) {
        new Thread(() -> {
            repository.updateCategory(category);
        }).start();
    }

    public int countExpensesWithLabel(int labelId) {
        return repository.countExpensesWithLabel(labelId);
    }
}
