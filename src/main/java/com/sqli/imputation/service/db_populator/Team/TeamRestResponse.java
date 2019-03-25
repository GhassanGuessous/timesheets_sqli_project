package com.sqli.imputation.service.db_populator.Team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamRestResponse {

    private int code;
    private TeamResponseData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public TeamResponseData getData() {
        return data;
    }

    public void setData(TeamResponseData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TeamRestResponse{" +
            "code=" + code +
            ", data=" + data +
            '}';
    }
}
