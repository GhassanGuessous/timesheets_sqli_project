package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.*;
import com.sqli.imputation.repository.CorrespondenceRepository;
import com.sqli.imputation.repository.ImputationTypeRepository;
import com.sqli.imputation.service.TbpImputationConverterService;
import com.sqli.imputation.service.dto.ChargeCollaboratorDTO;
import com.sqli.imputation.service.dto.ChargeTeamDTO;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.factory.ImputationFactory;
import com.sqli.imputation.service.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.sqli.imputation.service.util.DateUtil.END;
import static com.sqli.imputation.service.util.DateUtil.getNumberMonthsBetweenDates;

@Service
public class DefaultTbpImputationConverterServiceImp implements TbpImputationConverterService {

    public static final String TBP_TYPE_NAME = "TBP";
    public static final String START = "start";
    public static final String DELIMITER = "-";
    public static final String TWO_DIGITS_FORMAT = "%02d";
    @Autowired
    ImputationFactory imputationFactory;
    @Autowired
    ImputationTypeRepository imputationTypeRepository;
    @Autowired
    CorrespondenceRepository correspondenceRepository;

    @Override
    public Imputation convert(List<ChargeTeamDTO> chargeTeamDTOS, TbpRequestBodyDTO tbpRequestBodyDTO) {
        List<TbpRequestBodyDTO> bodyDTOS = dividePeriod(tbpRequestBodyDTO);

        Imputation imputation = createImputation(tbpRequestBodyDTO);

        for (ChargeTeamDTO chargeTeamDTO : chargeTeamDTOS) {
            for (ChargeCollaboratorDTO collaborateurDTO : chargeTeamDTO.getCollaborateurs()) {
                Collaborator collaborator = findCollabByCorrespondence(collaborateurDTO);

                CollaboratorMonthlyImputation monthlyImputation = getCollaboratorMonthlyImputation(imputation, collaborator);
                CollaboratorDailyImputation dailyImputation = createCollaboratorDailyImputation(chargeTeamDTO, collaborateurDTO, monthlyImputation);

                addDailyToMonthlyImputation(monthlyImputation, dailyImputation);
                setTotalImputationOfCollab(collaborateurDTO, monthlyImputation);

                addMonthlyImputationToImputation(imputation, monthlyImputation);
            }
        }
        sortImputations(imputation);
        return imputation;
    }

    private List<TbpRequestBodyDTO> dividePeriod(TbpRequestBodyDTO tbpRequestBodyDTO) {
        List<TbpRequestBodyDTO> bodyDTOS = new ArrayList<>();
        Map<Integer, Map<String, Integer>> months;

        if(isMultipleMonths(tbpRequestBodyDTO)){
            int numberOfMonths = DateUtil.getNumberMonthsBetweenDates(tbpRequestBodyDTO.getStartDate(), tbpRequestBodyDTO.getEndDate());
            months = DateUtil.getMonths(tbpRequestBodyDTO.getStartDate(), tbpRequestBodyDTO.getEndDate(), numberOfMonths);
            bodyDTOS = getTbpRequestBodyDTOS(months, DateUtil.getYear(tbpRequestBodyDTO.getStartDate()), tbpRequestBodyDTO.getIdTbp());
        }else{
            bodyDTOS.add(tbpRequestBodyDTO);
        }
        return bodyDTOS;
    }

    public List<TbpRequestBodyDTO> getTbpRequestBodyDTOS(Map<Integer, Map<String, Integer>> months, int year, String idTbp) {
        List<TbpRequestBodyDTO> bodyDTOS = new ArrayList<>();
        months.forEach((month, startEndMap) -> {
            StringBuilder startDateBuilder = new StringBuilder(year + DELIMITER);
            StringBuilder endDateBuilder = new StringBuilder(year + DELIMITER);
            composeStartDate(month, startEndMap, startDateBuilder);
            composeEndDate(month, startEndMap, endDateBuilder);
            bodyDTOS.add(new TbpRequestBodyDTO(idTbp, startDateBuilder.toString(), endDateBuilder.toString()));
        });
        return bodyDTOS;
    }

    private void composeEndDate(Integer month, Map<String, Integer> startEndMap, StringBuilder endDateBuilder) {
        endDateBuilder.append(String.format(TWO_DIGITS_FORMAT, month) + DELIMITER);
        endDateBuilder.append(String.format(TWO_DIGITS_FORMAT, startEndMap.get(END)));
    }

