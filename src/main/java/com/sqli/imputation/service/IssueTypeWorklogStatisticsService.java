package com.sqli.imputation.service;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.jira.IssueTypeStatisticsDTO;

import java.util.List;

public interface IssueTypeWorklogStatisticsService {
    List<IssueTypeStatisticsDTO> getIssueTypeWorkloged(TbpRequestBodyDTO requestBodyDTO, List<Collaborator> collaborators);
}
