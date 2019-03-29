package com.sqli.imputation.service.dto.db_populator.Team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargeTeamRestResponse {

    private int code;
    private ChargeTeamResponseData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ChargeTeamResponseData getData() {
        return data;
    }

    public void setData(ChargeTeamResponseData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ChargeTeamRestResponse{" +
            "code=" + code +
            ", data=" + data +
            '}';
    }
}
