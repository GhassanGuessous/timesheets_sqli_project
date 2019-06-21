package com.sqli.imputation.service.dto.jira;

public class PpmcProjectWorklogDTO {

    private String ppmcProject;
    private String timeSpent;
    private int totalMinutes;

    public String getPpmcProject() {
        return ppmcProject;
    }

    public void setPpmcProject(String ppmcProject) {
        this.ppmcProject = ppmcProject;
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

    @Override
    public String toString() {
        return "PpmcProjectWorklogDTO{" +
            "ppmcProject='" + ppmcProject + '\'' +
            ", timeSpent='" + timeSpent + '\'' +
            ", totalMinutes=" + totalMinutes +
            '}';
    }
}
