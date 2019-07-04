package com.sqli.imputation.service.dto.jira;

public class IssueTypeDTO {

    private String self;
    private String id;
    private String name;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "IssueTypeDTO{" +
            "self='" + self + '\'' +
            ", id='" + id + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
