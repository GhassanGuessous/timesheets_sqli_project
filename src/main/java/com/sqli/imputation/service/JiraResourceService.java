package com.sqli.imputation.service;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.jira.JiraImputationDTO;

import java.util.List;

public interface JiraResourceService {

    List<JiraImputationDTO> getJiraImputations(List<Collaborator> collaborators, TbpRequestBodyDTO requestBodyDTO);

}
