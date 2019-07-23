package com.sqli.imputation.service.dto.dbpopulator.collaborator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sqli.imputation.service.dto.CollaboratorDTO;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollaboratorResponseData {

    private List<CollaboratorDTO> collaborateurs;

    public List<CollaboratorDTO> getCollaborateurs() {
        return collaborateurs;
    }

    public void setCollaborateurs(List<CollaboratorDTO> collaborateurs) {
        this.collaborateurs = collaborateurs;
    }

    @Override
    public String toString() {
        return "CollaboratorResponseData{" +
            "collaborateurs=" + collaborateurs +
            '}';
    }
}
