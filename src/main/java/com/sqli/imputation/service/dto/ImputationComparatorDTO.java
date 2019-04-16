package com.sqli.imputation.service.dto;

import com.sqli.imputation.domain.Collaborator;

public class ImputationComparatorDTO {

    private Collaborator collaborator;
    private double totalApp;
    private double totalCompared;
    private double difference;

    public ImputationComparatorDTO(Collaborator collaborator, double totalApp, double totalCompared) {
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

    public double getTotalApp() {
        return totalApp;
    }

    public double getTotalCompared() {
        return totalCompared;
    }

    public void setTotalCompared(double totalCompared) {
        this.totalCompared = totalCompared;
    }

    public double getDifference() {
        return difference;
    }

    public void setDifference(double difference) {
        this.difference = difference;
    }
}
