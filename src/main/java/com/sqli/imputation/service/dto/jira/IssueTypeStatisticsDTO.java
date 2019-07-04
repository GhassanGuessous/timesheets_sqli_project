package com.sqli.imputation.service.dto.jira;

public class IssueTypeStatisticsDTO {

    public static final int FREQUANCY_STEP = 1;
    private String issueType;
    private String timeSpent;
    private int totalMinutes;
    private int frequency;

    public IssueTypeStatisticsDTO(String issueType) {
        this.issueType = issueType;
        this.frequency = FREQUANCY_STEP;
    }

    public static int getFrequancyStep() {
        return FREQUANCY_STEP;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(int totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void incrementFrequency() {
        this.frequency += FREQUANCY_STEP;
    }

    @Override
    public String toString() {
        return "IssueTypeStatisticsDTO{" +
            "issueType='" + issueType + '\'' +
            ", timeSpent='" + timeSpent + '\'' +
            ", totalMinutes=" + totalMinutes +
            ", frequency=" + frequency +
            '}';
    }
}
