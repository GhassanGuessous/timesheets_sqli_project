package com.sqli.imputation.service.util;

import com.sqli.imputation.service.dto.jira.TimeSpentDTO;

public class TimeSpentCalculatorUtil {

    public static final String TIME_SPENT_SEPARATOR = " ";
    public static final int NUMBRE_OF_MINUTES_IN_HOUR = 60;
    public static final int NUMBER_OF_WORKED_HOURS_IN_DAY = 8;
    public static final int NUMBER_OF_WORKED_DAYS_IN_WEEK = 5;
    public static final int ZERO_MINUTES = 0;

    public static TimeSpentDTO calculate(String timeSpent) {
        int numberOfMinutesSpent = 0;
        String[] timeSpentTokens = timeSpent.split(TIME_SPENT_SEPARATOR);
        for (String token : timeSpentTokens) {
            numberOfMinutesSpent += getNumberOfMinutesFromToken(token);
        }
        return getTimeSpent(numberOfMinutesSpent);
    }

    private static TimeSpentDTO getTimeSpent(int numberOfMinutesSpent) {
        return new TimeSpentDTO(
            numberOfMinutesSpent,
            numberOfMinutesSpent % NUMBRE_OF_MINUTES_IN_HOUR,
            numberOfMinutesSpent / NUMBRE_OF_MINUTES_IN_HOUR % NUMBER_OF_WORKED_HOURS_IN_DAY,
            numberOfMinutesSpent / NUMBER_OF_WORKED_HOURS_IN_DAY / NUMBRE_OF_MINUTES_IN_HOUR % NUMBER_OF_WORKED_DAYS_IN_WEEK,
            (numberOfMinutesSpent / NUMBER_OF_WORKED_HOURS_IN_DAY / NUMBRE_OF_MINUTES_IN_HOUR) / NUMBER_OF_WORKED_DAYS_IN_WEEK);
    }

    private static int getNumberOfMinutesFromToken(String token) {
        if (token.isEmpty()) {
            return ZERO_MINUTES;
        }
        char timeIndicator = token.charAt(token.length() - 1);
        int timespent = Integer.parseInt(token.substring(0, token.indexOf(timeIndicator)));
        return getMinutes(timespent, timeIndicator);
    }

    private static int getMinutes(int timespent, char timeIndicator) {
        switch (timeIndicator) {
            case 'h':
                return timespent * NUMBRE_OF_MINUTES_IN_HOUR;
            case 'd':
                return timespent * NUMBER_OF_WORKED_HOURS_IN_DAY * NUMBRE_OF_MINUTES_IN_HOUR;
            case 'w':
                return timespent * NUMBER_OF_WORKED_DAYS_IN_WEEK * NUMBER_OF_WORKED_HOURS_IN_DAY * NUMBRE_OF_MINUTES_IN_HOUR;
            default:
                return timespent;
        }
    }
}
