package com.sqli.imputation.service.dto.jira;

public class PpmcProjectWorklogDTO {

    public static final int FREQUANCY_STEP = 1;
    private String ppmcProject;
    private String timeSpent;
    private int totalMinutes;
    private int frequency;

    public PpmcProjectWorklogDTO(String ppmcProject, int frequancy) {
        this.ppmcProject = ppmcProject;
        this.frequency = frequancy;
    }

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
        return "PpmcProjectWorklogDTO{" +
            "ppmcProject='" + ppmcProject + '\'' +
            ", timeSpent='" + timeSpent + '\'' +
            ", totalMinutes=" + totalMinutes +
            ", frequency=" + frequency +
            '}';
    }
}
