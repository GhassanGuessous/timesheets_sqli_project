package com.sqli.imputation.service.dto.jira;

public class TimeSpentDTO {

    private int totalMinutes;
    private int minutes;
    private int hours;
    private int days;
    private int weeks;

    public TimeSpentDTO(int totalMinutes, int minutes, int hours, int days, int weeks) {
        this.totalMinutes = totalMinutes;
        this.minutes = minutes;
        this.hours = hours;
        this.days = days;
        this.weeks = weeks;
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(int totalMinutes) {
        this.totalMinutes = totalMinutes;
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
            "totalMinutes=" + totalMinutes +
            "minutes=" + minutes +
            ", hours=" + hours +
            ", days=" + days +
            ", weeks=" + weeks +
            '}';
    }

    public String toCustomizedString() {
        StringBuilder builder = new StringBuilder();
        builder.append(weeks > 0 ? weeks + "w " : "");
        builder.append(days > 0 ? days + "d " : "");
        builder.append(hours > 0 ? hours + "h " : "");
        builder.append(minutes > 0 ? minutes + "m " : "");
        return builder.toString();
    }
}
