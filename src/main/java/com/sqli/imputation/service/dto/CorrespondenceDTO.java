package com.sqli.imputation.service.dto;

public class CorrespondenceDTO {

    private String id_ppmc;
    private String id_app;

    public String getId_ppmc() {
        return id_ppmc;
    }

    public void setId_ppmc(String id_ppmc) {
        this.id_ppmc = id_ppmc;
    }

    public String getId_app() {
        return id_app;
    }

    public void setId_app(String id_app) {
        this.id_app = id_app;
    }

    @Override
    public String toString() {
        return "CorrespondenceDTO{" +
            "id_ppmc='" + id_ppmc + '\'' +
            ", id_app='" + id_app + '\'' +
            '}';
    }
}
