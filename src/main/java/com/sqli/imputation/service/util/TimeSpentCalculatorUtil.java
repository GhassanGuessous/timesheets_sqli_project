package com.sqli.imputation.service.util;

import com.sqli.imputation.service.dto.jira.TimeSpentDTO;

public class TimeSpentCalculatorUtil {

    public static final String TIME_SPENT_SEPARATOR = " ";
    public static final int NUMBRE_OF_MINUTES_IN_HOUR = 60;
    public static final int NUMBER_OF_HOURS_IN_DAY = 8;
    public static final int NUMBER_OF_DAYS_IN_WEEK = 5;
    public static final int ZERO_HOURS = 0;

    public static TimeSpentDTO calculate(String timeSpent) {
        int numberOfHoursSpent = 0;
        String[] timeSpentTokens = timeSpent.split(TIME_SPENT_SEPARATOR);
        for (String token : timeSpentTokens
        ) {

            numberOfHoursSpent += getNumberOfHoursFromToken(token);
        }
        return getTimeSpent(numberOfHoursSpent);
    }

    private static TimeSpentDTO getTimeSpent(int numberOfHoursSpent) {
        return new TimeSpentDTO(
            numberOfHoursSpent % NUMBRE_OF_MINUTES_IN_HOUR,
            numberOfHoursSpent / NUMBRE_OF_MINUTES_IN_HOUR % NUMBER_OF_HOURS_IN_DAY,
            numberOfHoursSpent / NUMBER_OF_HOURS_IN_DAY / NUMBRE_OF_MINUTES_IN_HOUR % NUMBER_OF_DAYS_IN_WEEK,
            (numberOfHoursSpent / NUMBER_OF_HOURS_IN_DAY / NUMBRE_OF_MINUTES_IN_HOUR) / NUMBER_OF_DAYS_IN_WEEK);
    }

    private static int getNumberOfHoursFromToken(String token) {
        if (token.isEmpty()) {
            return ZERO_HOURS;
        }
        char timeIndicator = token.charAt(token.length() - 1);
        int timespent = Integer.parseInt(token.substring(0, token.indexOf(timeIndicator)));
        return getHours(timespent, timeIndicator);
    }

    private static int getHours(int timespent, char timeIndicator) {
        switch (timeIndicator) {
            case 'h':
                return timespent * NUMBRE_OF_MINUTES_IN_HOUR;
            case 'd':
                return timespent * NUMBER_OF_HOURS_IN_DAY * NUMBRE_OF_MINUTES_IN_HOUR;
            case 'w':
                return timespent * NUMBER_OF_DAYS_IN_WEEK * NUMBER_OF_HOURS_IN_DAY * NUMBRE_OF_MINUTES_IN_HOUR;
            default:
                return timespent;
        }
    }
}
