package com.flowpocket.utils;

import java.util.Calendar;

public class DateUtils {
    
    public static long[] getMonthRange(int year, int month) {
        Calendar startCal = Calendar.getInstance();
        startCal.set(year, month - 1, 1, 0, 0, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        
        Calendar endCal = Calendar.getInstance();
        endCal.set(year, month - 1, startCal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        
        return new long[]{startCal.getTimeInMillis(), endCal.getTimeInMillis()};
    }
    
    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }
    
    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
}