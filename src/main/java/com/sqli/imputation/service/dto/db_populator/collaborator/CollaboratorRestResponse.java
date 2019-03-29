package com.sqli.imputation.service.dto.db_populator.collaborator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollaboratorRestResponse {

    private int code;
    private CollaboratorResponseData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CollaboratorResponseData getData() {
        return data;
    }

    public void setData(CollaboratorResponseData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CollaboratorRestResponse{" +
            "code=" + code +
            ", data=" + data +
            '}';
    }
}
