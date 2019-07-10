package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.JiraRestService;
import com.sqli.imputation.service.JiraResourceService;
import com.sqli.imputation.service.TbpRequestComposerService;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.jira.*;
import com.sqli.imputation.service.factory.JiraImputationFactory;
import com.sqli.imputation.service.util.DateUtil;
import com.sqli.imputation.service.util.TimeSpentCalculatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultJiraResourceService implements JiraResourceService {
    private final Logger log = LoggerFactory.getLogger(DefaultJiraResourceService.class);

    public static final String JIRA_NESPRESSO_URL = "https://jira.nespresso.com/rest/api/2/search?jql=";
    private static final String SPACE = " ";


    @Autowired
    private JiraRestService jiraRestService;
    @Autowired
    private JiraImputationFactory jiraImputationFactory;
    @Autowired
    private JiraImputationService jiraImputationService;
    @Autowired
    private TbpRequestComposerService tbpComposerService;

    private String worklogAuthors;
    private List<Collaborator> collaborators;

    @Override
    public List<JiraImputationDTO> getJiraImputations(List<Collaborator> collaborators, TbpRequestBodyDTO requestBodyDTO) {
        this.collaborators = collaborators;
        worklogAuthors = jiraImputationService.initializeWorklogAuthors(collaborators);
        jiraRestService.logIn(requestBodyDTO.getUsername(), requestBodyDTO.getPassword());
        return getIssuesForTeamCollabs(requestBodyDTO);
    }

    private List<JiraImputationDTO> getIssuesForTeamCollabs(TbpRequestBodyDTO requestBodyDTO) {
        ResponseEntity<JiraIssuesResponseDTO> responseEntity = getCollaboratorsIssues(requestBodyDTO);
        List<IssueDTO> issueDTOS = responseEntity.getBody().getIssues();
        if (jiraImputationService.hasMoreIssues(responseEntity)) {
            responseEntity = getCollaboratorsIssuesWithPagination(requestBodyDTO, responseEntity.getBody().getMaxResults(), responseEntity.getBody().getTotal());
            jiraImputationService.addAllIssues(issueDTOS, responseEntity);
        }
        return getImputationsFromIssues(requestBodyDTO, issueDTOS);
    }

    private ResponseEntity<JiraIssuesResponseDTO> getCollaboratorsIssues(TbpRequestBodyDTO requestBodyDTO) {
        log.debug("DefaultJiraResourceService.getCollaboratorIssues: request to get issues from JIRA");
        return jiraRestService.getStories(JIRA_NESPRESSO_URL + "worklogAuthor in " + worklogAuthors + "+AND+issueFunction in workLogged('after " + requestBodyDTO.getStartDate() + " before " + requestBodyDTO.getEndDate() + "')&fields=worklog");
    }

    private ResponseEntity<JiraIssuesResponseDTO> getCollaboratorsIssuesWithPagination(TbpRequestBodyDTO requestBodyDTO, int startAt, int maxResult) {
        log.debug("DefaultJiraResourceService.getCollaboratorIssuesWithPagination: request to get issues from JIRA");
        return jiraRestService.getStories(JIRA_NESPRESSO_URL + "worklogAuthor in " + worklogAuthors + "+AND+issueFunction in workLogged('after " + requestBodyDTO.getStartDate() + " before " + requestBodyDTO.getEndDate() + "')&startAt=" + startAt + "&maxResults=" + maxResult + "&fields=worklog");
    }

    private List<JiraImputationDTO> getImputationsFromIssues(TbpRequestBodyDTO requestBodyDTO, List<IssueDTO> issueDTOS) {
        log.debug("DefaultJiraResourceService.getIssuesWorklogs: fetching issues for Collaborator");
        List<JiraImputationDTO> jiraImputationDTOS = new ArrayList<>();
        List<WorklogItemDTO> worklogItemDTOS = getIssuesWorklogItems(requestBodyDTO, issueDTOS);
        List<TbpRequestBodyDTO> requestBodies = tbpComposerService.divideTbpPeriod(requestBodyDTO);
        for (TbpRequestBodyDTO requestBody : requestBodies) {
            jiraImputationDTOS.add(getJiraImputation(requestBody, worklogItemDTOS));
        }
        return jiraImputationDTOS;
    }

    private List<WorklogItemDTO> getIssuesWorklogItems(TbpRequestBodyDTO requestBodyDTO, List<IssueDTO> issueDTOS) {
        List<WorklogItemDTO> worklogItemDTOS = new ArrayList<>();
        issueDTOS.stream().filter(this::isValidIssue).forEach(
            issueDTO -> getWorklogItemsOfIssue(requestBodyDTO, issueDTO, worklogItemDTOS)
        );
        return worklogItemDTOS;
    }

    private JiraImputationDTO getJiraImputation(TbpRequestBodyDTO requestBody, List<WorklogItemDTO> worklogItemDTOS) {
        int year = DateUtil.getYear(requestBody.getStartDate());
        int month = DateUtil.getMonth(requestBody.getStartDate());
        JiraImputationDTO jiraImputationDTO = jiraImputationFactory.createJiraImputationDTO(year, month);
        jiraImputationDTO.setCollaboratorWorklogs(getCollaboratorsWorklogs(worklogItemDTOS, requestBody));
        return jiraImputationDTO;
    }

    private List<JiraCollaboratorWorklog> getCollaboratorsWorklogs(List<WorklogItemDTO> worklogItemDTOS, TbpRequestBodyDTO requestBody) {
        List<JiraCollaboratorWorklog> jiraCollaboratorWorklogs = new ArrayList<>();
        worklogItemDTOS.stream()
            .filter(worklogItemDTO -> isRequestedWorklogItem(worklogItemDTO, requestBody))
            .forEach(worklogItemDTO -> getCollaboratorWorklog(worklogItemDTO, jiraCollaboratorWorklogs));
        jiraCollaboratorWorklogs.forEach(this::updateTotalTimeSpent);
        return jiraCollaboratorWorklogs;
    }

    private boolean isValidIssue(IssueDTO issueDTO) {
        return issueDTO.getFields().getWorklog() != null;
    }

    private void updateTotalTimeSpent(JiraCollaboratorWorklog worklog) {
        StringBuilder builder = jiraImputationService.getEmptyStringBuilder();
        worklog.getJiraDailyWorklogs().forEach(dailyWorklog -> builder.append(dailyWorklog.getWorklogTimeSpent()).append(SPACE));
        worklog.setTotalTimeSpent(TimeSpentCalculatorUtil.calculate(builder.toString()));
    }

    private void getWorklogItemsOfIssue(TbpRequestBodyDTO requestBodyDTO, IssueDTO issueDTO, List<WorklogItemDTO> worklogItemDTOS) {
        if (jiraImputationService.hasMoreWorklogs(issueDTO.getFields().getWorklog())) {
            issueDTO.getFields().setWorklog(getAllWorklogsOfIssue(issueDTO));
        }
        issueDTO.getFields().getWorklog().getWorklogs().stream()
            .filter(worklogItemDTO -> jiraImputationService.isValidWorklogItem(worklogItemDTO, requestBodyDTO, worklogAuthors))
            .forEach(worklogItemDTOS::add);
    }

    private WorklogDTO getAllWorklogsOfIssue(IssueDTO issueDTO) {
        log.debug("DefaultJiraResourceService.getAllWorklogsOfIssue: request to get issue's workLogs from JIRA");
        return jiraRestService.getIssueWorklogs(issueDTO.getSelf()).getBody();
    }

    private boolean isRequestedWorklogItem(WorklogItemDTO worklogItemDTO, TbpRequestBodyDTO requestBodyDTO) {
        return worklogItemDateIsValid(worklogItemDTO, requestBodyDTO) && worklogItemAuthorIsValid(worklogItemDTO);
    }

    private boolean worklogItemAuthorIsValid(WorklogItemDTO worklogItemDTO) {
        return worklogAuthors.contains(worklogItemDTO.getAuthor().getDisplayName().toLowerCase());
    }

    private boolean worklogItemDateIsValid(WorklogItemDTO worklogItemDTO, TbpRequestBodyDTO requestBodyDTO) {
        String worklogDate = jiraImputationService.getWorklogDate(worklogItemDTO.getStarted());
        return DateUtil.isSameYearAndMonth(worklogDate, requestBodyDTO.getStartDate()) && worklogItemDayIsRequested(worklogItemDTO, requestBodyDTO);
    }

    private boolean worklogItemDayIsRequested(WorklogItemDTO worklogItemDTO, TbpRequestBodyDTO requestBodyDTO) {
        return getWorklogDay(worklogItemDTO) >= DateUtil.getDay(requestBodyDTO.getStartDate()) && getWorklogDay(worklogItemDTO) <= DateUtil.getDay(requestBodyDTO.getEndDate());
    }

    private void getCollaboratorWorklog(WorklogItemDTO worklogItemDTO, List<JiraCollaboratorWorklog> collaboratorWorklogs) {
        Collaborator collaborator = findCollaboratorByFirstAndLastName(worklogItemDTO.getAuthor().getDisplayName());
        if (isWorklogAlreadyExistForCollab(collaboratorWorklogs, collaborator)) {
            updateJiraWorklogForCollab(collaboratorWorklogs, worklogItemDTO, collaborator);
        } else {
            addJiraWorklogForCollab(collaboratorWorklogs, worklogItemDTO);
        }
    }

    private void addDailyWorklog(WorklogItemDTO worklogItemDTO, JiraCollaboratorWorklog collaborartorWorklog) {
        int worklogDay = getWorklogDay(worklogItemDTO);
        TimeSpentDTO timeSpentDTO = TimeSpentCalculatorUtil.calculate(worklogItemDTO.getTimeSpent());
        JiraDailyWorklog jiraDailyWorklog = jiraImputationFactory.createJiraDailyWorklog(worklogDay, worklogItemDTO.getStarted(), worklogItemDTO.getTimeSpent(), timeSpentDTO);
        collaborartorWorklog.getJiraDailyWorklogs().add(jiraDailyWorklog);
    }

    private int getWorklogDay(WorklogItemDTO worklogItemDTO) {
        return DateUtil.getDay(jiraImputationService.getWorklogDate(worklogItemDTO.getStarted()));
    }

    private void updateDailyWorklog(List<JiraDailyWorklog> jiraDailyWorklogs, WorklogItemDTO worklogItemDTO) {
        JiraDailyWorklog jiraDailyWorklog = findDailyWorklog(jiraDailyWorklogs, getWorklogDay(worklogItemDTO));
        jiraDailyWorklog.setDate(worklogItemDTO.getStarted());
        jiraDailyWorklog.setWorklogTimeSpent(jiraDailyWorklog.getWorklogTimeSpent() + SPACE + worklogItemDTO.getTimeSpent());
        jiraDailyWorklog.setTimeSpent(TimeSpentCalculatorUtil.calculate(jiraDailyWorklog.getWorklogTimeSpent()));
    }

    private JiraDailyWorklog findDailyWorklog(List<JiraDailyWorklog> jiraDailyWorklogs, int day) {
        return jiraDailyWorklogs.stream().filter(worklog -> worklog.getDay() == day).findFirst().get();
    }

    private boolean isDailyWorklogAlreadyExist(List<JiraDailyWorklog> jiraDailyWorklogs, int day) {
        return jiraDailyWorklogs.stream().anyMatch(worklog -> worklog.getDay() == day);
    }

    private void addJiraWorklogForCollab(List<JiraCollaboratorWorklog> worklogs, WorklogItemDTO worklogItemDTO) {
        Collaborator collaborator = findCollaboratorByFirstAndLastName(worklogItemDTO.getAuthor().getDisplayName());
        JiraCollaboratorWorklog collaborartorWorklog = jiraImputationFactory.createJiraCollaboratorWorklog(collaborator);
        addDailyWorklog(worklogItemDTO, collaborartorWorklog);
        worklogs.add(collaborartorWorklog);
    }

    private Collaborator findCollaboratorByFirstAndLastName(String displayName) {
        return collaborators.stream().filter(
            collaborator -> compareFullName(displayName, collaborator.getFirstname(), collaborator.getLastname())
                || compareFullName(displayName, collaborator.getLastname(), collaborator.getFirstname())
        ).findFirst().get();
    }

    private boolean compareFullName(String name, String firstname, String lastname) {
        return name.equalsIgnoreCase(firstname + SPACE + lastname);
    }

    private boolean isWorklogAlreadyExistForCollab(List<JiraCollaboratorWorklog> worklogs, Collaborator collaborator) {
        return worklogs.stream().anyMatch(worklog -> compareCollabsById(collaborator, worklog.getCollaborator()));
    }

    private void updateJiraWorklogForCollab(List<JiraCollaboratorWorklog> worklogs, WorklogItemDTO worklogItemDTO, Collaborator collaborator) {
        JiraCollaboratorWorklog worklog = findWorklogOfCollab(worklogs, collaborator);
        if (isDailyWorklogAlreadyExist(worklog.getJiraDailyWorklogs(), getWorklogDay(worklogItemDTO))) {
            updateDailyWorklog(worklog.getJiraDailyWorklogs(), worklogItemDTO);
        } else {
            addDailyWorklog(worklogItemDTO, worklog);
        }

    }

    private JiraCollaboratorWorklog findWorklogOfCollab(List<JiraCollaboratorWorklog> worklogs, Collaborator collaborator) {
        return worklogs.stream().filter(worklog -> compareCollabsById(collaborator, worklog.getCollaborator())).findFirst().get();
    }

    private boolean compareCollabsById(Collaborator collaborator, Collaborator worklogCollaborator) {
        return worklogCollaborator.getId().equals(collaborator.getId());
    }

}
