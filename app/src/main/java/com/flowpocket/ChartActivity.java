package com.flowpocket;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.flowpocket.viewmodel.ExpenseViewModel;
import com.flowpocket.viewmodel.IncomeViewModel;
import com.flowpocket.util.ChartManager;
import com.github.mikephil.charting.charts.PieChart;

import java.util.Calendar;
import java.util.Date;

public class ChartActivity extends AppCompatActivity {
    private ExpenseViewModel expenseViewModel;
    private IncomeViewModel incomeViewModel;
    private PieChart pieChart;
    private ChartManager chartManager;
    private TextView incomeAmountText;
    private TextView expensesAmountText;
    private TextView remainingAmountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_chart);

            initViews();
            setupToolbar();
            setupChart();
            observeData();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void initViews() {
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        incomeViewModel = new ViewModelProvider(this).get(IncomeViewModel.class);
        pieChart = findViewById(R.id.pie_chart);
        incomeAmountText = findViewById(R.id.chart_income_amount);
        expensesAmountText = findViewById(R.id.chart_expenses_amount);
        remainingAmountText = findViewById(R.id.chart_remaining_amount);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Expense Chart");
        }
    }

    private void setupChart() {
        try {
            chartManager = new ChartManager(pieChart);
            // Improve chart appearance
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleRadius(45f);
            pieChart.setTransparentCircleRadius(50f);
            pieChart.setDescription(null);
            pieChart.setDrawEntryLabels(false); // Remove labels that overlap
            pieChart.getLegend().setEnabled(true); // Show legend instead
        } catch (Exception e) {
            // Handle chart initialization errors gracefully
            pieChart.setVisibility(android.view.View.GONE);
        }
    }

    private void observeData() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        // Observe expenses
        expenseViewModel.getMonthlyTotal(startDate, endDate).observe(this, totalExpenses -> {
            double expenseAmount = totalExpenses != null ? totalExpenses : 0.0;
            expensesAmountText.setText(String.format("₹%.0f", expenseAmount));
            updateChart(expenseAmount);
        });

        // Observe income
        incomeViewModel.getCurrentMonthIncome().observe(this, income -> {
            double incomeAmount = (income != null) ? income.getAmount() : 0.0;
            incomeAmountText.setText(String.format("₹%.0f", incomeAmount));
            updateChart(incomeAmount);
        });
    }

    private void updateChart(double amount) {
        // Get both income and expenses to update chart
        incomeViewModel.getCurrentMonthIncome().observe(this, income -> {
            double incomeAmount = (income != null) ? income.getAmount() : 0.0;

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date startDate = cal.getTime();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date endDate = cal.getTime();

            expenseViewModel.getMonthlyTotal(startDate, endDate).observe(this, totalExpenses -> {
                double expenseAmount = totalExpenses != null ? totalExpenses : 0.0;
                double remaining = incomeAmount - expenseAmount;

                remainingAmountText.setText(String.format("₹%.0f", remaining));
                if (chartManager != null) {
                    chartManager.updateChart(incomeAmount, expenseAmount);
                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}