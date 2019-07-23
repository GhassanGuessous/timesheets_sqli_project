package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Activity;
import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.domain.Correspondence;
import com.sqli.imputation.repository.ActivityRepository;
import com.sqli.imputation.service.CollaboratorPopulatorService;
import com.sqli.imputation.service.CollaboratorService;
import com.sqli.imputation.service.CorrespondenceService;
import com.sqli.imputation.service.EmailGenerator;
import com.sqli.imputation.service.dto.ActivityDTO;
import com.sqli.imputation.service.dto.CollaboratorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultCollaboratorPopulatorService implements CollaboratorPopulatorService {

    public static final boolean EMAIL_DUPLICATED = true;
    public static final boolean EMAIL_NOT_DUPLICATED = false;
    private static final String AT_SYMBOL = "@";
    private static final int FIRST_POSITION = 0;

    @Autowired
    private CollaboratorService collaboratorService;
    @Autowired
    private CorrespondenceService correspondenceService;
    @Autowired
    private DefaultDbPopulatorService defaultDbPopulator;
    @Autowired
    private EmailGenerator emailGenerator;
    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public Collaborator populateDatabase(CollaboratorDTO collaboratorDTO) {
        Collaborator collaborator = clone(collaboratorDTO);
        try {
            collaborator = collaboratorService.save(collaborator);
        } catch (Exception e) {
            collaborator.setEmail(emailGenerator.generate(collaboratorDTO.getPrenom(), collaboratorDTO.getNom(), EMAIL_DUPLICATED));
            collaborator = collaboratorService.save(collaborator);
        }
        saveCorrespondenceTBP(collaborator, collaboratorDTO.getId());
        return collaborator;
    }

    private Collaborator clone(CollaboratorDTO collaboratorDTO) {
        Collaborator collaborator = new Collaborator();
        collaborator.setFirstname(collaboratorDTO.getPrenom());
        collaborator.setLastname(collaboratorDTO.getNom());
        collaborator.setEmail(emailGenerator.generate(collaboratorDTO.getPrenom(), collaboratorDTO.getNom(), EMAIL_NOT_DUPLICATED));
        collaborator.setActivity(findActivity(collaboratorDTO.getActivite()));
        return collaborator;
    }

    private void saveCorrespondenceTBP(Collaborator collaborator, String idTbp) {
        Correspondence correspondence = new Correspondence();
        correspondence.setCollaborator(collaborator);
        correspondence.setIdTBP(idTbp);
        correspondence.setIdAPP(getAPPIdFromEmail(collaborator.getEmail()));
        correspondenceService.save(correspondence);
    }

    private Activity findActivity(String id) {
        Long idDTO = Long.valueOf(Integer.parseInt(id));
        Optional<ActivityDTO> activityDTOOptional = findActivityByIdFromWs(idDTO);
        String name = activityDTOOptional.isPresent() ? activityDTOOptional.get().getLibelle() : "";
        return activityRepository.findByNameLike(name);
    }

    private Optional<ActivityDTO> findActivityByIdFromWs(Long id) {
        return defaultDbPopulator.getActivities().stream().filter(typeDTO -> typeDTO.getId() == id).findFirst();
    }

    @Override
    public String getAPPIdFromEmail(String email) {
        return email.split(AT_SYMBOL)[FIRST_POSITION];
    }
}
