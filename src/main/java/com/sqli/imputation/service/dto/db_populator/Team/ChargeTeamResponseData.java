package com.sqli.imputation.service.dto.db_populator.Team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sqli.imputation.service.dto.ChargeTeamDTO;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargeTeamResponseData {

    private List<ChargeTeamDTO> charge;

    public List<ChargeTeamDTO> getCharge() {
        return charge;
    }

    public void setCharge(List<ChargeTeamDTO> charge) {
        this.charge = charge;
    }

    @Override
    public String toString() {
        return "ChargeTeamResponseData{" +
            "charge=" + charge +
            '}';
    }
}
