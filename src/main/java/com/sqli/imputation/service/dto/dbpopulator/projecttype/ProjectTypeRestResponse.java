package com.sqli.imputation.service.dto.dbpopulator.projecttype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectTypeRestResponse {

    private int code;
    private ProjectTypeResponseData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ProjectTypeResponseData getData() {
        return data;
    }

    public void setData(ProjectTypeResponseData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ProjectTypeRestResponse{" +
            "code=" + code +
            ", data=" + data +
            '}';
    }
}
