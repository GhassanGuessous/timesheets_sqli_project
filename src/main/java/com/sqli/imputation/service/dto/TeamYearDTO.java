package com.sqli.imputation.service.dto;

import com.sqli.imputation.domain.Team;

public class TeamYearDTO {

    private Team team;
    private int year;
    private int month;

    public TeamYearDTO() {
    }

    public Team getTeam() {
        return team;
    }

    public int getYear() {
        return year;
    }

    public void setTeam(Team team) {
        this.team = team;
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
        return "TeamYearDTO{" +
            "team=" + team +
            ", year=" + year +
            ", month=" + month +
            '}';
    }
}
