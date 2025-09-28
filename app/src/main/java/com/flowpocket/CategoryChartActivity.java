package com.flowpocket;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.flowpocket.ui.CategoryLegendAdapter;
import com.flowpocket.viewmodel.ExpenseViewModel;
import com.flowpocket.dao.CategoryExpenseSummary;
import com.flowpocket.utils.MonthYearPicker;
import android.widget.TextView;
import android.widget.ImageButton;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryChartActivity extends AppCompatActivity {
    private ExpenseViewModel expenseViewModel;
    private PieChart pieChart;
    private RecyclerView legendRecyclerView;
    private TextView monthYearText;
    private ImageButton previousMonthBtn, nextMonthBtn;
    private int currentMonth, currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_chart);

        // Hide default ActionBar since we have custom header
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        pieChart = findViewById(R.id.category_pie_chart);
        legendRecyclerView = findViewById(R.id.category_legend_list);
        monthYearText = findViewById(R.id.tv_current_month_year);
        previousMonthBtn = findViewById(R.id.btn_previous_month);
        nextMonthBtn = findViewById(R.id.btn_next_month);

        // Initialize with passed month/year from MainActivity, or current month/year as fallback
        Intent intent = getIntent();
        if (intent.hasExtra("selected_month") && intent.hasExtra("selected_year")) {
            currentMonth = intent.getIntExtra("selected_month", MonthYearPicker.getCurrentMonth());
            currentYear = intent.getIntExtra("selected_year", MonthYearPicker.getCurrentYear());
        } else {
            // Fallback to current month/year
            Calendar cal = Calendar.getInstance();
            currentMonth = cal.get(Calendar.MONTH) + 1; // 1-based
            currentYear = cal.get(Calendar.YEAR);
        }

        setupChart();
        setupMonthNavigation();
        updateMonthYearDisplay();
        observeExpenseData();
    }

    private void setupChart() {
        // Basic chart setup - data will be loaded in observeExpenseData
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setTouchEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // Setup legend RecyclerView
        legendRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Show initial "No data" state
        showNoDataState();
    }

    private void observeExpenseData() {
        loadDataForCurrentMonth();
    }

    private void loadDataForCurrentMonth() {
        // Get date range for current selected month/year
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, currentYear);
        cal.set(Calendar.MONTH, currentMonth - 1); // Calendar is 0-based
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        expenseViewModel.getCategoryExpenseSummary(startDate, endDate).observe(this, categoryData -> {
            // Add debug logging
            android.util.Log.d("CategoryChart", "Data received: " + (categoryData != null ? categoryData.size() : "null"));

            if (categoryData != null && !categoryData.isEmpty()) {
                for (CategoryExpenseSummary summary : categoryData) {
                    android.util.Log.d("CategoryChart", "Category: " + summary.getCategoryName() +
                        ", Amount: " + summary.getTotalAmount() +
                        ", Color: " + summary.getCategoryColor());
                }
                updateChartWithRealData(categoryData);
            } else {
                android.util.Log.d("CategoryChart", "No data found, showing no data state");
                showNoDataState();
            }
        });
    }

    private void updateChartWithRealData(List<CategoryExpenseSummary> categoryData) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        List<CategoryLegendAdapter.CategoryItem> legendItems = new ArrayList<>();
        List<String> categoryNames = new ArrayList<>(); // Store category names separately
        DecimalFormat percentFormat = new DecimalFormat("0.0%");

        // Calculate total for percentages
        double total = 0;
        for (CategoryExpenseSummary summary : categoryData) {
            total += summary.getTotalAmount();
        }

        // Final variable for use in listener
        final double finalTotal = total;

        // Create unique color palette for chart - guaranteed no duplicates
        String[] uniqueColorPalette = {
            "#2196F3", "#F44336", "#FF9800", "#4CAF50", "#9C27B0", "#00BCD4", "#607D8B",
            "#E91E63", "#673AB7", "#3F51B5", "#009688", "#8BC34A", "#CDDC39", "#FFC107",
            "#FF5722", "#795548", "#9E9E9E", "#FF4081", "#536DFE", "#40C4FF", "#18FFFF",
            "#69F0AE", "#B2FF59", "#EEFF41", "#FFD740", "#FFAB40", "#FF6E40", "#E040FB",
            "#7C4DFF", "#448AFF", "#1DE9B6", "#64FFDA", "#C6FF00", "#FFFF00", "#FFB300"
        };

        // Convert data to chart entries
        for (int i = 0; i < categoryData.size(); i++) {
            CategoryExpenseSummary summary = categoryData.get(i);
            String category = summary.getCategoryName();
            double amount = summary.getTotalAmount();

            // Assign unique color from palette based on index to ensure no duplicates
            int color;
            try {
                String colorHex = uniqueColorPalette[i % uniqueColorPalette.length];
                color = Color.parseColor(colorHex);
            } catch (Exception e) {
                color = Color.parseColor("#607D8B"); // Default gray
            }

            // Add to pie chart
            entries.add(new PieEntry((float)amount));
            colors.add(color);
            categoryNames.add(category); // Store category name at same index

            // Add to legend with percentage
            double percentage = amount / total;
            String percentText = percentFormat.format(percentage);
            legendItems.add(new CategoryLegendAdapter.CategoryItem(category, color, percentText));
        }

        // Setup pie chart data
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(15f);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(android.graphics.Color.BLACK);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // Clear center text for real data and enable touch
        pieChart.setCenterText("");
        pieChart.setTouchEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // Add selection listener
        pieChart.setOnChartValueSelectedListener(new com.github.mikephil.charting.listener.OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(com.github.mikephil.charting.data.Entry e, com.github.mikephil.charting.highlight.Highlight h) {
                if (e instanceof PieEntry) {
                    PieEntry pieEntry = (PieEntry) e;
                    int selectedIndex = (int) h.getX(); // Get the index of selected slice

                    pieChart.setDrawEntryLabels(true);
                    pieChart.setEntryLabelTextSize(11f);
                    pieChart.setEntryLabelColor(android.graphics.Color.DKGRAY);

                    // Clear all entry labels
                    for (PieEntry entry : entries) {
                        entry.setLabel("");
                    }

                    // Set label for selected slice with bold styling
                    pieEntry.setLabel("₹" + String.format("%.0f", pieEntry.getValue()));

                    // Set category name in center of chart using the index
                    String categoryName = categoryNames.get(selectedIndex);
                    android.util.Log.d("CategoryChart", "Setting center text: " + categoryName);
                    pieChart.setCenterText(categoryName);
                    pieChart.setCenterTextSize(16f);
                    pieChart.setCenterTextColor(android.graphics.Color.DKGRAY);
                    pieChart.setCenterTextTypeface(android.graphics.Typeface.DEFAULT_BOLD);

                    // Make entry labels bold
                    pieChart.setEntryLabelTypeface(android.graphics.Typeface.DEFAULT_BOLD);
                    pieChart.invalidate();
                }
            }

            @Override
            public void onNothingSelected() {
                android.util.Log.d("CategoryChart", "Clearing center text");
                pieChart.setDrawEntryLabels(false);
                pieChart.setCenterText(""); // Clear center text when nothing selected
                pieChart.invalidate();
            }
        });

        // Update legend
        CategoryLegendAdapter adapter = new CategoryLegendAdapter();
        adapter.setItems(legendItems);
        legendRecyclerView.setAdapter(adapter);

        pieChart.invalidate();
    }

    private void showNoDataState() {
        // Show single gray slice with "No data" in center
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        entries.add(new PieEntry(1f, ""));
        colors.add(Color.parseColor("#E0E0E0")); // Light gray

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setTouchEnabled(false);

        // Show "No data" text in the center hole
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(45f);
        pieChart.setCenterText("No data");
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(Color.DKGRAY);

        // Clear legend
        CategoryLegendAdapter adapter = new CategoryLegendAdapter();
        adapter.setItems(new ArrayList<>());
        legendRecyclerView.setAdapter(adapter);

        pieChart.invalidate();
    }

    private void setupMonthNavigation() {
        previousMonthBtn.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }
            updateMonthYearDisplay();
            loadDataForCurrentMonth();
        });

        nextMonthBtn.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
            updateMonthYearDisplay();
            loadDataForCurrentMonth();
        });

        // Click on month/year text to open picker
        monthYearText.setOnClickListener(v -> {
            MonthYearPicker.showMonthYearPicker(this, currentMonth, currentYear,
                (month, year) -> {
                    currentMonth = month;
                    currentYear = year;
                    updateMonthYearDisplay();
                    loadDataForCurrentMonth();
                });
        });
    }

    private void updateMonthYearDisplay() {
        String displayText = MonthYearPicker.getMonthYearDisplay(currentMonth, currentYear);
        monthYearText.setText(displayText);
    }

}