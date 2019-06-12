package com.sqli.imputation.service;

import com.sqli.imputation.service.dto.AppTbpRequestBodyDTO;
import com.sqli.imputation.service.dto.jira.JiraImputationDTO;

public interface JiraResourceService {

    JiraImputationDTO getAllStories(AppTbpRequestBodyDTO requestBodyDTO);
}
