package com.sqli.imputation.service.dto;

public class TbpPeriodDTO {
    private String startDate;
    private String endDate;
    private int numberMonths;

    public TbpPeriodDTO(String startDate, String endDate, int numberMonths) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberMonths = numberMonths;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getNumberMonths() {
        return numberMonths;
    }
}
