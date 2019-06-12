package com.sqli.imputation.service.dto.jira;

public class IssueFieldsDTO {
    private WorklogDTO worklog;

    public WorklogDTO getWorklog() {
        return worklog;
    }

    public void setWorklog(WorklogDTO worklog) {
        this.worklog = worklog;
    }

    @Override
    public String toString() {
        return "IssueFieldsDTO{" +
            "worklog=" + worklog +
            '}';
    }
}
