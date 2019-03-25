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

    @Autowired
    private CollaboratorService collaboratorService;

    @Override
    public Collaborator populateDatabase(CollaboratorDTO collaboratorDTO) {
        Collaborator collaborator = clone(collaboratorDTO);
        collaborator = collaboratorService.save(collaborator);
        return collaborator;
    }

    private Collaborator clone(CollaboratorDTO collaboratorDTO) {
        Collaborator collaborator = new Collaborator();
        collaborator.setFirstname(collaboratorDTO.getPrenom());
        collaborator.setLastname(collaboratorDTO.getNom());
        collaborator.setEmail(generateEmail(collaboratorDTO.getPrenom(), collaboratorDTO.getNom()));
        return collaborator;
    }

    private String generateEmail(String fisrtname, String lastname){
        StringBuilder email = new StringBuilder();
        email.append(getFirstLetter(fisrtname.toLowerCase()));
        email.append(lastname.toLowerCase().trim());
        email.append(SQLI_COM);
        return email.toString();
    }

    private String getFirstLetter(String fisrtname) {
        if(fisrtname.isEmpty())
            return SQLI_DEFAULT_EMAIL;
        return String.valueOf(fisrtname.charAt(0));
    }


}
