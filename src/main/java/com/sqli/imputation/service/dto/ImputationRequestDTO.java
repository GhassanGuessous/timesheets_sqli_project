package com.sqli.imputation.service.dto;

public class ImputationRequestDTO {

    private String agresso;
    private int year;
    private int month;
    private String type;

    public ImputationRequestDTO(String agresso, int year, int month, String type) {
        this.agresso = agresso;
        this.year = year;
        this.month = month;
        this.type = type;
    }

    public String getAgresso() {
        return agresso;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public String getType() {
        return type;
    }
}
