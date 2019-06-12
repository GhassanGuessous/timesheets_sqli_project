package com.sqli.imputation.service.dto.jira;

public class JiraDailyWorklog {

    private String date;
    private int day;
    private TimeSpentDTO timeSpent;
    private  String worklogTimeSpent;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public TimeSpentDTO getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(TimeSpentDTO timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getWorklogTimeSpent() {
        return worklogTimeSpent;
    }

    public void setWorklogTimeSpent(String worklogTimeSpent) {
        this.worklogTimeSpent = worklogTimeSpent;
    }

    @Override
    public String toString() {
        return "JiraDailyWorklog{" +
            "date='" + date + '\'' +
            ", day=" + day +
            ", timeSpent=" + timeSpent +
            ", worklogTimeSpent='" + worklogTimeSpent + '\'' +
            '}';
    }
}
