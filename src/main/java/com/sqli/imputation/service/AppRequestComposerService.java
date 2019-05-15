package com.sqli.imputation.service;

import com.sqli.imputation.service.dto.AppRequestDTO;

import java.util.List;

public interface AppRequestComposerService {

    List<AppRequestDTO> divideAppPeriod(AppRequestDTO appRequestDTO);
}
