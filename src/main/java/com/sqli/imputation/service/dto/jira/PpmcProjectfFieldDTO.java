package com.sqli.imputation.service.dto.jira;

public class PpmcProjectfFieldDTO {

    private String self;
    private String value;
    private String id;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PpmcProjectfFieldDTO{" +
            "self='" + self + '\'' +
            ", value='" + value + '\'' +
            ", id='" + id + '\'' +
            '}';
    }
}
