package com.sqli.imputation.service;

import com.sqli.imputation.domain.ProjectType;
import com.sqli.imputation.service.dto.ProjectTypeDTO;

public interface ProjectTypePopulatorService {

    ProjectType populateDatabase(ProjectTypeDTO projectTypeDTO);
}
