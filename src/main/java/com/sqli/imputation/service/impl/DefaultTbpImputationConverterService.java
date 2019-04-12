package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.*;
import com.sqli.imputation.repository.CorrespondenceRepository;
import com.sqli.imputation.service.TbpImputationConverterService;
import com.sqli.imputation.service.dto.ChargeCollaboratorDTO;
import com.sqli.imputation.service.dto.ChargeTeamDTO;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaultTbpImputationConverterService implements TbpImputationConverterService {


    public static final String TBP_TYPE_NAME = "tbp";

    @Autowired
    private ImputationConverterUtilService imputationConverterUtilService;
    @Autowired
    private CorrespondenceRepository correspondenceRepository;

    @Override
    public Imputation convert(List<ChargeTeamDTO> chargeTeamDTOS, TbpRequestBodyDTO tbpRequestBodyDTO) {
        Imputation imputation = createImputation(tbpRequestBodyDTO);
        chargeTeamDTOS.forEach(chargeTeamDTO -> fillMonthlyImputationForFachCollab(imputation, chargeTeamDTO));
        imputationConverterUtilService.sortImputations(imputation);
        return imputation;
    }

    private void fillMonthlyImputationForFachCollab(Imputation imputation, ChargeTeamDTO chargeTeamDTO) {
        chargeTeamDTO.getCollaborateurs().forEach(collaborateurDTO -> {
            Collaborator collaborator = findCollabByCorrespondence(collaborateurDTO);

            CollaboratorMonthlyImputation monthlyImputation = imputationConverterUtilService.getCollabMonthlyImputation(imputation, collaborator);
            CollaboratorDailyImputation dailyImputation = createCollaboratorDailyImputation(chargeTeamDTO, collaborateurDTO, monthlyImputation);

            imputationConverterUtilService.addDailyToMonthlyImputation(monthlyImputation, dailyImputation);
            imputationConverterUtilService.setTotalImputationOfCollab(monthlyImputation, Double.parseDouble(collaborateurDTO.getCharge()));
            imputationConverterUtilService.addMonthlyImputationToImputation(imputation, monthlyImputation);
        });
    }

    private Collaborator findCollabByCorrespondence(ChargeCollaboratorDTO collaborateurDTO) {
        Correspondence correspondence = correspondenceRepository.findByIdTBP(collaborateurDTO.getId());
        Collaborator collaborator = correspondence.getCollaborator();
        return collaborator;
    }

    private Imputation createImputation(TbpRequestBodyDTO tbpRequestBodyDTO) {
        return imputationConverterUtilService.createImputation(DateUtil.getYear(tbpRequestBodyDTO.getStartDate()),
            DateUtil.getMonth(tbpRequestBodyDTO.getStartDate()), imputationConverterUtilService.findImputationTypeByNameLike(TBP_TYPE_NAME));
    }

    private CollaboratorDailyImputation createCollaboratorDailyImputation(
        ChargeTeamDTO chargeTeamDTO, ChargeCollaboratorDTO collaborateurDTO, CollaboratorMonthlyImputation monthlyImputation) {
        return imputationConverterUtilService.createDailyImputation(DateUtil.getDay(chargeTeamDTO.getDate()), Double.parseDouble(collaborateurDTO.getCharge()), monthlyImputation);
    }

}
