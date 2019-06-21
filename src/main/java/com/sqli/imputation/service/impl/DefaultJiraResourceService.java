package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.domain.Team;
import com.sqli.imputation.service.JiraLoginService;
import com.sqli.imputation.service.JiraResourceService;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.jira.*;
import com.sqli.imputation.service.factory.JiraImputationFactory;
import com.sqli.imputation.service.util.DateUtil;
import com.sqli.imputation.service.util.TimeSpentCalculatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultJiraResourceService implements JiraResourceService {

    private final Logger log = LoggerFactory.getLogger(DefaultJiraResourceService.class);

    public static final String TIME_DELIMITER = "T";
    public static final int DATE_POSITION = 0;
    public static final String TIME_SPENT_SEPARATOR = " ";


    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    JiraLoginService jiraLoginService;
    @Autowired
    JiraImputationFactory jiraImputationFactory;

    @Override
    public JiraImputationDTO getJiraImputation(Team team, TbpRequestBodyDTO requestBodyDTO) {
        JiraImputationDTO jiraImputationDTO = new JiraImputationDTO(DateUtil.getYear(requestBodyDTO.getStartDate()), DateUtil.getMonth(requestBodyDTO.getStartDate()));
        this.restTemplate = jiraLoginService.logIn(requestBodyDTO.getUsername(), requestBodyDTO.getPassword());
        team.getCollaborators().forEach(collaborator -> getIssuesForCollab(requestBodyDTO, jiraImputationDTO, collaborator));
        return jiraImputationDTO;
    }

    private void getIssuesForCollab(TbpRequestBodyDTO requestBodyDTO, JiraImputationDTO jiraImputationDTO, Collaborator collaborator) {
        List<IssueDTO> issueDTOS = new ArrayList<>();
        ResponseEntity<JiraIssuesResponseDTO> responseEntity = getCollaboratorIssues(requestBodyDTO, collaborator);
        addAllIssues(issueDTOS, responseEntity);
        if (hasMoreIssues(responseEntity)) {
            responseEntity = getCollaboratorIssuesWithPagination(requestBodyDTO, collaborator, responseEntity.getBody().getMaxResults(), responseEntity.getBody().getTotal());
            addAllIssues(issueDTOS, responseEntity);
        }
        jiraImputationDTO.getCollaboratorWorklogs().add(getIssuesWorklogs(requestBodyDTO, issueDTOS, collaborator));
    }

    private boolean hasMoreIssues(ResponseEntity<JiraIssuesResponseDTO> responseEntity) {
        return responseEntity.getBody().getTotal() > responseEntity.getBody().getMaxResults();
    }

    private void addAllIssues(List<IssueDTO> issueDTOS, ResponseEntity<JiraIssuesResponseDTO> responseEntity) {
        issueDTOS.addAll(responseEntity.getBody().getIssues());
    }

    private ResponseEntity<JiraIssuesResponseDTO> getCollaboratorIssuesWithPagination(TbpRequestBodyDTO requestBodyDTO, Collaborator collaborator, int startAt, int maxResult) {
        log.debug("DefaultJiraResourceService.getCollaboratorIssuesWithPagination: request to get issues from JIRA");
        return restTemplate.exchange("https://jira.nespresso.com/rest/api/2/search?jql=worklogAuthor='" + collaborator.getFirstname() + " " + collaborator.getLastname() + "'+AND+issueFunction in workLogged('after " + requestBodyDTO.getStartDate() + " before " + requestBodyDTO.getEndDate() + "')&startAt=" + startAt + "&maxResults=" + maxResult + "&fields=worklog", HttpMethod.GET, jiraLoginService.getTbpHttpHeaders(), JiraIssuesResponseDTO.class);
    }

    private ResponseEntity<JiraIssuesResponseDTO> getCollaboratorIssues(TbpRequestBodyDTO requestBodyDTO, Collaborator collaborator) {
        log.debug("DefaultJiraResourceService.getCollaboratorIssues: request to get issues from JIRA");
        return restTemplate.exchange("https://jira.nespresso.com/rest/api/2/search?jql=worklogAuthor='" + collaborator.getFirstname() + " " + collaborator.getLastname() + "'+AND+issueFunction in workLogged('after " + requestBodyDTO.getStartDate() + " before " + requestBodyDTO.getEndDate() + "')&fields=worklog", HttpMethod.GET, jiraLoginService.getTbpHttpHeaders(), JiraIssuesResponseDTO.class);
    }

    private JiraCollaboratorWorklog getIssuesWorklogs(TbpRequestBodyDTO requestBodyDTO, List<IssueDTO> issueDTOS, Collaborator collaborator) {
        log.debug("DefaultJiraResourceService.getIssuesWorklogs: fetching issues for Collaborator");
        JiraCollaboratorWorklog collaboratorWorklog = new JiraCollaboratorWorklog();
        collaboratorWorklog.setCollaborator(collaborator);
        issueDTOS.forEach(issueDTO -> getWorklogOfIssue(collaboratorWorklog, requestBodyDTO, issueDTO));
        updateTotalTimeSpent(collaboratorWorklog);
        return collaboratorWorklog;
    }

    private void updateTotalTimeSpent(JiraCollaboratorWorklog worklog) {
        StringBuilder builder = new StringBuilder();
        worklog.getJiraDailyWorklogs().forEach(dailyWorklog -> builder.append(dailyWorklog.getWorklogTimeSpent()).append(" "));
        worklog.setTotalTimeSpent(TimeSpentCalculatorUtil.calculate(builder.toString()));
    }

    private void getWorklogOfIssue(JiraCollaboratorWorklog collaboratorWorklog, TbpRequestBodyDTO requestBodyDTO, IssueDTO issueDTO) {
        if (hasMoreWorklogs(issueDTO.getFields().getWorklog())) {
            issueDTO.getFields().setWorklog(getAllWorklogsOfIssue(issueDTO));
        }
        issueDTO.getFields().getWorklog().getWorklogs().stream()
            .filter(worklogItemDTO -> isValidWorklogItem(worklogItemDTO, collaboratorWorklog.getCollaborator(), requestBodyDTO))
            .forEach(worklogItemDTO -> getCollaboratorWorklog(collaboratorWorklog, worklogItemDTO));
    }

    private WorklogDTO getAllWorklogsOfIssue(IssueDTO issueDTO) {
        log.debug("DefaultJiraResourceService.getAllWorklogsOfIssue: request to get issue's workLogs from JIRA");
        return restTemplate.exchange("https://jira.nespresso.com/rest/api/2/issue/" + issueDTO.getId() + "/worklog", HttpMethod.GET, jiraLoginService.getTbpHttpHeaders(), WorklogDTO.class).getBody();
    }

    private boolean hasMoreWorklogs(WorklogDTO worklog) {
        return worklog.getTotal() > worklog.getMaxResults();
    }

    private boolean isValidWorklogItem(WorklogItemDTO worklogItemDTO, Collaborator collaborator, TbpRequestBodyDTO requestBodyDTO) {
        return worklogItemDateIsValid(worklogItemDTO, requestBodyDTO) && isWorklogItemOfCollab(worklogItemDTO, collaborator);
    }

    private boolean isWorklogItemOfCollab(WorklogItemDTO worklogItemDTO, Collaborator collaborator) {
        return compareFullName(worklogItemDTO.getAuthor().getDisplayName(), collaborator.getFirstname(), collaborator.getLastname())
            || compareFullName(worklogItemDTO.getAuthor().getDisplayName(), collaborator.getLastname(), collaborator.getFirstname());
    }

    private boolean compareFullName(String name, String firstname, String lastname) {
        return name.equalsIgnoreCase(firstname + " " + lastname);
    }

    private boolean worklogItemDateIsValid(WorklogItemDTO worklogItemDTO, TbpRequestBodyDTO requestBodyDTO) {
        return DateUtil.isSameYearAndMonth(worklogItemDTO.getStarted().split(TIME_DELIMITER)[DATE_POSITION], requestBodyDTO.getStartDate())&&worklogItemDayIsRequested(worklogItemDTO, requestBodyDTO);
    }

    private boolean worklogItemDayIsRequested(WorklogItemDTO worklogItemDTO, TbpRequestBodyDTO requestBodyDTO) {
        return getWorklogDay(worklogItemDTO)>= DateUtil.getDay(requestBodyDTO.getStartDate());
    }

    private void getCollaboratorWorklog(JiraCollaboratorWorklog collaboratorWorklog, WorklogItemDTO worklogItemDTO) {
        if (isDailyWorklogAlreadyExist(collaboratorWorklog.getJiraDailyWorklogs(), getWorklogDay(worklogItemDTO))) {
            updateDailyWorklog(collaboratorWorklog.getJiraDailyWorklogs(), worklogItemDTO);
        } else {
            addDailyWorklog(worklogItemDTO, collaboratorWorklog);
        }
    }

    private void addDailyWorklog(WorklogItemDTO worklogItemDTO, JiraCollaboratorWorklog collaborartorWorklog) {
        JiraDailyWorklog jiraDailyWorklog = new JiraDailyWorklog();
        jiraDailyWorklog.setDate(worklogItemDTO.getStarted());
        jiraDailyWorklog.setWorklogTimeSpent(worklogItemDTO.getTimeSpent());
        jiraDailyWorklog.setDay(getWorklogDay(worklogItemDTO));
        jiraDailyWorklog.setTimeSpent(TimeSpentCalculatorUtil.calculate(jiraDailyWorklog.getWorklogTimeSpent()));
        collaborartorWorklog.getJiraDailyWorklogs().add(jiraDailyWorklog);
    }

    private int getWorklogDay(WorklogItemDTO worklogItemDTO) {
        return DateUtil.getDay(worklogItemDTO.getStarted().split(TIME_DELIMITER)[DATE_POSITION]);
    }

    private void updateDailyWorklog(List<JiraDailyWorklog> jiraDailyWorklogs, WorklogItemDTO worklogItemDTO) {
        JiraDailyWorklog jiraDailyWorklog = findDailyWorklog(jiraDailyWorklogs, getWorklogDay(worklogItemDTO));
        jiraDailyWorklog.setDate(worklogItemDTO.getStarted());
        jiraDailyWorklog.setWorklogTimeSpent(jiraDailyWorklog.getWorklogTimeSpent() + TIME_SPENT_SEPARATOR + worklogItemDTO.getTimeSpent());
        jiraDailyWorklog.setTimeSpent(TimeSpentCalculatorUtil.calculate(jiraDailyWorklog.getWorklogTimeSpent()));
    }

    private JiraDailyWorklog findDailyWorklog(List<JiraDailyWorklog> jiraDailyWorklogs, int day) {
        return jiraDailyWorklogs.stream().filter(worklog -> worklog.getDay() == day).findFirst().get();
    }

    private boolean isDailyWorklogAlreadyExist(List<JiraDailyWorklog> jiraDailyWorklogs, int day) {
        return jiraDailyWorklogs.stream().anyMatch(worklog -> worklog.getDay() == day);
    }


//    private void addJiraWorklogForCollab(List<JiraCollaboratorWorklog> worklogs, WorklogItemDTO worklogItemDTO, Collaborator collaborator) {
//        JiraCollaboratorWorklog collaborartorWorklog = new JiraCollaboratorWorklog();
//        collaborartorWorklog.setDate(worklogItemDTO.getStarted()());
//        collaborartorWorklog.setCollaborator(collaborator);
//        addDailyWorklog(worklogItemDTO, collaborartorWorklog);
//        worklogs.add(collaborartorWorklog);
//    }
//
//    private boolean isWorklogAlreadyExistForCollab(List<JiraCollaboratorWorklog> worklogs, Collaborator collaborator) {
//        if (isExternalCollaborator(collaborator)) {
//            return worklogs.stream().anyMatch(worklog -> compareCollabsByFirstname(collaborator, worklog.getCollaborator()));
//        }
//        return worklogs.stream().anyMatch(worklog -> isExternalCollaborator(worklog.getCollaborator()) ? compareCollabsByFirstname(collaborator, worklog.getCollaborator()) : compareCollabsById(collaborator, worklog.getCollaborator()));
//    }
//
//    private IssueDTO getIssueDetails(IssueDTO issueDTO) {
//        return restTemplate.exchange(issueDTO.getSelf(), HttpMethod.GET, getTbpHttpHeaders(), IssueDTO.class).getBody();
//    }
//
//    private void updateJiraWorklogForCollab(List<JiraCollaboratorWorklog> worklogs, WorklogItemDTO worklogItemDTO, Collaborator collaborator) {
//        JiraCollaboratorWorklog worklog = findWorklogOfCollab(worklogs, collaborator);
//        if (isDailyWorklogAlreadyExist(worklog.getJiraDailyWorklogs(), getWorklogDay(worklogItemDTO))) {
//            updateDailyWorklog(worklog.getJiraDailyWorklogs(), worklogItemDTO);
//        } else {
//            addDailyWorklog(worklogItemDTO, worklog);
//        }
//
//    }
//private JiraCollaboratorWorklog findWorklogOfCollab(List<JiraCollaboratorWorklog> worklogs, Collaborator collaborator) {
//    if (isExternalCollaborator(collaborator)) {
//        return worklogs.stream().filter(worklog -> compareCollabsByFirstname(collaborator, worklog.getCollaborator())).findFirst().get();
//    }
//    return worklogs.stream().filter(worklog -> isExternalCollaborator(worklog.getCollaborator()) ? compareCollabsByFirstname(collaborator, worklog.getCollaborator()) : compareCollabsById(collaborator, worklog.getCollaborator())).findFirst().get();
//}
//private boolean isExternalCollaborator(Collaborator collaborator) {
//    return collaborator.getId() == null;
//}
//
//    private boolean compareCollabsById(Collaborator collaborator, Collaborator worklogCollaborator) {
//        return worklogCollaborator.getId().equals(collaborator.getId());
//    }
//
//    private boolean compareCollabsByFirstname(Collaborator collaborator, Collaborator worklogCollaborator) {
//        return worklogCollaborator.getFirstname().equals(collaborator.getFirstname());
//    }
}
