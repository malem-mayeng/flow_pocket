package com.flowpocket.util;

import android.graphics.Color;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import java.util.List;

public class MiniChartManager {
    private final PieChart chart;

    public MiniChartManager(PieChart chart) {
        this.chart = chart;
        setupChart();
    }

    private void setupChart() {
        // ABSOLUTE MAXIMUM chart optimization - eliminate ALL spacing
        chart.setDrawHoleEnabled(false);  // NO HOLE - solid pie chart
        chart.setDescription(null);
        chart.setDrawEntryLabels(false);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(false);

        // NUCLEAR option - remove every possible space waster
        chart.setExtraOffsets(0f, 0f, 0f, 0f);
        chart.setMinOffset(0f);
        chart.setNoDataText("");
        chart.setDrawCenterText(false);
        chart.setMaxAngle(360f);
        chart.setRotationAngle(0f);

        // EXTREME viewport optimization
        chart.setDrawMarkers(false);

        // Force absolute maximum chart area usage
        try {
            chart.getViewPortHandler().setMaximumScaleX(1f);
            chart.getViewPortHandler().setMaximumScaleY(1f);
            chart.getViewPortHandler().setMinimumScaleX(1f);
            chart.getViewPortHandler().setMinimumScaleY(1f);
        } catch (Exception e) {
            // Ignore if not available
        }
    }

    public void updateChart(double income, double expenses) {
        try {
            if (income <= 0) {
                showNoDataState();
                return;
            }

            List<PieEntry> entries = new ArrayList<>();
            double remaining = income - expenses;

            if (expenses > 0) {
                entries.add(new PieEntry((float)expenses, "Expenses"));
            }
            if (remaining > 0) {
                entries.add(new PieEntry((float)remaining, "Remaining"));
            }

            if (entries.isEmpty()) {
                showNoDataState();
                return;
            }

            PieDataSet dataSet = new PieDataSet(entries, "");

            // Set colors
            List<Integer> colors = new ArrayList<>();
            if (expenses > 0) colors.add(Color.parseColor("#F44336")); // Red for expenses
            if (remaining > 0) colors.add(Color.parseColor("#4CAF50")); // Green for remaining

            dataSet.setColors(colors);
            dataSet.setDrawValues(false);
            dataSet.setSliceSpace(0f);  // NO space between slices for maximum area
            dataSet.setSelectionShift(0f);  // No shift when selected

            PieData data = new PieData(dataSet);
            chart.setData(data);
            chart.invalidate();

        } catch (Exception e) {
            // Log error but show no data state
            android.util.Log.e("MiniChart", "Error updating chart", e);
            showNoDataState();
        }
    }

    private void showNoDataState() {
        // Show a greyed out circle for "No Data"
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(1f, "No Data"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColor(Color.parseColor("#CCCCCC")); // Light grey
        dataSet.setDrawValues(false);
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        chart.setData(data);
        chart.invalidate();
    }
}