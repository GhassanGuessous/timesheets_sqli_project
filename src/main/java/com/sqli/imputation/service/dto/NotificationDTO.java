package com.sqli.imputation.service.dto;

import com.sqli.imputation.domain.Collaborator;

public class NotificationDTO {
    private Collaborator collaborator;
    private int month;
    private int year;
    private GapDTO appGap;
    private GapDTO comparedGap;

    public NotificationDTO() {
        //default constructor
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public GapDTO getAppGap() {
        return appGap;
    }


    public GapDTO getComparedGap() {
        return comparedGap;
    }

    @Override
    public String toString() {
        return "NotificationDTO{" +
            "collaborator=" + collaborator +
            ", month=" + month +
            ", year=" + year +
            ", appGap=" + appGap +
            ", comparedGap=" + comparedGap +
            '}';
    }
}
