package com.sqli.imputation.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargeCollaboratorDTO {

    private String id;
    private String nom;
    private String charge;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    @Override
    public String toString() {
        return "ChargeCollaboratorDTO{" +
            "id='" + id + '\'' +
            ", nom='" + nom + '\'' +
            ", charge='" + charge + '\'' +
            '}';
    }
}
