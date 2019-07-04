package com.sqli.imputation.service.factory;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.dto.jira.*;
import org.springframework.stereotype.Component;

@Component
public class JiraImputationFactory {


    public static final int DEFAULT_FREQUANCY = 1;

    public JiraImputationDTO createJiraImputationDTO(int year, int month) {
        return new JiraImputationDTO(year, month);
    }

    public JiraCollaboratorWorklog createJiraCollaboratorWorklog(Collaborator collaborator) {
        return new JiraCollaboratorWorklog(collaborator);
    }

    public JiraDailyWorklog createJiraDailyWorklog(int day, String date, String worklogTimeSpent, TimeSpentDTO timeSpent) {
        return new JiraDailyWorklog(day, date, worklogTimeSpent, timeSpent);
    }

    public PpmcProjectWorklogDTO createPpmcProjectWorkolgDTO(String ppmcProject) {
        return new PpmcProjectWorklogDTO(ppmcProject, DEFAULT_FREQUANCY);
    }

    public IssueTypeStatisticsDTO createIssueTypeStatisticsDTO(String issueType) {
        return new IssueTypeStatisticsDTO(issueType);
    }
}
