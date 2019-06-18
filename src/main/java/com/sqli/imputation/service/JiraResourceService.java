package com.sqli.imputation.service;

import com.sqli.imputation.domain.Team;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.jira.JiraImputationDTO;

public interface JiraResourceService {

    JiraImputationDTO getJiraImputation(Team team, TbpRequestBodyDTO requestBodyDTO);
}
