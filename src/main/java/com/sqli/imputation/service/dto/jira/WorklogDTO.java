package com.sqli.imputation.service.dto.jira;

import java.util.List;

public class WorklogDTO {

    private int startAt;
    private int maxResults;
    private int total;
    private List<WorklogItemDTO> worklogs;

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

    public List<WorklogItemDTO> getWorklogs() {
        return worklogs;
    }

    public void setWorklogs(List<WorklogItemDTO> worklogs) {
        this.worklogs = worklogs;
    }

    @Override
    public String toString() {
        return "WorklogDTO{" +
            "startAt=" + startAt +
            ", maxResults=" + maxResults +
            ", total=" + total +
            ", worklogs=" + worklogs +
            '}';
    }
}
