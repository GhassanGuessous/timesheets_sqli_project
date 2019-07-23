package com.sqli.imputation.service.dto.dbpopulator.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sqli.imputation.service.dto.ActivityDTO;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityResponseData {

    private List<ActivityDTO> activites;

    public List<ActivityDTO> getActivites() {
        return activites;
    }

    public void setActivites(List<ActivityDTO> activites) {
        this.activites = activites;
    }

    @Override
    public String toString() {
        return "ActivityResponseData{" +
            "activites=" + activites +
            '}';
    }
}
