package com.sqli.imputation.service;

import com.sqli.imputation.service.dto.AppRequestDTO;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;

import java.util.List;

public interface TbpRequestComposerService {

    List<TbpRequestBodyDTO> tbpDividePeriod(TbpRequestBodyDTO tbpRequestBodyDTO);
    List<AppRequestDTO> appDividePeriod(AppRequestDTO appRequestDTO);
}
