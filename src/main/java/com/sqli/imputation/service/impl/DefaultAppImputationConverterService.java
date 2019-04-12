package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.*;
import com.sqli.imputation.repository.CorrespondenceRepository;
import com.sqli.imputation.service.AppImputationConverterService;
import com.sqli.imputation.service.dto.AppChargeDTO;
import com.sqli.imputation.service.dto.AppRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaultAppImputationConverterService implements AppImputationConverterService {

    private static final String APP_NAME = "APP";

    @Autowired
    private ImputationConverterUtilService imputationConverterUtilService;
    @Autowired
    private CorrespondenceRepository correspondenceRepository;

    @Override
    public Imputation convert(AppRequestDTO appRequestDTO, List<AppChargeDTO> appChargeDTOS) {
        Imputation imputation = createImputation(appRequestDTO);
        fillImputation(imputation, appChargeDTOS, appRequestDTO);
        imputationConverterUtilService.sortImputations(imputation);
        return imputation;
    }

    private Imputation createImputation(AppRequestDTO appRequestDTO) {
        ImputationType imputationType = imputationConverterUtilService.findImputationTypeByNameLike(APP_NAME);
        return imputationConverterUtilService.createImputation(appRequestDTO.getYear(), appRequestDTO.getMonth(), imputationType);
    }

    private void fillImputation(Imputation imputation, List<AppChargeDTO> appChargeDTOS, AppRequestDTO appRequestDTO) {
        appChargeDTOS.forEach(appChargeDTO -> {
            if (isDayRequested(appChargeDTO, appRequestDTO)) {
                Collaborator collaborator = findCollabByCorrespondence(appChargeDTO.getAppLogin());
                CollaboratorMonthlyImputation monthlyImputation = imputationConverterUtilService.getCollabMonthlyImputation(imputation, collaborator);
                fillCollaboratorMonthlyImputation(monthlyImputation, appChargeDTO);
                imputationConverterUtilService.addMonthlyImputationToImputation(imputation, monthlyImputation);
            }
        });
    }

    private Collaborator findCollabByCorrespondence(String appLogin) {
        Correspondence correspondence = correspondenceRepository.findByIdAPP(appLogin);
        Collaborator collaborator = correspondence.getCollaborator();
        return collaborator;
    }

    private boolean isDayRequested(AppChargeDTO appChargeDTO, AppRequestDTO appRequestDTO) {
        return appChargeDTO.getDay() >= appRequestDTO.getStartDay() && appChargeDTO.getDay() < (appRequestDTO.getManDay() + appRequestDTO.getStartDay());
    }

    private void fillCollaboratorMonthlyImputation(CollaboratorMonthlyImputation monthlyImputation, AppChargeDTO appChargeDTO) {
        CollaboratorDailyImputation dailyImputation = imputationConverterUtilService.createDailyImputation(appChargeDTO.getDay(), appChargeDTO.getCharge(), monthlyImputation);
        monthlyImputation.getDailyImputations().add(dailyImputation);
        imputationConverterUtilService.setTotalImputationOfCollab(monthlyImputation, dailyImputation.getCharge());
    }

}
