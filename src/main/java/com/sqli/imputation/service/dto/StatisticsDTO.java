package com.sqli.imputation.service.dto;

public class StatisticsDTO {

    private String collaborator;
    private int appVsPpmc;
    private int appVsTbp;

    public StatisticsDTO() {
        //default constructor
    }

    public String getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(String collaborator) {
        this.collaborator = collaborator;
    }

    public int getAppVsPpmc() {
        return appVsPpmc;
    }

    public void setAppVsPpmc(int appVsPpmc) {
        this.appVsPpmc = appVsPpmc;
    }

    public int getAppVsTbp() {
        return appVsTbp;
    }

    public void setAppVsTbp(int appVsTbp) {
        this.appVsTbp = appVsTbp;
    }
}
