package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.*;
import com.sqli.imputation.repository.ImputationTypeRepository;
import com.sqli.imputation.service.dto.ImputationComparatorAdvancedDTO;
import com.sqli.imputation.service.dto.ImputationComparatorDTO;
import com.sqli.imputation.service.factory.ImputationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImputationConverterUtilService {

    @Autowired
    private ImputationFactory imputationFactory;
    @Autowired
    private ImputationTypeRepository imputationTypeRepository;

    public CollaboratorMonthlyImputation getCollabMonthlyImputation(Imputation imputation, Collaborator collaborator) {
        if (isMonthlyImputationExistForCurrentCollab(imputation.getMonthlyImputations(), collaborator)) {
            return findMonthlyImputationByCollab(imputation.getMonthlyImputations(), collaborator);
        }
        return createMonthlyImputation(imputation, collaborator);
    }

    public void addDailyToMonthlyImputation(CollaboratorMonthlyImputation monthlyImputation, CollaboratorDailyImputation dailyImputation) {
        monthlyImputation.getDailyImputations().add(dailyImputation);
    }

    public void setTotalOfMonthlyImputation(CollaboratorMonthlyImputation monthlyImputation, Double charge) {
        monthlyImputation.setTotal(monthlyImputation.getTotal() + charge);
    }

    public boolean isMonthlyImputationExistForCurrentCollab(Set<CollaboratorMonthlyImputation> monthlyImputations, Collaborator collaborator) {
        return monthlyImputations.stream()
            .anyMatch(collaboratorMonthlyImputation -> collaboratorMonthlyImputation.getCollaborator().equals(collaborator));
    }

    public CollaboratorMonthlyImputation findMonthlyImputationByCollab(Set<CollaboratorMonthlyImputation> monthlyImputations, Collaborator collaborator) {
        return monthlyImputations.stream().filter(
            collaboratorMonthlyImputation -> collaboratorMonthlyImputation.getCollaborator().equals(collaborator)
        ).findFirst().get();
    }

    public boolean isDailyImputationExist(Set<CollaboratorDailyImputation> dailyImputations, int day) {
        return dailyImputations.stream()
            .anyMatch(dailyImputation -> dailyImputation.getDay().equals(day));
    }

    public CollaboratorDailyImputation findDailyImputationByDay(Set<CollaboratorDailyImputation> dailyImputations, int day) {
        return dailyImputations.stream().filter(
            collaboratorMonthlyImputation -> collaboratorMonthlyImputation.getDay().equals(day)
        ).findFirst().get();
    }

    public void addMonthlyImputationToImputation(Imputation imputation, CollaboratorMonthlyImputation monthlyImputation) {
        if (isMonthlyImputationExistForCurrentCollab(imputation.getMonthlyImputations(), monthlyImputation.getCollaborator())) {
            replaceMonthlyImputation(imputation, monthlyImputation);
        } else {
            addMonthlyImputation(imputation, monthlyImputation);
        }
    }

    private void addMonthlyImputation(Imputation imputation, CollaboratorMonthlyImputation monthlyImputation) {
        imputation.getMonthlyImputations().add(monthlyImputation);
    }

    private void replaceMonthlyImputation(Imputation imputation, CollaboratorMonthlyImputation monthlyImputation) {
        List<CollaboratorMonthlyImputation> monthlyImputationList = new ArrayList<>(imputation.getMonthlyImputations());
        int index = monthlyImputationList.indexOf(findMonthlyImputationByCollab(imputation.getMonthlyImputations(), monthlyImputation.getCollaborator()));
        monthlyImputationList.set(index, monthlyImputation);
        imputation.setMonthlyImputations(new HashSet<>(monthlyImputationList));
    }

    public void replaceDailyImputation(CollaboratorMonthlyImputation monthlyImputation, CollaboratorDailyImputation dailyImputation) {
        List<CollaboratorDailyImputation> dailyImputations = new ArrayList<>(monthlyImputation.getDailyImputations());
        int index = dailyImputations.indexOf(findDailyImputationByDay(monthlyImputation.getDailyImputations(), dailyImputation.getDay()));
        dailyImputations.set(index, dailyImputation);
        monthlyImputation.setDailyImputations(new HashSet<>(dailyImputations));
    }

    public void sortImputations(Imputation imputation) {
        imputation.setMonthlyImputations(sortMonthlyImputations(imputation.getMonthlyImputations()));
        imputation.getMonthlyImputations().forEach(
            collaboratorMonthlyImputation -> collaboratorMonthlyImputation.setDailyImputations(sortDailyImputations(collaboratorMonthlyImputation.getDailyImputations()))
        );
    }

    private Set<CollaboratorMonthlyImputation> sortMonthlyImputations(Set<CollaboratorMonthlyImputation> monthlyImputations) {
        return monthlyImputations.stream().sorted(Comparator.comparing(CollaboratorMonthlyImputation::getTotal)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<CollaboratorDailyImputation> sortDailyImputations(Set<CollaboratorDailyImputation> dailyImputations) {
        return dailyImputations.stream().sorted(Comparator.comparing(CollaboratorDailyImputation::getDay)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Imputation createImputation(int year, int month, ImputationType imputationType) {
        return imputationFactory.createImputation(year, month, imputationType);
    }

    public CollaboratorMonthlyImputation createMonthlyImputation(Imputation imputation, Collaborator collaborator) {
        return imputationFactory.createMonthlyImputation(imputation, collaborator);
    }

    public CollaboratorDailyImputation createDailyImputation(int day, double charge, CollaboratorMonthlyImputation monthlyImputation) {
        return imputationFactory.createDailyImputation(day, charge, monthlyImputation);
    }

    public ImputationType findImputationTypeByNameLike(String name) {
        return imputationTypeRepository.findByNameLike(name);
    }

    public List<ImputationComparatorDTO> compareImputations(Imputation appImputation, Imputation comparedImputation) {
        List<ImputationComparatorDTO> comparatorDTOS = new ArrayList<>();
        fillDTOsWithAppImputation(comparatorDTOS, appImputation);
        fillDTOsWithComparedImputation(comparatorDTOS, comparedImputation);
        setDifference(comparatorDTOS);
        return comparatorDTOS;
    }

    private void fillDTOsWithComparedImputation(List<ImputationComparatorDTO> comparatorDTOS, Imputation comparedImputation) {
        comparedImputation.getMonthlyImputations().forEach(monthly -> {
            if(isComparatorExistWithTheSameCollab(comparatorDTOS, monthly.getCollaborator())){
                setComparedTotal(comparatorDTOS, monthly);
            }else{
                comparatorDTOS.add(new ImputationComparatorDTO(monthly.getCollaborator(), 0D, monthly.getTotal()));
            }
        });
    }

    private void fillDTOsWithAppImputation(List<ImputationComparatorDTO> comparatorDTOS, Imputation appImputation) {
        appImputation.getMonthlyImputations().forEach(
            monthly -> comparatorDTOS.add(new ImputationComparatorDTO(monthly.getCollaborator(), monthly.getTotal(), 0D))
        );
    }

    private void setDifference(List<ImputationComparatorDTO> comparatorDTOS) {
        comparatorDTOS.forEach(dto -> dto.setDifference(dto.getTotalApp() - dto.getTotalCompared()));
    }

    private void setComparedTotal(List<ImputationComparatorDTO> comparatorDTOS, CollaboratorMonthlyImputation monthly) {
        comparatorDTOS.stream().filter(
            dto -> dto.getCollaborator().getId().equals(monthly.getCollaborator().getId())
        ).findFirst().get().setTotalCompared(monthly.getTotal());
    }

    private boolean isComparatorExistWithTheSameCollab(List<ImputationComparatorDTO> comparatorDTOS, Collaborator collaborator) {
        return comparatorDTOS.stream().anyMatch(dto -> dto.getCollaborator().getId().equals(collaborator.getId()));
    }

    public List<ImputationComparatorAdvancedDTO> compareImputationsAdvanced(Imputation appImputation, Imputation comparedImputation) {
        List<ImputationComparatorAdvancedDTO> comparatorDTOS = new ArrayList<>();
        fillAdvancedDTOsWithAppImputation(comparatorDTOS, appImputation);
        fillAdvancedDTOsWithComparedImputation(comparatorDTOS, comparedImputation);
        return comparatorDTOS;
    }

    private void fillAdvancedDTOsWithComparedImputation(List<ImputationComparatorAdvancedDTO> comparatorDTOS, Imputation comparedImputation) {
        comparedImputation.getMonthlyImputations().forEach(monthly -> {
            if(isAdvancedComparatorExistWithTheSameCollab(comparatorDTOS, monthly.getCollaborator())){
                setComparedList(comparatorDTOS, monthly);
            }else{
                comparatorDTOS.add(new ImputationComparatorAdvancedDTO(monthly.getCollaborator(), new CollaboratorMonthlyImputation(), monthly));
            }
        });
    }

    private void fillAdvancedDTOsWithAppImputation(List<ImputationComparatorAdvancedDTO> comparatorDTOS, Imputation appImputation) {
        appImputation.getMonthlyImputations().forEach(
            monthly -> comparatorDTOS.add(new ImputationComparatorAdvancedDTO(monthly.getCollaborator(), monthly, new CollaboratorMonthlyImputation()))
        );
    }

    private boolean isAdvancedComparatorExistWithTheSameCollab(List<ImputationComparatorAdvancedDTO> comparatorDTOS, Collaborator collaborator) {
        return comparatorDTOS.stream().anyMatch(dto -> dto.getCollaborator().getId().equals(collaborator.getId()));
    }

    private void setComparedList(List<ImputationComparatorAdvancedDTO> comparatorDTOS, CollaboratorMonthlyImputation monthly) {
        comparatorDTOS.stream().filter(
            dto -> dto.getCollaborator().getId().equals(monthly.getCollaborator().getId())
        ).findFirst().get().setComparedMonthlyImputation(monthly);
    }
}
