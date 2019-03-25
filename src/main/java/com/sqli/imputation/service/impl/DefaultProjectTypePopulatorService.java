package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.ProjectType;
import com.sqli.imputation.service.ProjectTypePopulatorService;
import com.sqli.imputation.service.ProjectTypeService;
import com.sqli.imputation.service.dto.ProjectTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultProjectTypePopulatorService implements ProjectTypePopulatorService {
    @Autowired
    private ProjectTypeService projectTypeService;

    @Override
    public ProjectType populateDatabase(ProjectTypeDTO projectTypeDTO) {
        ProjectType projectType = clone(projectTypeDTO);
        return projectTypeService.save(projectType);
    }

    private ProjectType clone(ProjectTypeDTO projectTypeDTO) {
        ProjectType projectType = new ProjectType();
        projectType.setName(projectTypeDTO.getLibelle());
        return projectType;
    }
}
