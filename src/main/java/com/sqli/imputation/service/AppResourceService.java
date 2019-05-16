package com.sqli.imputation.service;

import com.sqli.imputation.service.dto.AppChargeDTO;
import com.sqli.imputation.service.dto.AppRequestDTO;

import java.util.List;

public interface AppResourceService {

    List<AppChargeDTO> getTeamCharges(AppRequestDTO requestBody);

}
