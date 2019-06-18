package com.sqli.imputation.service.impl;

import com.sqli.imputation.config.Constants;
import com.sqli.imputation.domain.*;
import com.sqli.imputation.repository.CorrespondenceRepository;
import com.sqli.imputation.service.TbpImputationConverterService;
import com.sqli.imputation.service.dto.ChargeCollaboratorDTO;
import com.sqli.imputation.service.dto.ChargeTeamDTO;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaultTbpImputationConverterService implements TbpImputationConverterService {

    private final Logger log = LoggerFactory.getLogger(DefaultTbpImputationConverterService.class);

    @Autowired
    private ImputationConverterUtilService imputationConverterUtilService;
    @Autowired
    private CorrespondenceRepository correspondenceRepository;
    List<String> strings;
    @Override
    public void convertChargesToImputation(List<ChargeTeamDTO> chargeTeamDTOS, Imputation imputation) {
        strings = new ArrayList<>();
        chargeTeamDTOS.forEach(chargeTeamDTO -> fillMonthlyImputationForFachCollab(imputation, chargeTeamDTO));
        imputationConverterUtilService.sortImputations(imputation);
    }

    private void fillMonthlyImputationForFachCollab(Imputation imputation, ChargeTeamDTO chargeTeamDTO) {
        chargeTeamDTO.getCollaborateurs().forEach(collaborateurDTO -> {
            try {
                Collaborator collaborator = findCollabByCorrespondence(collaborateurDTO);

                CollaboratorMonthlyImputation monthlyImputation = imputationConverterUtilService.getCollabMonthlyImputation(imputation, collaborator);
                CollaboratorDailyImputation dailyImputation = createCollaboratorDailyImputation(chargeTeamDTO, collaborateurDTO, monthlyImputation);

                imputationConverterUtilService.addDailyToMonthlyImputation(monthlyImputation, dailyImputation);
                imputationConverterUtilService.setTotalOfMonthlyImputation(monthlyImputation, Double.parseDouble(collaborateurDTO.getCharge()));
                imputationConverterUtilService.addMonthlyImputationToImputation(imputation, monthlyImputation);
            }catch (Exception e){
                log.error("NO TBP id for "+ collaborateurDTO.toString());
                strings.add(collaborateurDTO.toString());
            }
        });
    }

    private Collaborator findCollabByCorrespondence(ChargeCollaboratorDTO collaborateurDTO) {
        Correspondence correspondence = correspondenceRepository.findByIdTBP(collaborateurDTO.getId());
        Collaborator collaborator = correspondence.getCollaborator();
        return collaborator;
    }

    public Imputation createImputation(TbpRequestBodyDTO tbpRequestBodyDTO) {
        return imputationConverterUtilService.createImputation(DateUtil.getYear(tbpRequestBodyDTO.getStartDate()),
            DateUtil.getMonth(tbpRequestBodyDTO.getStartDate()), imputationConverterUtilService.findImputationTypeByNameLike(Constants.TBP_IMPUTATION_TYPE));
    }

    private CollaboratorDailyImputation createCollaboratorDailyImputation(
        ChargeTeamDTO chargeTeamDTO, ChargeCollaboratorDTO collaborateurDTO, CollaboratorMonthlyImputation monthlyImputation) {
        return imputationConverterUtilService.createDailyImputation(DateUtil.getDay(chargeTeamDTO.getDate()), Double.parseDouble(collaborateurDTO.getCharge()), monthlyImputation);
    }

}
