package com.sqli.imputation.service.dto.jira;


public class IssueFieldsDTO {
    private WorklogDTO worklog;
    private PpmcProjectfFieldDTO customfield_10841;
    private IssueTypeDTO issuetype;

    public WorklogDTO getWorklog() {
        return worklog;
    }

    public void setWorklog(WorklogDTO worklog) {
        this.worklog = worklog;
    }

    public PpmcProjectfFieldDTO getCustomfield_10841() {
        return customfield_10841;
    }

    public void setCustomfield_10841(PpmcProjectfFieldDTO customfield_10841) {
        this.customfield_10841 = customfield_10841;
    }

    public IssueTypeDTO getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(IssueTypeDTO issuetype) {
        this.issuetype = issuetype;
    }

    @Override
    public String toString() {
        return "IssueFieldsDTO{" +
            "worklog=" + worklog +
            "ppmcProject=" + customfield_10841 +
            "issuetype=" + issuetype +
            '}';
    }
}
