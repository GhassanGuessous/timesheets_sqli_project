package com.sqli.imputation.service.dto.jira;

public class IssueDTO {

    private String id;
    private String self;
    private String key;
    private IssueFieldsDTO fields;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public IssueFieldsDTO getFields() {
        return fields;
    }

    public void setFields(IssueFieldsDTO fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "IssueDTO{" +
            "id='" + id + '\'' +
            ", self='" + self + '\'' +
            ", key='" + key + '\'' +
            ", fields='" + fields + '\'' +
            '}';
    }
}
