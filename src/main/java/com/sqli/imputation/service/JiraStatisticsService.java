package com.sqli.imputation.service;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.jira.IssueTypeStatisticsDTO;
import com.sqli.imputation.service.dto.jira.PpmcProjectWorklogDTO;

import java.util.List;

public interface JiraStatisticsService {

    List<PpmcProjectWorklogDTO> getPpmcProjectWorkloged(List<Collaborator> collaboratorList, TbpRequestBodyDTO requestBodyDTO);

    List<IssueTypeStatisticsDTO> getIssueTypeWorkloged(List<Collaborator> all, TbpRequestBodyDTO requestBodyDTO);
}
