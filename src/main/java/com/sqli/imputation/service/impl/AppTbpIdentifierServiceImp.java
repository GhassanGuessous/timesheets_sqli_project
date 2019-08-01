package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.AppTbpIdentifier;
import com.sqli.imputation.repository.AppTbpIdentifierRepository;
import com.sqli.imputation.service.AppTbpIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppTbpIdentifierServiceImp implements AppTbpIdentifierService {

    @Autowired
    private AppTbpIdentifierRepository appTbpIdentifierRepository;

    @Override
    public AppTbpIdentifier save(AppTbpIdentifier appTbpIdentifier) {
        return appTbpIdentifierRepository.save(appTbpIdentifier);
    }

    @Override
    public AppTbpIdentifier findByAgresso(String agresso) {
        return appTbpIdentifierRepository.findByAgresso(agresso);
    }

    @Override
    public AppTbpIdentifier findByIdTbp(String idTbp) {
        return appTbpIdentifierRepository.findByIdTbp(idTbp);
    }
}
