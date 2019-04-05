package com.sqli.imputation.service.util;

public final class DateUtil {

    public static final String DELIMITER = "-";
    public static final int FIRST_POSITION = 0;
    private static final int SECOND_POSITION = 1;
    private static final int THIRD_POSITION = 2;

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
}
