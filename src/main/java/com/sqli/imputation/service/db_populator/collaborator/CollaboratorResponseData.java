package com.sqli.imputation.service.db_populator.collaborator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sqli.imputation.service.dto.ActivityDTO;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollaboratorResponseData {

    private List<ActivityDTO> collaborateurs;

    public List<ActivityDTO> getCollaborateurs() {
        return collaborateurs;
    }

    public void setCollaborateurs(List<ActivityDTO> collaborateurs) {
        this.collaborateurs = collaborateurs;
    }

    @Override
    public String toString() {
        return "CollaboratorResponseData{" +
            "collaborateurs=" + collaborateurs +
            '}';
    }
}
