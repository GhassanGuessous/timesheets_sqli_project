package com.sqli.imputation.service.dto;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.domain.CollaboratorMonthlyImputation;

public class ImputationComparatorAdvancedDTO {

    private Collaborator collaborator;
    private CollaboratorMonthlyImputation appMonthlyImputation;
    private CollaboratorMonthlyImputation ppmcMonthlyImputation;

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

    public CollaboratorMonthlyImputation getPpmcMonthlyImputation() {
        return ppmcMonthlyImputation;
    }

    public void setPpmcMonthlyImputation(CollaboratorMonthlyImputation ppmcMonthlyImputation) {
        this.ppmcMonthlyImputation = ppmcMonthlyImputation;
    }
}
