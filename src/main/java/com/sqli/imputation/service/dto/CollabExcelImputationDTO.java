package com.sqli.imputation.service.dto;

public class CollabExcelImputationDTO {

    private String collaborator;
    private String projectRequestMisc;
    private int day;
    private double charge;

    public CollabExcelImputationDTO() {
        //default constructer
    }

    public String getProjectRequestMisc() {
        return projectRequestMisc;
    }

    public void setProjectRequestMisc(String projectRequestMisc) {
        this.projectRequestMisc = projectRequestMisc;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public String getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(String collaborator) {
        this.collaborator = collaborator;
    }

    @Override
    public String toString() {
        return "CollabExcelImputationDTO{" +
            "collaborator='" + collaborator + '\'' +
            ", projectRequestMisc='" + projectRequestMisc + '\'' +
            ", day=" + day +
            ", charge=" + charge +
            '}';
    }
}
