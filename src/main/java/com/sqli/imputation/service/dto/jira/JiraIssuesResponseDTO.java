package com.sqli.imputation.service.dto.jira;

import java.util.List;

public class JiraIssuesResponseDTO {

    private int startAt;
    private int maxResults;
    private int total;
    private List<IssueDTO> issues;

    public int getStartAt() {
        return startAt;
    }

    public void setStartAt(int startAt) {
        this.startAt = startAt;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<IssueDTO> getIssues() {
        return issues;
    }

    public void setIssues(List<IssueDTO> issues) {
        this.issues = issues;
    }

    @Override
    public String toString() {
        return "JiraIssuesResponseDTO{" +
            "startAt=" + startAt +
            ", maxResults=" + maxResults +
            ", total=" + total +
            '}';
    }
}
