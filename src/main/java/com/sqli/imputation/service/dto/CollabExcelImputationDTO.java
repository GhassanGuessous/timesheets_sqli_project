package com.sqli.imputation.service.dto;

public class CollabExcelImputationDTO {

    private String collaborator;
    private String projectRequestMisc;
    private String npdiProject;
    private int day;
    private double charge;

    public CollabExcelImputationDTO() {
    }

    public CollabExcelImputationDTO(String collaborator, String projectRequestMisc, String npdiProject, int day, double charge) {
        this.collaborator = collaborator;
        this.projectRequestMisc = projectRequestMisc;
        this.npdiProject = npdiProject;
        this.day = day;
        this.charge = charge;
    }

    public String getProjectRequestMisc() {
        return projectRequestMisc;
    }

    public void setProjectRequestMisc(String projectRequestMisc) {
        this.projectRequestMisc = projectRequestMisc;
    }

    public String getNpdiProject() {
        return npdiProject;
    }

    public void setNpdiProject(String npdiProject) {
        this.npdiProject = npdiProject;
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
            ", npdiProject='" + npdiProject + '\'' +
            ", day=" + day +
            ", charge=" + charge +
            '}';
    }
}
