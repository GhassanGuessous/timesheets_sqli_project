package com.sqli.imputation.service.dto;

public class StatisticsDTO {

    private String collaborator;
    private int app_vs_ppmc;
    private int app_vs_tbp;

    public StatisticsDTO() {
    }

    public String getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(String collaborator) {
        this.collaborator = collaborator;
    }

    public int getApp_vs_ppmc() {
        return app_vs_ppmc;
    }

    public void setApp_vs_ppmc(int app_vs_ppmc) {
        this.app_vs_ppmc = app_vs_ppmc;
    }

    public int getApp_vs_tbp() {
        return app_vs_tbp;
    }

    public void setApp_vs_tbp(int app_vs_tbp) {
        this.app_vs_tbp = app_vs_tbp;
    }
}
