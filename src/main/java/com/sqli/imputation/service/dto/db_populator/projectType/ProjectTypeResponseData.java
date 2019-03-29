package com.sqli.imputation.service.dto.db_populator.projectType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sqli.imputation.service.dto.ProjectTypeDTO;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectTypeResponseData {

    private List<ProjectTypeDTO> types;

    public List<ProjectTypeDTO> getTypes() {
        return types;
    }

    public void setTypes(List<ProjectTypeDTO> types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "ProjectTypeResponseData{" +
            "types=" + types +
            '}';
    }
}
