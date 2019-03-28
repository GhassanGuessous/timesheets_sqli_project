package com.sqli.imputation.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargeTeamDTO {

    private String date;
    private String dateFormat;
    private String typeCase;
    private String charge;
    private List<ChargeCollaboratorDTO> collaborateurs;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTypeCase() {
        return typeCase;
    }

    public void setTypeCase(String typeCase) {
        this.typeCase = typeCase;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public List<ChargeCollaboratorDTO> getCollaborateurs() {
        return collaborateurs;
    }

    public void setCollaborateurs(List<ChargeCollaboratorDTO> collaborateurs) {
        this.collaborateurs = collaborateurs;
    }

    @Override
    public String toString() {
        return "ChargeTeamDTO{" +
            "date='" + date + '\'' +
            ", dateFormat='" + dateFormat + '\'' +
            ", typeCase='" + typeCase + '\'' +
            ", charge='" + charge + '\'' +
            ", collaborateurs=" + collaborateurs +
            '}';
    }
}
