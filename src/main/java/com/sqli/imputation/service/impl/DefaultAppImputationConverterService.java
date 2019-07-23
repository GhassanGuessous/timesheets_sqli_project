package com.sqli.imputation.service.impl;

import com.sqli.imputation.config.Constants;
import com.sqli.imputation.domain.*;
import com.sqli.imputation.repository.CorrespondenceRepository;
import com.sqli.imputation.service.AppImputationConverterService;
import com.sqli.imputation.service.dto.AppChargeDTO;
import com.sqli.imputation.service.dto.AppRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaultAppImputationConverterService implements AppImputationConverterService {

    private final Logger log = LoggerFactory.getLogger(DefaultAppImputationConverterService.class);

    @Autowired
    private ImputationConverterUtilService imputationConverterUtilService;
    @Autowired
    private CorrespondenceRepository correspondenceRepository;
    List<String> strings;

    @Override
    public Imputation convert(AppRequestDTO appRequestDTO, List<AppChargeDTO> appChargeDTOS) {
        strings = new ArrayList<>();
        Imputation imputation = createImputation(appRequestDTO);
        fillImputation(imputation, appChargeDTOS, appRequestDTO);
        imputationConverterUtilService.sortImputations(imputation);
        return imputation;
    }

    private Imputation createImputation(AppRequestDTO appRequestDTO) {
        ImputationType imputationType = imputationConverterUtilService.findImputationTypeByNameLike(Constants.APP_IMPUTATION_TYPE);
        return imputationConverterUtilService.createImputation(appRequestDTO.getYear(), appRequestDTO.getMonth(), imputationType);
    }

    private void fillImputation(Imputation imputation, List<AppChargeDTO> appChargeDTOS, AppRequestDTO appRequestDTO) {
        appChargeDTOS.forEach(appChargeDTO -> {
            if (isDayRequested(appChargeDTO, appRequestDTO)) {
                try {
                    Collaborator collaborator = findCollabByCorrespondence(appChargeDTO.getAppLogin());
                    CollaboratorMonthlyImputation monthlyImputation = imputationConverterUtilService.getCollabMonthlyImputation(imputation, collaborator);
                    fillCollaboratorMonthlyImputation(monthlyImputation, appChargeDTO);
                    imputationConverterUtilService.addMonthlyImputationToImputation(imputation, monthlyImputation);
                } catch (Exception e) {
                    log.error("No APP login for {}", appChargeDTO.getAppLogin());
                    strings.add(appChargeDTO.getAppLogin());
                }
            }
        });
    }

    private Collaborator findCollabByCorrespondence(String appLogin) {
        return correspondenceRepository.findByIdAPP(appLogin).stream().findFirst().get().getCollaborator();
    }

    private boolean isDayRequested(AppChargeDTO appChargeDTO, AppRequestDTO appRequestDTO) {
        return appChargeDTO.getDay() >= appRequestDTO.getStartDay() && appChargeDTO.getDay() < (appRequestDTO.getManDay() + appRequestDTO.getStartDay());
    }

    private void fillCollaboratorMonthlyImputation(CollaboratorMonthlyImputation monthlyImputation, AppChargeDTO appChargeDTO) {
        CollaboratorDailyImputation dailyImputation = imputationConverterUtilService.createDailyImputation(appChargeDTO.getDay(), appChargeDTO.getCharge(), monthlyImputation);
        monthlyImputation.getDailyImputations().add(dailyImputation);
        imputationConverterUtilService.setTotalOfMonthlyImputation(monthlyImputation, dailyImputation.getCharge());
    }

}
