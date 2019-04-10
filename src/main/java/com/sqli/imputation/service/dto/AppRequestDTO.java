package com.sqli.imputation.service.dto;

public class AppRequestDTO {

    private String agresso;
    private int year;
    private int month;

    public int getManDay() {
        return manDay;
    }

    public void setManDay(int manDay) {
        this.manDay = manDay;
    }

    private int manDay;

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

    @Override
    public String toString() {
        return "AppRequestDTO{" +
            "agresso='" + agresso + '\'' +
            ", year=" + year +
            ", month=" + month +
            ", manDay=" + manDay +
            '}';
    }
}
