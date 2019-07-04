package com.sqli.imputation.service.dto.jira;

import java.util.ArrayList;
import java.util.List;

public class JiraImputationDTO {

    private int year;
    private int month;
    private List<JiraCollaboratorWorklog> collaboratorWorklogs;

    public JiraImputationDTO(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public List<JiraCollaboratorWorklog> getCollaboratorWorklogs() {
        if (collaboratorWorklogs == null) {
            collaboratorWorklogs = new ArrayList<>();
        }
        return collaboratorWorklogs;
    }

    public void setCollaboratorWorklogs(List<JiraCollaboratorWorklog> collaboratorWorklogs) {
        this.collaboratorWorklogs = collaboratorWorklogs;
    }

    @Override
    public String toString() {
        return "JiraImputationDTO{" +
            "year=" + year +
            ", month=" + month +
            ", collaboratorWorklogs=" + collaboratorWorklogs +
            '}';
    }
}
