package com.sqli.imputation.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamDTO {

    private String id;
    private String libelle;
    private String agresso;
    private String type;
    private String activite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getAgresso() {
        return agresso;
    }

    public void setAgresso(String agresso) {
        this.agresso = agresso;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActivite() {
        return activite;
    }

    public void setActivite(String activite) {
        this.activite = activite;
    }

    @Override
    public String toString() {
        return "TeamDTO{" +
            "id='" + id + '\'' +
            ", libelle='" + libelle + '\'' +
            ", agresso='" + agresso + '\'' +
            ", type='" + type + '\'' +
            ", activite='" + activite + '\'' +
            '}';
    }
}
