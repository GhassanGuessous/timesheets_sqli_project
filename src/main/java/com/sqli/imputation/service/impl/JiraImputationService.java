package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.JiraRestService;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.jira.*;
import com.sqli.imputation.service.util.DateUtil;
import com.sqli.imputation.service.util.TimeSpentCalculatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class JiraImputationService {

    private final Logger log = LoggerFactory.getLogger(JiraImputationService.class);
    private static final String JIRA_NESPRESSO_URL = "https://jira.nespresso.com/rest/api/2/search?jql=";
    private static final String SINGLE_QUOTE = "'";
    private static final String OPEN_PARENTHESIS = "(";
    private static final String CLOSED_PARENTHESIS = ")";
    private static final String COMMA = ",";
    private static final int EMPTY_STRING_LENGTH = 0;
    private static final String EPMTY_STRING = "";
    private static final String TIME_DELIMITER = "T";
    private static final int DATE_POSITION = 0;
    private static final String SPACE = " ";

    @Autowired
    private JiraRestService jiraRestService;

    private String worklogAuthors;
    private StringBuilder stringBuilder = new StringBuilder();

    public List<IssueDTO> getTeamJiraIssues(List<Collaborator> collaborators, TbpRequestBodyDTO requestBodyDTO) {
        this.worklogAuthors = initializeWorklogAuthors(collaborators);
        jiraRestService.logIn(requestBodyDTO.getUsername(), requestBodyDTO.getPassword());
        ResponseEntity<JiraIssuesResponseDTO> responseEntity = getCollaboratorsIssues(requestBodyDTO);
        List<IssueDTO> issueDTOS = responseEntity.getBody().getIssues();
        if (hasMoreIssues(responseEntity)) {
            responseEntity = getIssuesWithPagination(requestBodyDTO, responseEntity.getBody().getMaxResults(), responseEntity.getBody().getTotal());
            addAllIssues(issueDTOS, responseEntity);
        }
        return issueDTOS;
    }

    public String initializeWorklogAuthors(List<Collaborator> collaborators) {
        getEmptyStringBuilder();
        Iterator<Collaborator> collaboratorIterator = collaborators.iterator();
        stringBuilder.append(OPEN_PARENTHESIS);
        while (collaboratorIterator.hasNext()) {
            Collaborator collaborator = collaboratorIterator.next();
            stringBuilder.append(SINGLE_QUOTE)
                .append(collaborator.getFirstname().toLowerCase())
                .append(SPACE)
                .append(collaborator.getLastname().toLowerCase())
                .append(SINGLE_QUOTE);
            if (collaboratorIterator.hasNext()) stringBuilder.append(COMMA);
        }
        stringBuilder.append(CLOSED_PARENTHESIS);
        return stringBuilder.toString();
    }

    public StringBuilder getEmptyStringBuilder() {
        stringBuilder.setLength(EMPTY_STRING_LENGTH);
        return stringBuilder;
    }

    public boolean hasMoreIssues(ResponseEntity<JiraIssuesResponseDTO> responseEntity) {
        return responseEntity.getBody().getTotal() > responseEntity.getBody().getMaxResults();
    }

    public void addAllIssues(List<IssueDTO> issueDTOS, ResponseEntity<JiraIssuesResponseDTO> responseEntity) {
        issueDTOS.addAll(responseEntity.getBody().getIssues());
    }

    private ResponseEntity<JiraIssuesResponseDTO> getIssuesWithPagination(TbpRequestBodyDTO requestBodyDTO, int startAt, int maxResult) {
        log.debug("JiraImputationService.getCollaboratorIssuesWithPagination: request to get issues from JIRA");
        return jiraRestService.getStories(JIRA_NESPRESSO_URL + "worklogAuthor in" + worklogAuthors + " +AND+ issueFunction in workLogged('after " + requestBodyDTO.getStartDate() + " before " + requestBodyDTO.getEndDate() + "')&startAt=" + startAt + "&maxResults=" + maxResult + "&fields=worklog,customfield_10841,issuetype");
    }

    private ResponseEntity<JiraIssuesResponseDTO> getCollaboratorsIssues(TbpRequestBodyDTO requestBodyDTO) {
        log.debug("JiraImputationService.getCollaboratorIssues: request to get issues from JIRA");
        return jiraRestService.getStories(JIRA_NESPRESSO_URL + "worklogAuthor in" + worklogAuthors + "+AND+ issueFunction in workLogged('after " + requestBodyDTO.getStartDate() + " before " + requestBodyDTO.getEndDate() + "')&fields=worklog,customfield_10841,issuetype");
    }

    public TimeSpentDTO getWorklogsTimeSpentDTO(IssueDTO issueDTO, String timeSpent, TbpRequestBodyDTO requestBodyDTO) {
        if (hasMoreWorklogs(issueDTO.getFields().getWorklog())) {
            issueDTO.getFields().setWorklog(getAllWorklogsOfIssue(issueDTO));
        }
        return TimeSpentCalculatorUtil.calculate(getWorklogsTimeSpent(issueDTO, requestBodyDTO, timeSpent));
    }

    public String getWorklogsTimeSpent(IssueDTO issueDTO, TbpRequestBodyDTO requestBodyDTO, String timeSpent) {
        getEmptyStringBuilder();
        stringBuilder.append(timeSpent == null ? EPMTY_STRING : timeSpent);
        issueDTO.getFields().getWorklog().getWorklogs().stream()
            .filter(worklogItemDTO -> isValidWorklogItem(worklogItemDTO, requestBodyDTO, worklogAuthors))
            .forEach(worklogItem -> stringBuilder.append(worklogItem.getTimeSpent()).append(SPACE));
        return stringBuilder.toString();
    }

    public boolean isValidWorklogItem(WorklogItemDTO worklogItemDTO, TbpRequestBodyDTO requestBodyDTO, String worklogAuthors) {
        return worklogItemDateIsValid(worklogItemDTO, requestBodyDTO) && worklogItemAuthorIsValid(worklogItemDTO, worklogAuthors);
    }

    private boolean worklogItemAuthorIsValid(WorklogItemDTO worklogItemDTO, String worklogAuthors) {
        return worklogAuthors.contains(worklogItemDTO.getAuthor().getDisplayName().toLowerCase());
    }

    private boolean worklogItemDateIsValid(WorklogItemDTO worklogItemDTO, TbpRequestBodyDTO requestBodyDTO) {
        return DateUtil.isDateBetween(getWorklogDate(worklogItemDTO.getStarted()), requestBodyDTO.getStartDate(), requestBodyDTO.getEndDate());
    }

    public String getWorklogDate(String date) {
        return date.split(TIME_DELIMITER)[DATE_POSITION];
    }

    public boolean hasMoreWorklogs(WorklogDTO worklog) {
        return worklog.getTotal() > worklog.getMaxResults();
    }

    public WorklogDTO getAllWorklogsOfIssue(IssueDTO issueDTO) {
        log.debug("JiraImputationService.getAllWorklogsOfIssue: request to get issue's workLogs from JIRA");
        return jiraRestService.getIssueWorklogs(issueDTO.getSelf()).getBody();
    }
}
