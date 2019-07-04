package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.IssueTypeWorklogStatisticsService;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.jira.*;
import com.sqli.imputation.service.factory.JiraImputationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultIssueTypeStatisticsService implements IssueTypeWorklogStatisticsService {

    private final Logger log = LoggerFactory.getLogger(DefaultPpmcProjectStatisticsService.class);

    @Autowired
    private JiraImputationFactory jiraImputationFactory;
    @Autowired
    private JiraImputationService jiraImputationService;

    @Override
    public List<IssueTypeStatisticsDTO> getIssueTypeWorkloged(TbpRequestBodyDTO requestBodyDTO, List<Collaborator> collaborators) {
        log.debug("DefaultIssueTypeStatisticsService.getIssueTypeWorkloged: request to get IssueTypeStatisticsDTOs");
        List<IssueDTO> filtredIssues = jiraImputationService.getTeamJiraIssues(collaborators, requestBodyDTO).stream().filter(this::isValidIssue).collect(Collectors.toList());
        List<IssueTypeStatisticsDTO> issueTypeStatisticsDTOS = new ArrayList<>();
        filtredIssues.forEach(issueDTO -> getIssueTypeWorklogedOfIssue(issueDTO, issueTypeStatisticsDTOS, requestBodyDTO));
        return issueTypeStatisticsDTOS;
    }

    private void getIssueTypeWorklogedOfIssue(IssueDTO issueDTO, List<IssueTypeStatisticsDTO> issueTypeStatisticsDTOS, TbpRequestBodyDTO requestBodyDTO) {
        if (isIssueTypeWorklogAlreadyExist(issueDTO.getFields().getIssuetype().getName(), issueTypeStatisticsDTOS)) {
            updateIssueTypeWorklog(issueDTO, issueTypeStatisticsDTOS, requestBodyDTO);
        } else {
            IssueTypeStatisticsDTO issueTypeStatisticsDTO = createIssueTypeStatisticsDOT(issueDTO, requestBodyDTO);
            if (issueTypeStatisticsDTO.getTotalMinutes() > 0) issueTypeStatisticsDTOS.add(issueTypeStatisticsDTO);
        }
    }

    private void updateIssueTypeWorklog(IssueDTO issueDTO, List<IssueTypeStatisticsDTO> issueTypeStatisticsDTOS, TbpRequestBodyDTO requestBodyDTO) {
        IssueTypeStatisticsDTO issueTypeStatisticsDTO = findIssueTypeWorklog(issueDTO.getFields().getIssuetype().getName(), issueTypeStatisticsDTOS);
        issueTypeStatisticsDTO.incrementFrequency();
        updateIssueypeWorklogTimeSpent(issueDTO, issueTypeStatisticsDTO, requestBodyDTO);

    }

    private IssueTypeStatisticsDTO findIssueTypeWorklog(String ppmcProject, List<IssueTypeStatisticsDTO> issueTypeStatisticsDTOS) {
        return issueTypeStatisticsDTOS.stream().filter(issueTypeStatisticsDTO -> issueTypeStatisticsDTO.getIssueType().equals(ppmcProject)).findFirst().get();
    }

    private IssueTypeStatisticsDTO createIssueTypeStatisticsDOT(IssueDTO issueDTO, TbpRequestBodyDTO requestBodyDTO) {
        IssueTypeStatisticsDTO typeStatisticsDTO = jiraImputationFactory.createIssueTypeStatisticsDTO(issueDTO.getFields().getIssuetype().getName());
        updateIssueypeWorklogTimeSpent(issueDTO, typeStatisticsDTO, requestBodyDTO);
        return typeStatisticsDTO;
    }

    private void updateIssueypeWorklogTimeSpent(IssueDTO issueDTO, IssueTypeStatisticsDTO issueTypeStatisticsDTO, TbpRequestBodyDTO requestBodyDTO) {
        TimeSpentDTO timeSpentDTO = jiraImputationService.getWorklogsTimeSpentDTO(issueDTO, issueTypeStatisticsDTO.getTimeSpent(), requestBodyDTO);
        issueTypeStatisticsDTO.setTimeSpent(timeSpentDTO.toCustomizedString());
        issueTypeStatisticsDTO.setTotalMinutes(timeSpentDTO.getTotalMinutes());
    }


    private boolean isIssueTypeWorklogAlreadyExist(String ppmcProject, List<IssueTypeStatisticsDTO> issueTypeStatisticsDTOS) {
        return issueTypeStatisticsDTOS.stream().anyMatch(issueTypeStatisticsDTO -> issueTypeStatisticsDTO.getIssueType().equals(ppmcProject));
    }


    private boolean isValidIssue(IssueDTO issueDTO) {
        return isValidIssueType(issueDTO) && issueDTO.getFields().getWorklog() != null;
    }

    private boolean isValidIssueType(IssueDTO issueDTO) {
        return issueDTO.getFields().getIssuetype() != null && !issueDTO.getFields().getIssuetype().getName().isEmpty();
    }

}
