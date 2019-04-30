package com.sqli.imputation.service.dto;

public class AppRequestDTO {

    private String agresso;
    private int year;
    private int month;
    private int manDay;
    private int startDay;

    public String getAgresso() {
        return agresso;
    }

    public void setAgresso(String agresso) {
        this.agresso = agresso;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getManDay() {
        return manDay;
    }

    public AppRequestDTO() {
    }

    public AppRequestDTO(String agresso, int year, int month, int startDay, int manDay) {
        this.agresso = agresso;
        this.year = year;
        this.month = month;
        this.startDay = startDay;
        this.manDay = manDay;
    }

    public AppRequestDTO(String agresso, int year, int month) {
        this.agresso = agresso;
        this.year = year;
        this.month = month;
    }

    public void setManDay(int manDay) {
        this.manDay = manDay;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    @Override
    public String toString() {
        return "AppRequestDTO{" +
            "agresso='" + agresso + '\'' +
            ", year=" + year +
            ", month=" + month +
            ", manDay=" + manDay +
            ", startDay=" + startDay +
            '}';
    }
}