    private void composeStartDate(Integer month, Map<String, Integer> startEndMap, StringBuilder startDateBuilder) {
        startDateBuilder.append(String.format(TWO_DIGITS_FORMAT, month) + DELIMITER);
        startDateBuilder.append(String.format(TWO_DIGITS_FORMAT, startEndMap.get(START)));
    }

    private boolean isMultipleMonths(TbpRequestBodyDTO tbpRequestBodyDTO) {
        return getNumberMonthsBetweenDates(tbpRequestBodyDTO.getStartDate(), tbpRequestBodyDTO.getEndDate()) != 0;
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

    private void addDailyToMonthlyImputation(CollaboratorMonthlyImputation monthlyImputation, CollaboratorDailyImputation dailyImputation) {
        monthlyImputation.getDailyImputations().add(dailyImputation);
    }

    private void addMonthlyImputationToImputation(Imputation imputation, CollaboratorMonthlyImputation monthlyImputation) {
        if(isMonthlyImputationExistForCurrentCollab(imputation, monthlyImputation.getCollaborator())){
            replaceMonthlyImputation(imputation, monthlyImputation);
        }else{
            addMonthlyImputation(imputation, monthlyImputation);
        }
    }

    private void addMonthlyImputation(Imputation imputation, CollaboratorMonthlyImputation monthlyImputation) {
        imputation.getMonthlyImputations().add(monthlyImputation);
    }

    private void replaceMonthlyImputation(Imputation imputation, CollaboratorMonthlyImputation monthlyImputation) {
        List<CollaboratorMonthlyImputation> monthlyImputationList = new ArrayList<>(imputation.getMonthlyImputations());
        int index = monthlyImputationList.indexOf(findMonthlyImputationByCollab(imputation, monthlyImputation.getCollaborator()));
        monthlyImputationList.set(index, monthlyImputation);
        imputation.setMonthlyImputations(new HashSet<>(monthlyImputationList));
    }

    private Imputation createImputation(TbpRequestBodyDTO tbpRequestBodyDTO) {
        return imputationFactory.createImputation(DateUtil.getYear(tbpRequestBodyDTO.getStartDate()),
                DateUtil.getMonth(tbpRequestBodyDTO.getStartDate()), imputationTypeRepository.findByNameLike(TBP_TYPE_NAME));
    }

    private CollaboratorDailyImputation createCollaboratorDailyImputation(
        ChargeTeamDTO chargeTeamDTO, ChargeCollaboratorDTO collaborateurDTO, CollaboratorMonthlyImputation monthlyImputation) {
        return imputationFactory.createDailyImputation(DateUtil.getDay(chargeTeamDTO.getDate()), Double.parseDouble(collaborateurDTO.getCharge()), monthlyImputation);
    }

    private CollaboratorMonthlyImputation getCollaboratorMonthlyImputation(Imputation imputation, Collaborator collaborator) {
        CollaboratorMonthlyImputation monthlyImputation = null;

        if(isMonthlyImputationExistForCurrentCollab(imputation, collaborator))
            monthlyImputation = findMonthlyImputationByCollab(imputation, collaborator);
        else{
            monthlyImputation = imputationFactory.createMonthlyImputation(imputation, collaborator);
        }
        return monthlyImputation;
    }

    private Collaborator findCollabByCorrespondence(ChargeCollaboratorDTO collaborateurDTO) {
        Correspondence correspondence = correspondenceRepository.findByIdTBP(collaborateurDTO.getId());
        Collaborator collaborator = correspondence.getCollaborator();
        return collaborator;
    }

    private void setTotalImputationOfCollab(ChargeCollaboratorDTO collaborateurDTO, CollaboratorMonthlyImputation monthlyImputation) {
        monthlyImputation.setTotal(monthlyImputation.getTotal() + Double.parseDouble(collaborateurDTO.getCharge()));
    }

    private CollaboratorMonthlyImputation findMonthlyImputationByCollab(Imputation imputation, Collaborator collaborator) {
        return imputation.getMonthlyImputations().stream().filter(
            collaboratorMonthlyImputation -> collaboratorMonthlyImputation.getCollaborator().equals(collaborator)
        ).findFirst().get();
    }

    private boolean isMonthlyImputationExistForCurrentCollab(Imputation imputation, Collaborator collaborator) {
        return imputation.getMonthlyImputations().stream()
            .anyMatch(collaboratorMonthlyImputation -> collaboratorMonthlyImputation.getCollaborator().equals(collaborator));
    }
}
