package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.IssueTypeWorklogStatisticsService;
import com.sqli.imputation.service.JiraStatisticsService;
import com.sqli.imputation.service.PpmcProjectWorklogStaticticsService;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.jira.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class DefaultJiraStatisticsService implements JiraStatisticsService {

    private final Logger log = LoggerFactory.getLogger(DefaultJiraResourceService.class);

    public static final String SPACE = " ";

    @Autowired
    private PpmcProjectWorklogStaticticsService ppmcProjectWorklogStaticticsService;
    @Autowired
    private IssueTypeWorklogStatisticsService issueTypeWorklogStatisticsService;

    @Override
    public  List<PpmcProjectWorklogDTO> getPpmcProjectWorkloged(List<Collaborator> collaboratorList, TbpRequestBodyDTO requestBodyDTO) {
        log.debug("DefaultJiraResourceService.getCollaboratorIssuesWithPagination: request to get PPMC Project statistics from JIRA");
        return ppmcProjectWorklogStaticticsService.getPpmcProjectWorkloged(requestBodyDTO, collaboratorList);
    }

    @Override
    public List<IssueTypeStatisticsDTO> getIssueTypeWorkloged(List<Collaborator> collaboratorList, TbpRequestBodyDTO requestBodyDTO) {
        log.debug("DefaultJiraResourceService.getCollaboratorIssuesWithPagination: request to get PPMC Project statistics from JIRA");
        return issueTypeWorklogStatisticsService.getIssueTypeWorkloged(requestBodyDTO, collaboratorList);
    }

    private String initializeColabsList(List<Collaborator> collaboratorList) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Collaborator> collaboratorIterator = collaboratorList.iterator();
        stringBuilder.append("(");
        while (collaboratorIterator.hasNext()) {
            Collaborator collaborator = collaboratorIterator.next();
            stringBuilder.append("'").append(collaborator.getFirstname()).append(SPACE).append(collaborator.getLastname()).append("'");
            if (collaboratorIterator.hasNext()) stringBuilder.append(",");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}

