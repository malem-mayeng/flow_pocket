package com.flowpocket.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import com.flowpocket.R;
import java.util.Calendar;

public class MonthYearPicker {

    public interface OnDateSelectedListener {
        void onDateSelected(int month, int year);
    }

    public static void showMonthYearPicker(Context context, int currentMonth, int currentYear,
                                          OnDateSelectedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_month_year_picker, null);

        NumberPicker monthPicker = view.findViewById(R.id.month_picker);
        NumberPicker yearPicker = view.findViewById(R.id.year_picker);

        // Setup month picker (1-12, but display names)
        String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setDisplayedValues(months);
        monthPicker.setValue(currentMonth);
        monthPicker.setWrapSelectorWheel(true);

        // Setup year picker (reasonable range)
        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(2020);
        yearPicker.setMaxValue(thisYear + 5); // Allow future planning
        yearPicker.setValue(currentYear);
        yearPicker.setWrapSelectorWheel(false);

        builder.setView(view)
               .setTitle("Select Month & Year")
               .setPositiveButton("OK", (dialog, which) -> {
                   int selectedMonth = monthPicker.getValue();
                   int selectedYear = yearPicker.getValue();
                   listener.onDateSelected(selectedMonth, selectedYear);
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    public static String getMonthYearDisplay(int month, int year) {
        String[] months = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        if (month >= 1 && month <= 12) {
            return months[month - 1] + " " + year;
        }
        return "Invalid Date";
    }

    public static String getMonthYearDisplayFull(int month, int year) {
        String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        if (month >= 1 && month <= 12) {
            return months[month - 1] + " " + year;
        }
        return "Invalid Date";
    }

    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
}