package com.flowpocket.util;

import android.graphics.Color;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import java.util.List;

public class ChartManager {
    private final PieChart chart;

    public ChartManager(PieChart chart) {
        this.chart = chart;
        setupChart();
    }

    private void setupChart() {
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDescription(null);
        chart.setDrawEntryLabels(true);
    }

    public void updateChart(double income, double expenses) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float)expenses, "Expenses"));
        entries.add(new PieEntry((float)(income - expenses), "Remaining"));

        PieDataSet dataSet = new PieDataSet(entries, "Monthly Overview");
        dataSet.setColors(Color.RED, Color.GREEN);
        
        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        
        chart.setData(data);
        chart.invalidate();
    }
}
