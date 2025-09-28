package com.flowpocket;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.flowpocket.viewmodel.ExpenseViewModel;
import com.flowpocket.viewmodel.IncomeViewModel;
import com.flowpocket.ui.SimpleExpenseAdapter;
import com.flowpocket.dialog.AddExpenseDialog;
import com.flowpocket.dialog.AddIncomeDialog;
import com.flowpocket.dialog.EditExpenseDialog;
import com.flowpocket.entities.Expense;
import com.flowpocket.util.MiniChartManager;
import com.flowpocket.utils.MonthYearPicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton;
import com.github.mikephil.charting.charts.PieChart;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ExpenseViewModel viewModel;
    private IncomeViewModel incomeViewModel;
    private SimpleExpenseAdapter expenseAdapter;
    private RecyclerView expenseList;
    private TextView balanceText;
    private TextView incomeText;
    private MiniChartManager miniChartManager;

    // Month/Year picker variables
    private TextView selectedMonthYearText;
    private int currentSelectedMonth;
    private int currentSelectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide default ActionBar since we have custom title
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupRecyclerView();
        setupFab();
        setupButtons();
        setupMiniChart();
        observeData();
    }

    private TextView expensesText;
    private TextView expensesSectionTitle;
    private boolean showingAllExpenses = false;
    private MaterialButton viewAllExpensesBtn;
    private com.google.android.material.appbar.AppBarLayout appBarLayout;
    private androidx.constraintlayout.widget.ConstraintLayout mainContent;

    private void initViews() {
        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        incomeViewModel = new ViewModelProvider(this).get(IncomeViewModel.class);
        expenseAdapter = new SimpleExpenseAdapter();
        expenseList = findViewById(R.id.expenses_recycler_view);
        balanceText = findViewById(R.id.remaining_amount);
        incomeText = findViewById(R.id.income_amount);
        expensesText = findViewById(R.id.expenses_amount);
        expensesSectionTitle = findViewById(R.id.expenses_section_title);
        viewAllExpensesBtn = findViewById(R.id.btn_view_all_expenses);
        appBarLayout = findViewById(R.id.app_bar_layout);
        mainContent = findViewById(R.id.main_content);
        selectedMonthYearText = findViewById(R.id.tv_selected_month_year);

        // Initialize with current month/year
        currentSelectedMonth = MonthYearPicker.getCurrentMonth();
        currentSelectedYear = MonthYearPicker.getCurrentYear();
        updateMonthYearDisplay();
    }

    private void setupRecyclerView() {
        expenseList.setLayoutManager(new LinearLayoutManager(this));
        expenseList.setAdapter(expenseAdapter);

        // Set viewModel for dynamic category names
        expenseAdapter.setViewModel(viewModel);

        // Set click listener for expense items
        expenseAdapter.setOnExpenseClickListener(expense -> showEditExpenseDialog(expense));
    }


    private void setupFab() {
        FloatingActionButton fab = findViewById(R.id.fab_add_expense);
        fab.setOnClickListener(v -> showAddExpenseDialog());
    }

    private void setupButtons() {
        // Edit income icon click
        ImageView editIncomeIcon = findViewById(R.id.edit_income_icon);
        editIncomeIcon.setOnClickListener(v -> showAddIncomeDialog());


        viewAllExpensesBtn.setOnClickListener(v -> {
            // Toggle between showing recent (3) and all expenses
            showAllExpenses();
        });

        // Month/Year picker click listener
        selectedMonthYearText.setOnClickListener(v -> {
            MonthYearPicker.showMonthYearPicker(this, currentSelectedMonth, currentSelectedYear,
                (month, year) -> {
                    currentSelectedMonth = month;
                    currentSelectedYear = year;
                    updateMonthYearDisplay();
                    refreshDataForSelectedMonth();
                });
        });
    }

    private void setupMiniChart() {
        try {
            PieChart miniChart = findViewById(R.id.mini_pie_chart);
            miniChartManager = new MiniChartManager(miniChart);

            // Make chart clickable to open detailed chart
            miniChart.setOnClickListener(v -> {
                Intent intent = new Intent(this, CategoryChartActivity.class);
                intent.putExtra("selected_month", currentSelectedMonth);
                intent.putExtra("selected_year", currentSelectedYear);
                startActivity(intent);
            });
        } catch (Exception e) {
            // Handle chart setup errors gracefully
        }
    }

    private void showAddIncomeDialog() {
        AddIncomeDialog dialog = new AddIncomeDialog();
        dialog.show(getSupportFragmentManager(), "AddIncome");
    }

    private void observeData() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, currentSelectedYear);
        cal.set(Calendar.MONTH, currentSelectedMonth - 1); // Calendar months are 0-based
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        viewModel.getMonthlyExpenses(startDate, endDate).observe(this, expenses -> {
            if (expenses != null) {
                updateExpenseList(expenses);
            }
        });

        viewModel.getMonthlyTotal(startDate, endDate).observe(this, totalExpenses -> {
            double expenseAmount = totalExpenses != null ? totalExpenses : 0.0;
            expensesText.setText(String.format("₹%.0f", expenseAmount));
            updateBalanceDisplay(expenseAmount);
        });

        incomeViewModel.getCurrentMonthIncome().observe(this, income -> {
            if (income != null) {
                incomeText.setText(String.format("₹%.0f", income.getAmount()));
            }
        });
    }

    private void updateBalanceDisplay(double totalExpenses) {
        // Calculate remaining balance
        incomeViewModel.getCurrentMonthIncome().observe(this, income -> {
            double incomeAmount = (income != null) ? income.getAmount() : 0.0;
            double remainingBalance = incomeAmount - totalExpenses;
            balanceText.setText(String.format("₹%.0f", remainingBalance));

            // Update mini chart
            if (miniChartManager != null) {
                miniChartManager.updateChart(incomeAmount, totalExpenses);
            }
        });
    }


    private void showAddExpenseDialog() {
        AddExpenseDialog dialog = new AddExpenseDialog();
        dialog.show(getSupportFragmentManager(), "AddExpense");
    }

    private void showEditExpenseDialog(Expense expense) {
        EditExpenseDialog dialog = EditExpenseDialog.newInstance(expense);
        dialog.setOnExpenseUpdatedListener(new EditExpenseDialog.OnExpenseUpdatedListener() {
            @Override
            public void onExpenseUpdated() {
                // Refresh data after update
                refreshExpenseData();
            }

            @Override
            public void onExpenseDeleted() {
                // Refresh data after deletion
                refreshExpenseData();
            }
        });
        dialog.show(getSupportFragmentManager(), "EditExpense");
    }

    private void refreshExpenseData() {
        // Trigger observers to refresh data for selected month/year
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, currentSelectedYear);
        cal.set(Calendar.MONTH, currentSelectedMonth - 1); // Calendar months are 0-based
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        if (showingAllExpenses) {
            // Load all expenses for selected month (not all-time)
            viewModel.getMonthlyExpenses(startDate, endDate).observe(this, expenses -> {
                if (expenses != null) {
                    expenseAdapter.setExpenses(expenses); // Show all expenses for selected month
                }
            });
        } else {
            viewModel.getMonthlyExpenses(startDate, endDate).observe(this, expenses -> {
                if (expenses != null) {
                    updateExpenseList(expenses);
                }
            });
        }
    }

    private void showAllExpenses() {
        showingAllExpenses = !showingAllExpenses;

        if (showingAllExpenses) {
            // FULLSCREEN MODE - Hide header and expand content
            viewAllExpensesBtn.setText("Show Recent");
            expensesSectionTitle.setText("All Expenses");
            // Clear all button states to avoid visual delay
            viewAllExpensesBtn.clearFocus();
            viewAllExpensesBtn.setPressed(false);
            viewAllExpensesBtn.getBackground().jumpToCurrentState();
            viewAllExpensesBtn.refreshDrawableState();
            appBarLayout.setVisibility(android.view.View.GONE);

            // Remove scrolling behavior so content uses full screen
            androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams params =
                (androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams) mainContent.getLayoutParams();
            params.setBehavior(null);
            mainContent.setLayoutParams(params);

            // Load all expenses for selected month (not all-time)
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, currentSelectedYear);
            cal.set(Calendar.MONTH, currentSelectedMonth - 1); // Calendar months are 0-based
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date startDate = cal.getTime();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date endDate = cal.getTime();

            viewModel.getMonthlyExpenses(startDate, endDate).observe(this, expenses -> {
                if (expenses != null) {
                    expenseAdapter.setExpenses(expenses); // Show all expenses for selected month
                }
            });
        } else {
            // NORMAL MODE - Show header and restore scrolling behavior
            viewAllExpensesBtn.setText("View All");
            expensesSectionTitle.setText("Recent Expenses");
            // Clear all button states to avoid visual delay
            viewAllExpensesBtn.clearFocus();
            viewAllExpensesBtn.setPressed(false);
            viewAllExpensesBtn.getBackground().jumpToCurrentState();
            viewAllExpensesBtn.refreshDrawableState();
            appBarLayout.setVisibility(android.view.View.VISIBLE);

            // Restore scrolling behavior
            androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams params =
                (androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams) mainContent.getLayoutParams();
            params.setBehavior(new com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior());
            mainContent.setLayoutParams(params);

            // Reload recent expenses for selected month/year (trigger existing observer)
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, currentSelectedYear);
            cal.set(Calendar.MONTH, currentSelectedMonth - 1); // Calendar months are 0-based
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date startDate = cal.getTime();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date endDate = cal.getTime();

            viewModel.getMonthlyExpenses(startDate, endDate).observe(this, expenses -> {
                if (expenses != null) {
                    updateExpenseList(expenses);
                }
            });
        }
    }

    private void updateExpenseList(List<Expense> expenses) {
        if (showingAllExpenses) {
            // Show all expenses
            expenseAdapter.setExpenses(expenses);
        } else {
            // Show only the last 5 expenses on home screen
            List<Expense> recentExpenses = expenses.size() > 5 ?
                expenses.subList(0, 5) : expenses;
            expenseAdapter.setExpenses(recentExpenses);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_income) {
            showAddIncomeDialog();
            return true;
        } else if (id == R.id.action_statistics) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_budget_management) {
            Intent intent = new Intent(this, BudgetManagementActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (showingAllExpenses) {
            // If in "View All" mode, go back to recent view
            showAllExpenses(); // This will toggle back to recent mode
        } else {
            // Normal back behavior
            super.onBackPressed();
        }
    }

    private void updateMonthYearDisplay() {
        String displayText = MonthYearPicker.getMonthYearDisplay(currentSelectedMonth, currentSelectedYear);
        selectedMonthYearText.setText(displayText);
    }

    private void refreshDataForSelectedMonth() {
        // Force refresh all data observers with selected month/year
        observeData();
    }
}