package com.sqli.imputation.service.util;

import com.sqli.imputation.service.dto.TbpPeriodDTO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;

public class DateUtil {

    private static final String DELIMITER = "-";
    private static final int FIRST_POSITION = 0;
    private static final int SECOND_POSITION = 1;
    private static final int THIRD_POSITION = 2;
    private static final String PATTERN = "yyyy-MM-dd";
    private static final String START_STRING = "start";
    private static final String END_STRING = "end";
    private static final int FIRST_DAY_OF_MONTH = 1;
    private static final int FIRST_MONTH = 0;
    private static final String TWO_DIGITS_FORMAT = "%02d";

    public static int getYear(String date) {
        String[] tokens = date.split(DELIMITER);
        return Integer.parseInt(tokens[FIRST_POSITION]);
    }

    public static int getMonth(String date) {
        String[] tokens = date.split(DELIMITER);
        return Integer.parseInt(tokens[SECOND_POSITION]);
    }

    public static int getDay(String date) {
        String[] tokens = date.split(DELIMITER);
        return Integer.parseInt(tokens[THIRD_POSITION]);
    }

    public static boolean isDatesOrderNotValid(String startDate, String endDate) {
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

    /**
     * this function return a map representing months
     * of the TbpPeriodDTO (input) as follow :
     * 1 :
     *      start : 01
     *      end : 30
     * 2 :
     *      start : 01
     *      end : 28
     *
     * @param tbpPeriodDTO
     * @return
     */
    public static Map<Integer, Map<String, Integer>> getStartEndDatesOfMonths(TbpPeriodDTO tbpPeriodDTO) {
        int startMonth = getMonth(tbpPeriodDTO.getStartDate());
        int year = getYear(tbpPeriodDTO.getStartDate());
        Map<Integer, Map<String, Integer>> months = new HashMap<>();

        for (int i = FIRST_MONTH; i <= tbpPeriodDTO.getNumberMonths(); i++) {
            Map<String, Integer> startEndDays = new HashMap<>();
            if (i == FIRST_MONTH) {
                startEndDays.put(START_STRING, getDay(tbpPeriodDTO.getStartDate()));
                startEndDays.put(END_STRING, getLastDayOfMonth(year, startMonth));
            } else if (i == tbpPeriodDTO.getNumberMonths()) {
                startEndDays.put(START_STRING, FIRST_DAY_OF_MONTH);
                startEndDays.put(END_STRING, getDay(tbpPeriodDTO.getEndDate()));
            } else {
                startEndDays.put(START_STRING, FIRST_DAY_OF_MONTH);
                startEndDays.put(END_STRING, getLastDayOfMonth(year, startMonth));
            }
            months.put(startMonth, startEndDays);
            startMonth++;
        }
        return months;
    }

    public static int getLastDayOfMonth(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    public static boolean isDifferentYears(String startDate, String endDate) {
        return getYear(startDate) != getYear(endDate);
    }

    public static String getDateOfFirstDay(int year, int month) {
        StringBuilder builder = new StringBuilder();
        builder.append(year).append(DELIMITER).append(toTwoDidit(month)).append(DELIMITER).append(toTwoDidit(FIRST_DAY_OF_MONTH));
        return builder.toString();
    }

    public static String getDateOfLastDay(int year, int month) {
        StringBuilder builder = new StringBuilder();
        builder.append(year).append(DELIMITER).append(toTwoDidit(month)).append(DELIMITER).append(getLastDayOfMonth(year, month));
        return builder.toString();
    }

    public static String toTwoDidit(int number) {
        return String.format(TWO_DIGITS_FORMAT, number);
    }

    public static boolean isNotValidDates(String startDate, String endDate) {
        return (startDate == null || endDate == null) || (startDate.isEmpty() || endDate.isEmpty());
    }
}
