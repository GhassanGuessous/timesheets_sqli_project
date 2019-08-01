package com.sqli.imputation.service.dto;

import com.sqli.imputation.domain.AppTbpIdentifier;
import com.sqli.imputation.domain.Team;

public class AppTbpRequestBodyDTO {

    private Team team;
    private int year;
    private int month;
    private String username;
    private String password;
    private AppTbpIdentifier appTbpIdentifier;

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AppTbpIdentifier getAppTbpIdentifier() {
        return appTbpIdentifier;
    }

    public void setAppTbpIdentifier(AppTbpIdentifier appTbpIdentifier) {
        this.appTbpIdentifier = appTbpIdentifier;
    }

    @Override
    public String toString() {
        return "AppTbpRequestBodyDTO{" +
            "team=" + team +
            ", year=" + year +
            ", month=" + month +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", appTbpIdentifier=" + appTbpIdentifier +
            '}';
    }
}
