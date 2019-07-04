package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.PpmcProjectWorklogStaticticsService;
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
public class DefaultPpmcProjectStatisticsService implements PpmcProjectWorklogStaticticsService {

    private final Logger log = LoggerFactory.getLogger(DefaultPpmcProjectStatisticsService.class);

    @Autowired
    private JiraImputationFactory jiraImputationFactory;
    @Autowired
    private JiraImputationService jiraImputationService;

    @Override
    public List<PpmcProjectWorklogDTO> getPpmcProjectWorkloged(TbpRequestBodyDTO requestBodyDTO, List<Collaborator> collaborators) {
        log.debug("DefaultPpmcProjectStatisticsService.getPpmcProjectWorkloged: request to get PpmcProjectWorklogDTOs");
        List<IssueDTO> filtredIssues = jiraImputationService.getTeamJiraIssues(collaborators, requestBodyDTO).stream().filter(this::isValidIssue).collect(Collectors.toList());
        List<PpmcProjectWorklogDTO> ppmcProjectWorklogDTOS = new ArrayList<>();
        filtredIssues.forEach(issueDTO -> getPpmcProjectWorklogedOfIssue(issueDTO, ppmcProjectWorklogDTOS, requestBodyDTO));
        return ppmcProjectWorklogDTOS;
    }

    private void getPpmcProjectWorklogedOfIssue(IssueDTO issueDTO, List<PpmcProjectWorklogDTO> ppmcProjectWorklogDTOS, TbpRequestBodyDTO requestBodyDTO) {
        if (isPpmcProjectWorklogAlreadyExist(issueDTO.getFields().getCustomfield_10841().getValue(), ppmcProjectWorklogDTOS)) {
            updatePpmcProjectWorklog(issueDTO, ppmcProjectWorklogDTOS, requestBodyDTO);
        } else {
            PpmcProjectWorklogDTO ppmcProjectWorklogDTO = createPpmcProjectWorklog(issueDTO, requestBodyDTO);
            if (ppmcProjectWorklogDTO.getTotalMinutes() > 0) ppmcProjectWorklogDTOS.add(ppmcProjectWorklogDTO);
        }
    }

    private void updatePpmcProjectWorklog(IssueDTO issueDTO, List<PpmcProjectWorklogDTO> ppmcProjectWorklogDTOS, TbpRequestBodyDTO requestBodyDTO) {
        PpmcProjectWorklogDTO ppmcProjectWorklog = findPpmcProjectWorklog(issueDTO.getFields().getCustomfield_10841().getValue(), ppmcProjectWorklogDTOS);
        ppmcProjectWorklog.incrementFrequency();
        updatePpmcProjectWorklogTimeSpent(issueDTO, ppmcProjectWorklog, requestBodyDTO);

    }

    private PpmcProjectWorklogDTO findPpmcProjectWorklog(String ppmcProject, List<PpmcProjectWorklogDTO> ppmcProjectWorklogDTOS) {
        return ppmcProjectWorklogDTOS.stream().filter(ppmcProjectWorklog -> ppmcProjectWorklog.getPpmcProject().equals(ppmcProject)).findFirst().get();
    }

    private PpmcProjectWorklogDTO createPpmcProjectWorklog(IssueDTO issueDTO, TbpRequestBodyDTO requestBodyDTO) {
        PpmcProjectWorklogDTO ppmcProjectWorklog = jiraImputationFactory.createPpmcProjectWorkolgDTO(issueDTO.getFields().getCustomfield_10841().getValue());
        updatePpmcProjectWorklogTimeSpent(issueDTO, ppmcProjectWorklog, requestBodyDTO);
        return ppmcProjectWorklog;
    }

    private void updatePpmcProjectWorklogTimeSpent(IssueDTO issueDTO, PpmcProjectWorklogDTO ppmcProjectWorklog, TbpRequestBodyDTO requestBodyDTO) {
        TimeSpentDTO timeSpentDTO = jiraImputationService.getWorklogsTimeSpentDTO(issueDTO, ppmcProjectWorklog.getTimeSpent(), requestBodyDTO);
        ppmcProjectWorklog.setTimeSpent(timeSpentDTO.toCustomizedString());
        ppmcProjectWorklog.setTotalMinutes(timeSpentDTO.getTotalMinutes());
    }

    private boolean isPpmcProjectWorklogAlreadyExist(String ppmcProject, List<PpmcProjectWorklogDTO> ppmcProjectWorklogDTOS) {
        return ppmcProjectWorklogDTOS.stream().anyMatch(ppmcProjectWorklog -> ppmcProjectWorklog.getPpmcProject().equals(ppmcProject));
    }

    private boolean isValidIssue(IssueDTO issueDTO) {
        return isValidPPmcProject(issueDTO) && issueDTO.getFields().getWorklog() != null;
    }

    private boolean isValidPPmcProject(IssueDTO issueDTO) {
        return issueDTO.getFields().getCustomfield_10841() != null && !issueDTO.getFields().getCustomfield_10841().getValue().isEmpty();
    }


}
