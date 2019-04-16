package com.sqli.imputation.service.dto;

import com.sqli.imputation.domain.Collaborator;

public class ImputationComparatorDTO {

    private Collaborator collaborator;
    private Double totalApp;
    private Double totalCompared;
    private Double difference;

    public ImputationComparatorDTO() {
    }

    public ImputationComparatorDTO(Collaborator collaborator, Double totalApp, Double totalCompared) {
        this.collaborator = collaborator;
        this.totalApp = totalApp;
        this.totalCompared = totalCompared;
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    public Double getTotalApp() {
        return totalApp;
    }

    public void setTotalApp(Double totalApp) {
        this.totalApp = totalApp;
    }

    public Double getTotalCompared() {
        return totalCompared;
    }

    public void setTotalCompared(Double totalCompared) {
        this.totalCompared = totalCompared;
    }

    public Double getDifference() {
        return difference;
    }

    public void setDifference(Double difference) {
        this.difference = difference;
    }

    @Override
    public String toString() {
        return "ImputationComparatorDTO{" +
            "collaborator=" + collaborator +
            ", totalApp=" + totalApp +
            ", totalCompared=" + totalCompared +
            ", difference=" + difference +
            '}';
    }
}
