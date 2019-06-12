package com.sqli.imputation.service.dto.jira;

import com.sqli.imputation.domain.Collaborator;

import java.util.ArrayList;
import java.util.List;

public class JiraCollaboratorWorklog {

    private Collaborator collaborator;
    private String date;
    private List<JiraDailyWorklog> jiraDailyWorklogs;
    private TimeSpentDTO totalTimeSpent;

    public Collaborator getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public TimeSpentDTO getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(TimeSpentDTO totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public List<JiraDailyWorklog> getJiraDailyWorklogs() {
        if (jiraDailyWorklogs == null) {
            jiraDailyWorklogs = new ArrayList<>();
        }
        return jiraDailyWorklogs;
    }

    public void setJiraDailyWorklogs(List<JiraDailyWorklog> jiraDailyWorklogs) {
        this.jiraDailyWorklogs = jiraDailyWorklogs;
    }

    @Override
    public String toString() {
        return "JiraCollaborartorWorklog{" +
            "collaborator=" + collaborator +
            ", date='" + date + '\'' +
            ", totalTimeSpent='" + totalTimeSpent + '\'' +
            ", dailies='" + jiraDailyWorklogs + '\'' +
            '}';
    }
}

