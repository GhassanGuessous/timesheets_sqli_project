package com.sqli.imputation.service.dto;

import com.sqli.imputation.domain.Team;

public class AppTbpRequestBodyDTO {

    private Team team;
    private int year;
    private int month;

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

    @Override
    public String toString() {
        return "AppTbpRequestBody{" +
            "team=" + team +
            ", year=" + year +
            ", month=" + month +
            '}';
    }
}
