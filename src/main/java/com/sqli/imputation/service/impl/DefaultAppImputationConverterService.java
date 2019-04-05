package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.*;
import com.sqli.imputation.repository.CorrespondenceRepository;
import com.sqli.imputation.repository.ImputationTypeRepository;
import com.sqli.imputation.service.AppImputationConverterService;
import com.sqli.imputation.service.dto.AppChargeDTO;
import com.sqli.imputation.service.dto.AppRequestDTO;
import com.sqli.imputation.service.factory.ImputationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DefaultAppImputationConverterService implements AppImputationConverterService {

    public static final String DATE_SEPARATOR = "-";
    public static final int MONTH_POSITION = 1;
    public static final int YEAR_POSITION = 0;
    public static final String APP_NAME = "APP";
    @Autowired
    private ImputationTypeRepository imputationTypeRepository;
    @Autowired
    private CorrespondenceRepository correspondenceRepository;
    @Autowired
    private ImputationFactory imputationFactory;

    @Override
    public Imputation convert(AppRequestDTO appRequestDTO, List<AppChargeDTO> appChargeDTOS) {
        Imputation imputation = createImputation(appRequestDTO);
        fillImputation(imputation, appChargeDTOS);
        sortImputations(imputation);
        return imputation;
    }

    private Imputation createImputation(AppRequestDTO appRequestDTO) {
        ImputationType imputationType = imputationTypeRepository.findByNameLike(APP_NAME);
        int year = getYearFromDateRequest(appRequestDTO.getStartDate());
        int month = getMonthFromDateRequest(appRequestDTO.getStartDate());
        return imputationFactory.createImputation(year, month, imputationType);
    }

    private void fillImputation(Imputation imputation, List<AppChargeDTO> appChargeDTOS) {
        appChargeDTOS.forEach(appChargeDTO -> {
            CollaboratorMonthlyImputation monthlyImputation = getCollabMonthlyImputation(appChargeDTO.getAppLogin(), imputation);
            fillCollaboratorMonthlyImputation(monthlyImputation, appChargeDTO);
            addMontlyToImputation(imputation, monthlyImputation);
        });
    }

    private void fillCollaboratorMonthlyImputation(CollaboratorMonthlyImputation monthlyImputation, AppChargeDTO appChargeDTO) {
        //TODO REPLACE INT TO DOUBLE VALUE
        CollaboratorDailyImputation dailyImputation = imputationFactory.createDailyImputation(monthlyImputation, appChargeDTO.getDay(), appChargeDTO.getCharge().intValue());
        monthlyImputation.getDailyImputations().add(dailyImputation);
        monthlyImputation.setTotal(monthlyImputation.getTotal() + dailyImputation.getCharge());

    }

    private CollaboratorMonthlyImputation getCollabMonthlyImputation(String appLogin, Imputation imputation) {
        Collaborator collaborator = correspondenceRepository.findByIdAPP(appLogin).getCollaborator();
        if (isMontlyImputationExist(imputation, collaborator)) {
            return getMonthlyImputationOfCollab(imputation, collaborator);
        }
        return createNewMonthlyImputation(imputation, collaborator);
    }

    private void addMontlyToImputation(Imputation imputation, CollaboratorMonthlyImputation monthlyImputation) {
        if (isMontlyImputationExist(imputation, monthlyImputation.getCollaborator())) {
            replaceMonthlyImputation(imputation, monthlyImputation);
        } else {
            imputation.getMonthlyImputations().add(monthlyImputation);
        }
    }

    private void replaceMonthlyImputation(Imputation imputation, CollaboratorMonthlyImputation monthlyImputation) {
        List<CollaboratorMonthlyImputation> monthlyImputations = new ArrayList<>(imputation.getMonthlyImputations());
        monthlyImputations.set(monthlyImputations.indexOf(monthlyImputation), getMonthlyImputationOfCollab(imputation, monthlyImputation.getCollaborator()));
        imputation.setMonthlyImputations(new HashSet<>(monthlyImputations));
    }

    private CollaboratorMonthlyImputation getMonthlyImputationOfCollab(Imputation imputation, Collaborator collaborator) {
        return imputation.getMonthlyImputations().stream()
            .filter(monthlyImputation -> monthlyImputation.getCollaborator().equals(collaborator)).collect(Collectors.toList()).get(0);
    }

    private void sortImputations(Imputation imputation) {
        imputation.setMonthlyImputations(sortMonthlyImputations(imputation.getMonthlyImputations()));
        imputation.getMonthlyImputations().forEach(collaboratorMonthlyImputation -> {
            collaboratorMonthlyImputation.setDailyImputations(sortDailyImputations(collaboratorMonthlyImputation.getDailyImputations()));
        });
    }

    private Set<CollaboratorMonthlyImputation> sortMonthlyImputations(Set<CollaboratorMonthlyImputation> monthlyImputations) {
        return monthlyImputations.stream().sorted(Comparator.comparing(CollaboratorMonthlyImputation::getTotal)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<CollaboratorDailyImputation> sortDailyImputations(Set<CollaboratorDailyImputation> dailyImputations) {
        return dailyImputations.stream().sorted(Comparator.comparing(CollaboratorDailyImputation::getDay)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean isMontlyImputationExist(Imputation imputation, Collaborator collaborator) {
        return imputation.getMonthlyImputations().stream().anyMatch(item -> item.getCollaborator().equals(collaborator));
    }

    private CollaboratorMonthlyImputation createNewMonthlyImputation(Imputation imputation, Collaborator collaborator) {
        return imputationFactory.createMonthlyImputation(imputation, collaborator);
    }

    private int getYearFromDateRequest(String date) {
        String[] splitDate = splitDate(date);
        return Integer.parseInt(splitDate[YEAR_POSITION]);
    }

    private int getMonthFromDateRequest(String date) {
        String[] splitDate = splitDate(date);
        return Integer.parseInt(splitDate[MONTH_POSITION]);
    }

    private String[] splitDate(String date) {
        return date.split(DATE_SEPARATOR);
    }
}
