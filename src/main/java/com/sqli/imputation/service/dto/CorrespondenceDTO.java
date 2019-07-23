package com.sqli.imputation.service.dto;

public class CorrespondenceDTO {

    private String idPpmc;
    private String idApp;

    public String getIdPpmc() {
        return idPpmc;
    }

    public void setIdPpmc(String idPpmc) {
        this.idPpmc = idPpmc;
    }

    public String getIdApp() {
        return idApp;
    }

    public void setIdApp(String idApp) {
        this.idApp = idApp;
    }

    @Override
    public String toString() {
        return "CorrespondenceDTO{" +
            "idPpmc='" + idPpmc + '\'' +
            ", idApp='" + idApp + '\'' +
            '}';
    }
}
