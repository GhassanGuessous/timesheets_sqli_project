package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.JiraLoginService;
import com.sqli.imputation.service.JiraStatisticsService;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.jira.*;
import com.sqli.imputation.service.util.DateUtil;
import com.sqli.imputation.service.util.TimeSpentCalculatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultJiraStatisticsService implements JiraStatisticsService {

    private final Logger log = LoggerFactory.getLogger(DefaultJiraResourceService.class);

    public static final String TIME_SPENT_SEPARATOR = " ";
    public static final String TIME_DELIMITER = "T";
    public static final int DATE_POSITION = 0;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    JiraLoginService jiraLoginService;

    @Override
    public List<PpmcProjectWorklogDTO> getPpmcProjectWorkloged(TbpRequestBodyDTO requestBodyDTO) {
        this.restTemplate = jiraLoginService.logIn(requestBodyDTO.getUsername(), requestBodyDTO.getPassword());
        return getTeamJiraIssues(requestBodyDTO);
    }

    private List<PpmcProjectWorklogDTO> getTeamJiraIssues(TbpRequestBodyDTO requestBodyDTO) {
        List<IssueDTO> issueDTOS = new ArrayList<>();
        ResponseEntity<JiraIssuesResponseDTO> responseEntity = getCollaboratorIssues(requestBodyDTO);
        addAllIssues(issueDTOS, responseEntity);
        if (hasMoreIssues(responseEntity)) {
            responseEntity = getIssuesWithPagination(requestBodyDTO, responseEntity.getBody().getMaxResults(), responseEntity.getBody().getTotal());
            addAllIssues(issueDTOS, responseEntity);
        }
        List<PpmcProjectWorklogDTO> ppmcProjectWorklogDTOS = new ArrayList<>();
        issueDTOS.stream().filter(this::isValidIssue).forEach(issueDTO -> getPpmcProjectWorklogedOfIssue(issueDTO, ppmcProjectWorklogDTOS, requestBodyDTO));
        return ppmcProjectWorklogDTOS;
    }

    private void getPpmcProjectWorklogedOfIssue(IssueDTO issueDTO, List<PpmcProjectWorklogDTO> ppmcProjectWorklogDTOS, TbpRequestBodyDTO requestBodyDTO) {
        if (isPpmcProjectWorklogAlreadyExist(issueDTO.getFields().getCustomfield_10841().getValue(), ppmcProjectWorklogDTOS)) {
            updatePpmcProjectWorklog(issueDTO, ppmcProjectWorklogDTOS, requestBodyDTO);
        } else {
            PpmcProjectWorklogDTO ppmcProjectWorklogDTO=createPpmcProjectWorklog(issueDTO, requestBodyDTO);
           if (ppmcProjectWorklogDTO.getTotalMinutes()>0)ppmcProjectWorklogDTOS.add(ppmcProjectWorklogDTO);
        }
    }

    private void updatePpmcProjectWorklog(IssueDTO issueDTO, List<PpmcProjectWorklogDTO> ppmcProjectWorklogDTOS, TbpRequestBodyDTO requestBodyDTO) {
        PpmcProjectWorklogDTO ppmcProjectWorklog = findPpmcProjectWorklog(issueDTO.getFields().getCustomfield_10841().getValue(), ppmcProjectWorklogDTOS);
        updatePpmcProjectWorklogTimeSpent(issueDTO, ppmcProjectWorklog, requestBodyDTO);

    }

    private PpmcProjectWorklogDTO findPpmcProjectWorklog(String ppmcProject, List<PpmcProjectWorklogDTO> ppmcProjectWorklogDTOS) {
        return ppmcProjectWorklogDTOS.stream().filter(ppmcProjectWorklog -> ppmcProjectWorklog.getPpmcProject().equals(ppmcProject)).findFirst().get();
    }

    private PpmcProjectWorklogDTO createPpmcProjectWorklog(IssueDTO issueDTO, TbpRequestBodyDTO requestBodyDTO) {
        PpmcProjectWorklogDTO ppmcProjectWorklog = new PpmcProjectWorklogDTO();
        ppmcProjectWorklog.setPpmcProject(issueDTO.getFields().getCustomfield_10841().getValue());
        updatePpmcProjectWorklogTimeSpent(issueDTO, ppmcProjectWorklog, requestBodyDTO);
        return ppmcProjectWorklog;
    }

    private void updatePpmcProjectWorklogTimeSpent(IssueDTO issueDTO, PpmcProjectWorklogDTO ppmcProjectWorklog, TbpRequestBodyDTO requestBodyDTO) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ppmcProjectWorklog.getTimeSpent() == null ? "" : ppmcProjectWorklog.getTimeSpent());
        issueDTO.getFields().getWorklog().getWorklogs().stream()
            .filter(worklogItemDTO -> worklogItemDateIsValid(worklogItemDTO, requestBodyDTO))
            .forEach(worklogItem -> stringBuilder.append(worklogItem.getTimeSpent()).append(TIME_SPENT_SEPARATOR));
        TimeSpentDTO timeSpentDTO = TimeSpentCalculatorUtil.calculate(stringBuilder.toString());
        ppmcProjectWorklog.setTimeSpent(timeSpentDTO.toCustomizedString());
        ppmcProjectWorklog.setTotalMinutes(timeSpentDTO.getTotalMinutes());
    }

    private boolean isPpmcProjectWorklogAlreadyExist(String ppmcProject, List<PpmcProjectWorklogDTO> ppmcProjectWorklogDTOS) {
        return ppmcProjectWorklogDTOS.stream().anyMatch(ppmcProjectWorklog -> ppmcProjectWorklog.getPpmcProject().equals(ppmcProject));
    }

    private boolean worklogItemDateIsValid(WorklogItemDTO worklogItemDTO, TbpRequestBodyDTO requestBodyDTO) {
        return DateUtil.isSameYearAndMonth(worklogItemDTO.getStarted().split(TIME_DELIMITER)[DATE_POSITION], requestBodyDTO.getStartDate())&&worklogItemDayIsRequested(worklogItemDTO, requestBodyDTO);
    }

    private boolean worklogItemDayIsRequested(WorklogItemDTO worklogItemDTO, TbpRequestBodyDTO requestBodyDTO) {
        return getWorklogDay(worklogItemDTO)>= DateUtil.getDay(requestBodyDTO.getStartDate());
    }

    private int getWorklogDay(WorklogItemDTO worklogItemDTO) {
        return DateUtil.getDay(worklogItemDTO.getStarted().split(TIME_DELIMITER)[DATE_POSITION]);
    }

    private boolean isValidIssue(IssueDTO issueDTO) {
        return issueDTO.getFields().getCustomfield_10841() != null && !issueDTO.getFields().getCustomfield_10841().getValue().isEmpty() && issueDTO.getFields().getWorklog() != null;
    }

    private boolean hasMoreIssues(ResponseEntity<JiraIssuesResponseDTO> responseEntity) {
        return responseEntity.getBody().getTotal() > responseEntity.getBody().getMaxResults();
    }

    private void addAllIssues(List<IssueDTO> issueDTOS, ResponseEntity<JiraIssuesResponseDTO> responseEntity) {
        issueDTOS.addAll(responseEntity.getBody().getIssues());
    }

    private ResponseEntity<JiraIssuesResponseDTO> getIssuesWithPagination(TbpRequestBodyDTO requestBodyDTO, int startAt, int maxResult) {
        log.debug("DefaultJiraResourceService.getCollaboratorIssuesWithPagination: request to get issues from JIRA");
        return restTemplate.exchange("https://jira.nespresso.com/rest/api/2/search?jql=team=" + requestBodyDTO.getIdTbp() + " +AND+ issueFunction in workLogged('after " + requestBodyDTO.getStartDate() + " before " + requestBodyDTO.getEndDate() + "')&startAt=" + startAt + "&maxResults=" + maxResult + "&fields=worklog,customfield_10841", HttpMethod.GET, jiraLoginService.getTbpHttpHeaders(), JiraIssuesResponseDTO.class);
    }

    private ResponseEntity<JiraIssuesResponseDTO> getCollaboratorIssues(TbpRequestBodyDTO requestBodyDTO) {
        log.debug("DefaultJiraResourceService.getCollaboratorIssues: request to get issues from JIRA");
        return restTemplate.exchange("https://jira.nespresso.com/rest/api/2/search?jql=team=" + requestBodyDTO.getIdTbp() + " +AND+ issueFunction in workLogged('after " + requestBodyDTO.getStartDate() + " before " + requestBodyDTO.getEndDate() + "')&fields=worklog,customfield_10841", HttpMethod.GET, jiraLoginService.getTbpHttpHeaders(), JiraIssuesResponseDTO.class);
    }


}

