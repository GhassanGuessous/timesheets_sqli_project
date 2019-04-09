package com.sqli.imputation.service;

import com.sqli.imputation.domain.Imputation;
import com.sqli.imputation.service.dto.AppChargeDTO;
import com.sqli.imputation.service.dto.AppRequestDTO;

import java.util.List;

public interface AppImputationConverterService {

    Imputation convert(AppRequestDTO appRequestDTO, List<AppChargeDTO> appChargeDTOS);
}
