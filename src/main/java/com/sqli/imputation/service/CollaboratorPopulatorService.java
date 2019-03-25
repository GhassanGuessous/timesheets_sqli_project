package com.sqli.imputation.service;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.dto.CollaboratorDTO;

public interface CollaboratorPopulatorService {

    Collaborator populateDatabase(CollaboratorDTO collaboratorDTO);
}
