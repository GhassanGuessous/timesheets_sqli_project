package com.sqli.imputation.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TbpRequestBodyDTO {

    private String idTbp;
    private String startDate;
    private String endDate;
    private String username;
    private String password;

    public TbpRequestBodyDTO(String idTbp) {
        this.idTbp = idTbp;
    }

    @JsonCreator()
    public TbpRequestBodyDTO(
        @JsonProperty("idTbp") String idTbp,
        @JsonProperty("startDate") String startDate,
        @JsonProperty("endDate") String endDate,
        @JsonProperty("username") String username,
        @JsonProperty("password") String password
    ) {
        this.idTbp = idTbp;
        this.startDate = startDate;
        this.endDate = endDate;
        this.username = username;
        this.password = password;
    }

    public String getIdTbp() {
        return idTbp;
    }

    public void setIdTbp(String idTbp) {
        this.idTbp = idTbp;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    @Override
    public String toString() {
        return "TbpRequestBodyDTO{" +
            "idTbp='" + idTbp + '\'' +
            ", startDate='" + startDate + '\'' +
            ", endDate='" + endDate + '\'' +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}
