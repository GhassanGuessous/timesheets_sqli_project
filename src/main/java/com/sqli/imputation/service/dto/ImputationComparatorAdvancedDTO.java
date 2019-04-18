package com.sqli.imputation.service.dto;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.domain.CollaboratorMonthlyImputation;

public class ImputationComparatorAdvancedDTO {

    private Collaborator collaborator;
    private CollaboratorMonthlyImputation appMonthlyImputation;
    private CollaboratorMonthlyImputation comparedMonthlyImputation;

    public ImputationComparatorAdvancedDTO(Collaborator collaborator, CollaboratorMonthlyImputation appMonthlyImputation, CollaboratorMonthlyImputation comparedMonthlyImputation) {
        this.collaborator = collaborator;
        this.appMonthlyImputation = appMonthlyImputation;
        this.comparedMonthlyImputation = comparedMonthlyImputation;
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    public CollaboratorMonthlyImputation getAppMonthlyImputation() {
        return appMonthlyImputation;
    }

    public void setAppMonthlyImputation(CollaboratorMonthlyImputation appMonthlyImputation) {
        this.appMonthlyImputation = appMonthlyImputation;
    }

    public CollaboratorMonthlyImputation getComparedMonthlyImputation() {
        return comparedMonthlyImputation;
    }

    public void setComparedMonthlyImputation(CollaboratorMonthlyImputation comparedMonthlyImputation) {
        this.comparedMonthlyImputation = comparedMonthlyImputation;
    }
}
