package com.sqli.imputation.service.dto;

public class AppRequestDTO {

    private String agresso;
    private String startDate;
    private String endDate;

    public String getAgresso() {
        return agresso;
    }

    public void setAgresso(String agresso) {
        this.agresso = agresso;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "AppRequestDTO{" +
            "agresso='" + agresso + '\'' +
            ", startDate='" + startDate + '\'' +
            ", endDate='" + endDate + '\'' +
            '}';
    }
}
