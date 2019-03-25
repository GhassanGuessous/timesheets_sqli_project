package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.CollaboratorPopulatorService;
import com.sqli.imputation.service.CollaboratorService;
import com.sqli.imputation.service.dto.CollaboratorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultCollaboratorPopulatorService implements CollaboratorPopulatorService {

    public static final String SQLI_COM = "@sqli.com";
    public static final String SQLI_DEFAULT_EMAIL = "sqli";
    public static final String SPACE = " ";
    public static final boolean EMAIL_DUPLICATED = true;
    public static final boolean EMAIL_NOT_DUPLICATED = false;

    @Autowired
    private CollaboratorService collaboratorService;

    @Override
    public Collaborator populateDatabase(CollaboratorDTO collaboratorDTO) {
        Collaborator collaborator = clone(collaboratorDTO);
        try {
            collaborator = collaboratorService.save(collaborator);
        } catch (Exception e) {
            collaborator.setEmail(generateEmail(collaboratorDTO.getPrenom(), collaboratorDTO.getNom(), EMAIL_DUPLICATED));
            collaborator = collaboratorService.save(collaborator);
        }
        return collaborator;
    }

    private Collaborator clone(CollaboratorDTO collaboratorDTO) {
        Collaborator collaborator = new Collaborator();
        collaborator.setFirstname(collaboratorDTO.getPrenom());
        collaborator.setLastname(collaboratorDTO.getNom());
        collaborator.setEmail(generateEmail(collaboratorDTO.getPrenom(), collaboratorDTO.getNom(), EMAIL_NOT_DUPLICATED));
        return collaborator;
    }

    private String generateEmail(String firstname, String lastname, boolean isEmailDuplicated) {
        StringBuilder email = new StringBuilder();
        if (isEmailDuplicated) email.append(getFirstTwoLetters(firstname.toLowerCase()));
        else email.append(getFirstLetter(firstname.toLowerCase()));
        email.append(lastname.toLowerCase().replaceAll(SPACE, ""));
        email.append(SQLI_COM);
        return email.toString();
    }

    private String getFirstLetter(String firstname) {
        if (firstname.isEmpty())
            return SQLI_DEFAULT_EMAIL;
        return String.valueOf(firstname.charAt(0));
    }

    private String getFirstTwoLetters(String firstname) {
        if (firstname.isEmpty())
            return SQLI_DEFAULT_EMAIL;
        return firstname.substring(0, 2);

    }


}
