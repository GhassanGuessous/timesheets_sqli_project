package com.sqli.imputation.service;

import com.sqli.imputation.domain.Imputation;
import com.sqli.imputation.service.dto.ChargeTeamDTO;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;

import java.util.List;

public interface TbpImputationConverterService {

    Imputation convert(List<ChargeTeamDTO> chargeTeamDTOS, TbpRequestBodyDTO tbpRequestBodyDTO);
}