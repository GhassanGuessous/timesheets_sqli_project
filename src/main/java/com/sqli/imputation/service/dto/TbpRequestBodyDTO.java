package com.sqli.imputation.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TbpRequestBodyDTO {

    private String idTbp;
    private String startDate;
    private String endDate;

    public TbpRequestBodyDTO(String idTbp) {
        this.idTbp = idTbp;
    }

    @JsonCreator()
    public TbpRequestBodyDTO(@JsonProperty("idTbp") String idTbp, @JsonProperty("startDate") String startDate, @JsonProperty("endDate") String endDate) {
        this.idTbp = idTbp;
        this.startDate = startDate;
        this.endDate = endDate;
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

    @Override
    public String toString() {
        return "TbpRequestBodyDTO{" +
            "idTbp='" + idTbp + '\'' +
            ", startDate='" + startDate + '\'' +
            ", endDate='" + endDate + '\'' +
            '}';
    }
}
