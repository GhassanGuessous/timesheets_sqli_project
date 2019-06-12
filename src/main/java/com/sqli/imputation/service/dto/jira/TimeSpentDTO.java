package com.sqli.imputation.service.dto.jira;

public class TimeSpentDTO {

    private int minutes;
    private int hours;
    private int days;
    private int weeks;

    public TimeSpentDTO(int minutes, int hours, int days, int weeks) {
        this.minutes = minutes;
        this.hours = hours;
        this.days = days;
        this.weeks = weeks;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    @Override
    public String toString() {
        return "TimeSpentDTO{" +
            "minutes=" + minutes +
            ", hours=" + hours +
            ", days=" + days +
            ", weeks=" + weeks +
            '}';
    }
}
