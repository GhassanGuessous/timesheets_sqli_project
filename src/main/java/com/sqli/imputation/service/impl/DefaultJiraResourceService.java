package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.CollaboratorService;
import com.sqli.imputation.service.JiraResourceService;
import com.sqli.imputation.service.dto.AppTbpRequestBodyDTO;
import com.sqli.imputation.service.dto.jira.*;
import com.sqli.imputation.service.util.DateUtil;
import com.sqli.imputation.service.util.TimeSpentCalculatorUtil;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultJiraResourceService implements JiraResourceService {

    private static final String JIRA_AUTH_URL = "https://jira.nespresso.com/rest/gadget/1.0/login";
    public static final String TIME_DELIMITER = "T";
    public static final int DATE_POSITION = 0;
    public static final String TIME_SPENT_SEPARATOR = " ";
    public static final String JIRA_LOGIN_URL_FORMAT = "?os_username=%s&os_password=%s";


    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CollaboratorService collaboratorService;

    @Override
    public JiraImputationDTO getAllStories(AppTbpRequestBodyDTO requestBodyDTO) {
        List<IssueDTO> issueDTOS = new ArrayList<>();
        JiraImputationDTO jiraImputationDTO = new JiraImputationDTO(requestBodyDTO.getYear(), requestBodyDTO.getMonth());
        logIn("kraouin", "Ironm@n102019");
        ResponseEntity<JiraIssuesResponseDTO> responseEntity = restTemplate.exchange("https://jira.nespresso.com/rest/api/2/search?jql=team=" + requestBodyDTO.getTeam().getDisplayName() + "+AND+issueFunction in workLogged('after " + DateUtil.getDateOfFirstDay(requestBodyDTO.getYear(), requestBodyDTO.getMonth()) + " before " + DateUtil.getDateOfLastDay(requestBodyDTO.getYear(), requestBodyDTO.getMonth()) + "')", HttpMethod.GET, getTbpHttpHeaders(), JiraIssuesResponseDTO.class);
        responseEntity.getBody().getIssues().forEach(issueDTO -> {
            IssueDTO issueDetails = getIssueDetails(issueDTO);
            issueDTOS.add(issueDetails);
        });
        jiraImputationDTO.setCollaboratorWorklogs(getIssuesWorklogs(requestBodyDTO, issueDTOS));
        return jiraImputationDTO;
    }

    private List<JiraCollaboratorWorklog> getIssuesWorklogs(AppTbpRequestBodyDTO requestBodyDTO, List<IssueDTO> issueDTOS) {
        List<JiraCollaboratorWorklog> worklogs = new ArrayList<>();
        issueDTOS.forEach(issueDTO -> getWorklogOfIssue(requestBodyDTO, worklogs, issueDTO));
        for (JiraCollaboratorWorklog worklog : worklogs) {
            updateTotalTimeSpent(worklog);
        }
        return worklogs;
    }

    private void updateTotalTimeSpent(JiraCollaboratorWorklog worklog) {
        StringBuilder builder = new StringBuilder();
        worklog.getJiraDailyWorklogs().forEach(dailyWorklog -> builder.append(dailyWorklog.getWorklogTimeSpent()).append(" "));
        worklog.setTotalTimeSpent(TimeSpentCalculatorUtil.calculate(builder.toString()));
    }

    private void getWorklogOfIssue(AppTbpRequestBodyDTO requestBodyDTO, List<JiraCollaboratorWorklog> worklogs, IssueDTO issueDTO) {
        issueDTO.getFields().getWorklog().getWorklogs().forEach(worklogItemDTO -> {
            if (DateUtil.getMonth(worklogItemDTO.getUpdated().split(TIME_DELIMITER)[DATE_POSITION]) != requestBodyDTO.getMonth())
                return;
            getCollaboratorWorklogs(worklogs, worklogItemDTO);
        });
    }

    private void getCollaboratorWorklogs(List<JiraCollaboratorWorklog> worklogs, WorklogItemDTO worklogItemDTO) {
        Collaborator collaborator = collaboratorService.findByFirstnameAndLastname(worklogItemDTO.getAuthor().getDisplayName());
        if (collaborator == null) {
            collaborator = new Collaborator().firstname(worklogItemDTO.getAuthor().getDisplayName()).email(worklogItemDTO.getAuthor().getEmailAddress());
        }
        if (isWorklogAlreadyExistForCollab(worklogs, collaborator)) {
            updateJiraWorklogForCollab(worklogs, worklogItemDTO, collaborator);
        } else {
            addJiraWorklogForCollab(worklogs, worklogItemDTO, collaborator);
        }
    }

    private void addJiraWorklogForCollab(List<JiraCollaboratorWorklog> worklogs, WorklogItemDTO worklogItemDTO, Collaborator collaborator) {
        JiraCollaboratorWorklog collaborartorWorklog = new JiraCollaboratorWorklog();
        collaborartorWorklog.setDate(worklogItemDTO.getUpdated());
        collaborartorWorklog.setCollaborator(collaborator);
        addDailyWorklog(worklogItemDTO, collaborartorWorklog);
        worklogs.add(collaborartorWorklog);
    }

    private void addDailyWorklog(WorklogItemDTO worklogItemDTO, JiraCollaboratorWorklog collaborartorWorklog) {
        JiraDailyWorklog jiraDailyWorklog = new JiraDailyWorklog();
        jiraDailyWorklog.setDate(worklogItemDTO.getUpdated());
        jiraDailyWorklog.setWorklogTimeSpent(worklogItemDTO.getTimeSpent());
        jiraDailyWorklog.setDay(getWorklogDay(worklogItemDTO));
        jiraDailyWorklog.setTimeSpent(TimeSpentCalculatorUtil.calculate(jiraDailyWorklog.getWorklogTimeSpent()));
        collaborartorWorklog.getJiraDailyWorklogs().add(jiraDailyWorklog);
    }

    private int getWorklogDay(WorklogItemDTO worklogItemDTO) {
        return DateUtil.getDay(worklogItemDTO.getUpdated().split(TIME_DELIMITER)[DATE_POSITION]);
    }

    private void updateJiraWorklogForCollab(List<JiraCollaboratorWorklog> worklogs, WorklogItemDTO worklogItemDTO, Collaborator collaborator) {
        JiraCollaboratorWorklog worklog = findWorklogOfCollab(worklogs, collaborator);
        if (isDailyWorklogAlreadyExist(worklog.getJiraDailyWorklogs(), getWorklogDay(worklogItemDTO))) {
            updateDailyWorklog(worklog.getJiraDailyWorklogs(), worklogItemDTO);
        } else {
            addDailyWorklog(worklogItemDTO, worklog);
        }

    }

    private void updateDailyWorklog(List<JiraDailyWorklog> jiraDailyWorklogs, WorklogItemDTO worklogItemDTO) {
        JiraDailyWorklog jiraDailyWorklog = findDailyWorklog(jiraDailyWorklogs, getWorklogDay(worklogItemDTO));
        jiraDailyWorklog.setDate(worklogItemDTO.getUpdated());
        jiraDailyWorklog.setWorklogTimeSpent(jiraDailyWorklog.getWorklogTimeSpent() + TIME_SPENT_SEPARATOR + worklogItemDTO.getTimeSpent());
        jiraDailyWorklog.setTimeSpent(TimeSpentCalculatorUtil.calculate(jiraDailyWorklog.getWorklogTimeSpent()));
    }

    private JiraDailyWorklog findDailyWorklog(List<JiraDailyWorklog> jiraDailyWorklogs, int day) {
        return jiraDailyWorklogs.stream().filter(worklog -> worklog.getDay() == day).findFirst().get();
    }

    private boolean isDailyWorklogAlreadyExist(List<JiraDailyWorklog> jiraDailyWorklogs, int day) {
        return jiraDailyWorklogs.stream().anyMatch(worklog -> worklog.getDay() == day);
    }

    private JiraCollaboratorWorklog findWorklogOfCollab(List<JiraCollaboratorWorklog> worklogs, Collaborator collaborator) {
        if (isExternalCollaborator(collaborator)) {
            return worklogs.stream().filter(worklog -> compareCollabsByFirstname(collaborator, worklog.getCollaborator())).findFirst().get();
        }
        return worklogs.stream().filter(worklog -> isExternalCollaborator(worklog.getCollaborator()) ? compareCollabsByFirstname(collaborator, worklog.getCollaborator()) : compareCollabsById(collaborator, worklog.getCollaborator())).findFirst().get();
    }

    private boolean isExternalCollaborator(Collaborator collaborator) {
        return collaborator.getId() == null;
    }

    private boolean compareCollabsById(Collaborator collaborator, Collaborator worklogCollaborator) {
        return worklogCollaborator.getId().equals(collaborator.getId());
    }

    private boolean compareCollabsByFirstname(Collaborator collaborator, Collaborator worklogCollaborator) {
        return worklogCollaborator.getFirstname().equals(collaborator.getFirstname());
    }

    private boolean isWorklogAlreadyExistForCollab(List<JiraCollaboratorWorklog> worklogs, Collaborator collaborator) {
        if (isExternalCollaborator(collaborator)) {
            return worklogs.stream().anyMatch(worklog -> compareCollabsByFirstname(collaborator, worklog.getCollaborator()));
        }
        return worklogs.stream().anyMatch(worklog -> isExternalCollaborator(worklog.getCollaborator()) ? compareCollabsByFirstname(collaborator, worklog.getCollaborator()) : compareCollabsById(collaborator, worklog.getCollaborator()));
    }

    private IssueDTO getIssueDetails(IssueDTO issueDTO) {
        return restTemplate.exchange(issueDTO.getSelf(), HttpMethod.GET, getTbpHttpHeaders(), IssueDTO.class).getBody();
    }

    public void logIn(String login, String password) {
        restTemplate = new RestTemplate(ignoreSSL());
        String authURL = String.format(JIRA_AUTH_URL + JIRA_LOGIN_URL_FORMAT, login, password);
        restTemplate.exchange(authURL, HttpMethod.POST, HttpEntity.EMPTY, Object.class);
    }

    public HttpComponentsClientHttpRequestFactory ignoreSSL() {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = null;
        try {
            sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom()
            .setSSLSocketFactory(csf)
            .build();
        HttpComponentsClientHttpRequestFactory requestFactory =
            new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        return requestFactory;
    }

    private HttpEntity<String> getTbpHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>("parameters", headers);
    }
}
