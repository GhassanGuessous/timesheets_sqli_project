package com.sqli.imputation.service.dto;

public class NotificationGapVariationDTO {

    public static final int INITIAL_FREQUENCE = 1;
    private int year;
    private int month;
    private int frequence;

    public NotificationGapVariationDTO() {
    }

    public NotificationGapVariationDTO(int year, int month, int frequence) {
        this.year = year;
        this.month = month;
        this.frequence = frequence;
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

    public int getFrequence() {
        return frequence;
    }

    public void setFrequence(int frequence) {
        this.frequence = frequence;
    }

    @Override
    public String toString() {
        return "NotificationGapVariationDTO{" +
            "year=" + year +
            ", month=" + month +
            ", frequence=" + frequence +
            '}';
    }

    public void incrementFrecuency() {
        this.frequence += INITIAL_FREQUENCE;
    }
}
