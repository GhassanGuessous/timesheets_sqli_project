package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Activity;
import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.domain.Correspondence;
import com.sqli.imputation.repository.ActivityRepository;
import com.sqli.imputation.service.CollaboratorPopulatorService;
import com.sqli.imputation.service.CollaboratorService;
import com.sqli.imputation.service.CorrespondenceService;
import com.sqli.imputation.service.dto.ActivityDTO;
import com.sqli.imputation.service.dto.CollaboratorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultCollaboratorPopulatorService implements CollaboratorPopulatorService {

    public static final String SQLI_COM = "@sqli.com";
    public static final String SQLI_DEFAULT_EMAIL = "sqli";
    public static final String SPACE = " ";
    public static final boolean EMAIL_DUPLICATED = true;
    public static final boolean EMAIL_NOT_DUPLICATED = false;

    @Autowired
    private CollaboratorService collaboratorService;
    @Autowired
    private CorrespondenceService correspondenceService;
    @Autowired
    private DefaultDbPopulator defaultDbPopulator;
    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public Collaborator populateDatabase(CollaboratorDTO collaboratorDTO) {
        Collaborator collaborator = clone(collaboratorDTO);
        try {
            collaborator = collaboratorService.save(collaborator);
        } catch (Exception e) {
            collaborator.setEmail(generateEmail(collaboratorDTO.getPrenom(), collaboratorDTO.getNom(), EMAIL_DUPLICATED));
            collaborator = collaboratorService.save(collaborator);
        }
        saveCorrespondenceTBP(collaborator, collaboratorDTO.getId());
        return collaborator;
    }

    private Collaborator clone(CollaboratorDTO collaboratorDTO) {
        Collaborator collaborator = new Collaborator();
        collaborator.setFirstname(collaboratorDTO.getPrenom());
        collaborator.setLastname(collaboratorDTO.getNom());
        collaborator.setEmail(generateEmail(collaboratorDTO.getPrenom(), collaboratorDTO.getNom(), EMAIL_NOT_DUPLICATED));
        collaborator.setActivity(findActivity(collaboratorDTO.getActivite()));
        return collaborator;
    }

    private void saveCorrespondenceTBP(Collaborator collaborator, String id_tbp) {
        Correspondence correspondence = new Correspondence();
        correspondence.setCollaborator(collaborator);
        correspondence.setIdTBP(id_tbp);
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

    private String generateEmail(String firstname, String lastname, boolean isEmailDuplicated) {
        StringBuilder email = new StringBuilder();
        if (isEmailDuplicated)
            email.append(getFirstTwoLetters(firstname.toLowerCase()));
        else
            email.append(getEmailPrefix(firstname.toLowerCase()));
        email.append(lastname.toLowerCase().replaceAll(SPACE, ""));
        email.append(SQLI_COM);
        return email.toString();
    }

    private String getFirstLetter(String firstname) {
        return String.valueOf(firstname.charAt(0));
    }

    private String getFirstTwoLetters(String firstname) {
        if (firstname.isEmpty())
            return SQLI_DEFAULT_EMAIL;
        return firstname.substring(0, 2);
    }

    private String getEmailPrefix(String firstname) {
        if (firstname.isEmpty())
            return SQLI_DEFAULT_EMAIL;
        else {
            if (isComposed(firstname)) {
                String[] tokens = firstname.split(SPACE);
                return getFirstLetterOfEach(tokens);
            }
            return getFirstLetter(firstname);
        }
    }

    private String getFirstLetterOfEach(String[] tokens) {
        StringBuilder emailPrefix = new StringBuilder();
        for (String token : tokens) {
            emailPrefix.append(getFirstLetter(token));
        }
        return emailPrefix.toString();
    }

    private boolean isComposed(String firstname) {
        return firstname.contains(SPACE);
    }

}
