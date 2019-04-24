package com.sqli.imputation.service.dto;

import com.sqli.imputation.domain.CollaboratorDailyImputation;

import java.util.List;

public class GapDTO {
    private String imputationType;
    private List<CollaboratorDailyImputation> dailyImputations;

    public GapDTO() {
    }

    public String getImputationType() {
        return imputationType;
    }

    public void setImputationType(String imputationType) {
        this.imputationType = imputationType;
    }

    public List<CollaboratorDailyImputation> getDailyImputations() {
        return dailyImputations;
    }

    public void setDailyImputations(List<CollaboratorDailyImputation> dailyImputations) {
        this.dailyImputations = dailyImputations;
    }

    @Override
    public String toString() {
        return "GapDTO{" +
            "imputationType='" + imputationType + '\'' +
            ", dailyImputations=" + dailyImputations +
            '}';
    }
}
