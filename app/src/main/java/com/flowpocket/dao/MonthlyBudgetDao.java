package com.flowpocket.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.flowpocket.entities.MonthlyBudget;
import java.util.List;

@Dao
public interface MonthlyBudgetDao {
    @Insert
    long insert(MonthlyBudget budget);

    @Insert
    void insertAll(List<MonthlyBudget> budgets);

    @Update
    void update(MonthlyBudget budget);

    @Delete
    void delete(MonthlyBudget budget);

    // Get budget for specific category and month
    @Query("SELECT * FROM monthly_budgets WHERE category_id = :categoryId AND month = :month AND year = :year AND is_active = 1")
    LiveData<MonthlyBudget> getBudgetForCategoryAndMonth(int categoryId, int month, int year);

    @Query("SELECT * FROM monthly_budgets WHERE category_id = :categoryId AND month = :month AND year = :year AND is_active = 1")
    MonthlyBudget getBudgetForCategoryAndMonthSync(int categoryId, int month, int year);

    // Get all budgets for a specific month
    @Query("SELECT * FROM monthly_budgets WHERE month = :month AND year = :year AND is_active = 1 ORDER BY category_id")
    LiveData<List<MonthlyBudget>> getBudgetsForMonth(int month, int year);

    @Query("SELECT * FROM monthly_budgets WHERE month = :month AND year = :year AND is_active = 1 ORDER BY category_id")
    List<MonthlyBudget> getBudgetsForMonthSync(int month, int year);

    // Get budget summary with category info
    @Query("SELECT mb.*, el.name as categoryName, el.color as categoryColor " +
           "FROM monthly_budgets mb " +
           "INNER JOIN expense_labels el ON mb.category_id = el.id " +
           "WHERE mb.month = :month AND mb.year = :year AND mb.is_active = 1 " +
           "ORDER BY el.name")
    LiveData<List<BudgetSummary>> getBudgetSummaryForMonth(int month, int year);

    // Update spent amount for a category and month
    @Query("UPDATE monthly_budgets SET spent_amount = :spentAmount, updated_at = :updatedAt " +
           "WHERE category_id = :categoryId AND month = :month AND year = :year AND is_active = 1")
    void updateSpentAmount(int categoryId, int month, int year, double spentAmount, long updatedAt);

    // Get over-budget categories for a month
    @Query("SELECT mb.*, el.name as categoryName, el.color as categoryColor " +
           "FROM monthly_budgets mb " +
           "INNER JOIN expense_labels el ON mb.category_id = el.id " +
           "WHERE mb.month = :month AND mb.year = :year AND mb.is_active = 1 " +
           "AND mb.spent_amount > mb.budget_amount " +
           "ORDER BY (mb.spent_amount - mb.budget_amount) DESC")
    LiveData<List<BudgetSummary>> getOverBudgetCategories(int month, int year);

    // Get total budget and spent for a month
    @Query("SELECT SUM(budget_amount) as totalBudget, SUM(spent_amount) as totalSpent " +
           "FROM monthly_budgets " +
           "WHERE month = :month AND year = :year AND is_active = 1")
    LiveData<MonthlyTotals> getMonthlyTotals(int month, int year);

    // Copy budgets from previous month (for recurring budgets)
    @Query("INSERT INTO monthly_budgets (category_id, month, year, budget_amount, spent_amount, is_active, created_at, updated_at) " +
           "SELECT category_id, :toMonth, :toYear, budget_amount, 0, 1, :currentTime, :currentTime " +
           "FROM monthly_budgets " +
           "WHERE month = :fromMonth AND year = :fromYear AND is_active = 1")
    void copyBudgetsFromPreviousMonth(int fromMonth, int fromYear, int toMonth, int toYear, long currentTime);

    // Data classes for complex queries
    class BudgetSummary {
        public int id;
        @ColumnInfo(name = "category_id")
        public int categoryId;
        public int month;
        public int year;
        @ColumnInfo(name = "budget_amount")
        public double budgetAmount;
        @ColumnInfo(name = "spent_amount")
        public double spentAmount;
        @ColumnInfo(name = "categoryName")
        public String categoryName;
        @ColumnInfo(name = "categoryColor")
        public String categoryColor;

        public double getRemainingBudget() {
            return budgetAmount - spentAmount;
        }

        public double getBudgetUsagePercentage() {
            if (budgetAmount == 0) return 0;
            return (spentAmount / budgetAmount) * 100;
        }

        public boolean isOverBudget() {
            return spentAmount > budgetAmount;
        }
    }

    class MonthlyTotals {
        public double totalBudget;
        public double totalSpent;

        public double getTotalRemaining() {
            return totalBudget - totalSpent;
        }

        public double getTotalUsagePercentage() {
            if (totalBudget == 0) return 0;
            return (totalSpent / totalBudget) * 100;
        }
    }
}