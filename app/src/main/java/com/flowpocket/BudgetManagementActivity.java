package com.flowpocket;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.flowpocket.ui.BudgetCategoryAdapter;
import com.flowpocket.viewmodel.ExpenseViewModel;
import com.flowpocket.dao.MonthlyBudgetDao;
import com.flowpocket.utils.MonthYearPicker;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Button;
import android.graphics.Color;
import java.util.Calendar;
import java.util.ArrayList;

public class BudgetManagementActivity extends AppCompatActivity {
    private ExpenseViewModel expenseViewModel;
    private RecyclerView budgetCategoriesList;
    private TextView monthYearText, totalBudgetText, remainingBudgetText;
    private ImageButton previousMonthBtn, nextMonthBtn, addBudgetBtn;
    private LinearLayout noBudgetLayout;
    private Button createFirstBudgetBtn;
    private int currentMonth, currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_management);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        budgetCategoriesList = findViewById(R.id.budget_categories_list);
        monthYearText = findViewById(R.id.tv_budget_month_year);
        totalBudgetText = findViewById(R.id.tv_total_budget);
        remainingBudgetText = findViewById(R.id.tv_remaining_budget);
        previousMonthBtn = findViewById(R.id.btn_previous_budget_month);
        nextMonthBtn = findViewById(R.id.btn_next_budget_month);
        addBudgetBtn = findViewById(R.id.btn_add_budget);
        noBudgetLayout = findViewById(R.id.no_budget_layout);
        createFirstBudgetBtn = findViewById(R.id.btn_create_first_budget);

        // Initialize with current month/year
        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH) + 1; // 1-based
        currentYear = cal.get(Calendar.YEAR);

        setupRecyclerView();
        setupMonthNavigation();
        setupBudgetActions();
        updateMonthYearDisplay();
        observeBudgetData();
    }

    private void setupRecyclerView() {
        budgetCategoriesList.setLayoutManager(new LinearLayoutManager(this));

        // Initialize with empty adapter
        BudgetCategoryAdapter adapter = new BudgetCategoryAdapter();
        adapter.setOnEditClickListener(this::editBudget);
        budgetCategoriesList.setAdapter(adapter);
    }

    private void setupMonthNavigation() {
        previousMonthBtn.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }
            updateMonthYearDisplay();
            observeBudgetData();
        });

        nextMonthBtn.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
            updateMonthYearDisplay();
            observeBudgetData();
        });

        // Click on month/year text to open picker
        monthYearText.setOnClickListener(v -> {
            MonthYearPicker.showMonthYearPicker(this, currentMonth, currentYear,
                (month, year) -> {
                    currentMonth = month;
                    currentYear = year;
                    updateMonthYearDisplay();
                    observeBudgetData();
                });
        });
    }

    private void setupBudgetActions() {
        addBudgetBtn.setOnClickListener(v -> createNewBudget());
        createFirstBudgetBtn.setOnClickListener(v -> createNewBudget());
    }

    private void updateMonthYearDisplay() {
        String displayText = MonthYearPicker.getMonthYearDisplayFull(currentMonth, currentYear);
        monthYearText.setText(displayText);
    }

    private void observeBudgetData() {
        // TODO: Implement budget data observation
        // For now, show no data state
        showNoBudgetState();
    }

    private void showNoBudgetState() {
        budgetCategoriesList.setVisibility(RecyclerView.GONE);
        noBudgetLayout.setVisibility(LinearLayout.VISIBLE);
        totalBudgetText.setText("₹0");
        remainingBudgetText.setText("₹0");
        remainingBudgetText.setTextColor(Color.parseColor("#2196F3"));
    }

    private void showBudgetData(java.util.List<MonthlyBudgetDao.BudgetSummary> budgets) {
        budgetCategoriesList.setVisibility(RecyclerView.VISIBLE);
        noBudgetLayout.setVisibility(LinearLayout.GONE);

        BudgetCategoryAdapter adapter = (BudgetCategoryAdapter) budgetCategoriesList.getAdapter();
        adapter.setBudgets(budgets);

        // Calculate totals
        double totalBudget = 0;
        double totalSpent = 0;
        for (MonthlyBudgetDao.BudgetSummary budget : budgets) {
            totalBudget += budget.budgetAmount;
            totalSpent += budget.spentAmount;
        }

        double remaining = totalBudget - totalSpent;

        totalBudgetText.setText(String.format("₹%.0f", totalBudget));
        remainingBudgetText.setText(String.format("₹%.0f", remaining));

        // Color coding for remaining budget
        if (remaining < 0) {
            remainingBudgetText.setTextColor(Color.parseColor("#F44336")); // Red for over budget
        } else if (remaining < totalBudget * 0.2) {
            remainingBudgetText.setTextColor(Color.parseColor("#FF9800")); // Orange for low budget
        } else {
            remainingBudgetText.setTextColor(Color.parseColor("#4CAF50")); // Green for good budget
        }
    }

    private void createNewBudget() {
        // TODO: Open budget creation dialog
        android.util.Log.d("BudgetManagement", "Create new budget clicked");
    }

    private void editBudget(MonthlyBudgetDao.BudgetSummary budget) {
        // TODO: Open budget edit dialog
        android.util.Log.d("BudgetManagement", "Edit budget clicked for: " + budget.categoryName);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}