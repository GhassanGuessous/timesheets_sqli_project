package com.sqli.imputation.service.db_populator.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityRestResponse {

    private int code;
    private ActivityResponseData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ActivityResponseData getData() {
        return data;
    }

    public void setData(ActivityResponseData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ActivityRestResponse{" +
            "code=" + code +
            ", data=" + data +
            '}';
    }
}
