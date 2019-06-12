package com.sqli.imputation.service.dto.jira;

public class AuthorDTO {

    private String name;
    private String key;
    private String emailAddress;
    private String displayName;
    private String timeZone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public String toString() {
        return "AuthorDTO{" +
            "name='" + name + '\'' +
            ", key='" + key + '\'' +
            ", emailAddress='" + emailAddress + '\'' +
            ", displayName='" + displayName + '\'' +
            ", timeZone='" + timeZone + '\'' +
            '}';
    }
}
