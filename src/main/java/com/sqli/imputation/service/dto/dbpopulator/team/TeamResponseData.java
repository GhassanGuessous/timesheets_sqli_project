package com.sqli.imputation.service.dto.dbpopulator.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sqli.imputation.service.dto.TeamDTO;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamResponseData {

    private List<TeamDTO> projets;

    public List<TeamDTO> getProjets() {
        return projets;
    }

    public void setProjets(List<TeamDTO> projets) {
        this.projets = projets;
    }

    @Override
    public String toString() {
        return "TeamResponseData{" +
            "projets=" + projets +
            '}';
    }
}
