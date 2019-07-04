package com.sqli.imputation.service.dto.jira;

public class WorklogItemDTO {

    private String id;
    private AuthorDTO author;
    private AuthorDTO updateAuthor;
    private String created;
    private String updated;
    private String started;
    private String timeSpent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AuthorDTO getAuthor() {
        return author;
    }

    public void setAuthor(AuthorDTO author) {
        this.author = author;
    }

    public AuthorDTO getUpdateAuthor() {
        return updateAuthor;
    }

    public void setUpdateAuthor(AuthorDTO updateAuthor) {
        this.updateAuthor = updateAuthor;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    @Override
    public String toString() {
        return "WorklogItemDTO{" +
            "id='" + id + '\'' +
            ", author=" + author +
            ", updateAuthor=" + updateAuthor +
            ", created='" + created + '\'' +
            ", updated='" + updated + '\'' +
            ", started='" + started + '\'' +
            ", timeSpent='" + timeSpent + '\'' +
            '}';
    }
}
