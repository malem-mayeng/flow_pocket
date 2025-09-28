package com.flowpocket.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.flowpocket.database.FlowPocketDatabase;
import com.flowpocket.dao.ExpenseDao;
import com.flowpocket.dao.ExpenseLabelDao;
import com.flowpocket.dao.CategoryExpenseSummary;
import com.flowpocket.entities.Expense;
import com.flowpocket.entities.ExpenseLabel;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseRepository {
    private final ExpenseDao expenseDao;
    private final ExpenseLabelDao expenseLabelDao;
    private final ExecutorService executorService;

    public ExpenseRepository(Application application) {
        FlowPocketDatabase database = FlowPocketDatabase.getInstance(application);
        expenseDao = database.expenseDao();
        expenseLabelDao = database.expenseLabelDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Expense expense) {
        executorService.execute(() -> expenseDao.insert(expense));
    }

    public void update(Expense expense) {
        executorService.execute(() -> expenseDao.update(expense));
    }

    public void delete(Expense expense) {
        executorService.execute(() -> expenseDao.delete(expense));
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return expenseDao.getAllExpenses();
    }

    public LiveData<List<Expense>> getExpensesByDateRange(Date startDate, Date endDate) {
        return expenseDao.getExpensesByDateRange(startDate, endDate);
    }

    public LiveData<Double> getTotalExpensesByDateRange(Date startDate, Date endDate) {
        return expenseDao.getTotalExpensesByDateRange(startDate, endDate);
    }

    public LiveData<List<CategoryExpenseSummary>> getCategoryExpenseSummary(Date startDate, Date endDate) {
        return expenseDao.getCategoryExpenseSummary(startDate, endDate);
    }

    // Category management methods
    public LiveData<List<ExpenseLabel>> getAllCategories() {
        return expenseLabelDao.getAllLabels();
    }

    public long insertCategory(ExpenseLabel category) {
        return expenseLabelDao.insert(category);
    }

    public ExpenseLabel findCategoryByName(String name) {
        return expenseLabelDao.findByName(name);
    }

    public void deleteCategory(ExpenseLabel category) {
        executorService.execute(() -> expenseLabelDao.delete(category));
    }

    public void updateCategory(ExpenseLabel category) {
        executorService.execute(() -> expenseLabelDao.update(category));
    }

    public int countExpensesWithLabel(int labelId) {
        return expenseLabelDao.countExpensesWithLabel(labelId);
    }
}
