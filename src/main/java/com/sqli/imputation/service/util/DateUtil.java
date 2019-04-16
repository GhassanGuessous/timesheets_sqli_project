package com.sqli.imputation.service.util;

import javax.xml.stream.events.StartDocument;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;

public final class DateUtil {

    public static final String DELIMITER = "-";
    public static final int FIRST_POSITION = 0;
    private static final int SECOND_POSITION = 1;
    private static final int THIRD_POSITION = 2;
    public static final String PATTERN = "yyyy-MM-dd";
    public static final String START = "start";
    public static final String END = "end";
    public static final int FIRST_DAY_OF_MONTH = 1;
    public static final int FIRST_MONTH = 0;

    public static int getYear(String date){
        String[] tokens = date.split(DELIMITER);
        return Integer.parseInt(tokens[FIRST_POSITION]);
    }

    public static int getMonth(String date){
        String[] tokens = date.split(DELIMITER);
        return Integer.parseInt(tokens[SECOND_POSITION]);
    }

    public static int getDay(String date){
        String[] tokens = date.split(DELIMITER);
        return Integer.parseInt(tokens[THIRD_POSITION]);
    }

    public static boolean isDatesOrderNotValid(String startDate, String endDate){
        DateFormat dateFormat = new SimpleDateFormat(PATTERN);
        try {
            Date date1 = dateFormat.parse(startDate);
            Date date2 = dateFormat.parse(endDate);
            return date1.getTime() > date2.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getNumberMonthsBetweenDates(String startDate, String endDate) {
        return getMonth(endDate) - getMonth(startDate);
    }

    public static Map<Integer, Map<String, Integer>> getMonths(String startDate, String endDate, int numberMonths) {
        int startMonth = getMonth(startDate);
        int year = getYear(startDate);
        Map<Integer, Map<String, Integer>> months = new HashMap<>();

        for (int i = FIRST_MONTH; i <= numberMonths; i++) {
            Map<String, Integer> start_end_days = new HashMap<>();
            if(i == FIRST_MONTH){
                start_end_days.put(START, getDay(startDate));
                start_end_days.put(END, getLastDayOfMonth(year, startMonth));
            }else if(i == numberMonths){
                start_end_days.put(START, FIRST_DAY_OF_MONTH);
                start_end_days.put(END, getDay(endDate));
            }else{
                start_end_days.put(START, FIRST_DAY_OF_MONTH);
                start_end_days.put(END, getLastDayOfMonth(year, startMonth));
            }
            months.put(startMonth, start_end_days);
            startMonth++;
        }
        return months;
    }

    public static int getLastDayOfMonth(int year, int month){
        return YearMonth.of(year, month).lengthOfMonth();
    }

    public static boolean isDifferentYears(String startDate, String endDate) {
        return getYear(startDate) != getYear(endDate);
    }
}
