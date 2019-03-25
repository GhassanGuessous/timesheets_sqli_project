package com.sqli.imputation.service;

import com.sqli.imputation.domain.Team;
import com.sqli.imputation.service.dto.TeamDTO;

public interface TeamPopulatorService {

    Team populateDatabase(TeamDTO teamDTO);
}
