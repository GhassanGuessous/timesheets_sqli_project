package com.sqli.imputation.service;

import com.sqli.imputation.domain.AppTbpIdentifier;

/**
 * Service Interface for managing Activity.
 */
public interface AppTbpIdentifierService {

    AppTbpIdentifier save(AppTbpIdentifier appTbpIdentifier);

    AppTbpIdentifier findByAgresso(String agresso);

    AppTbpIdentifier findByIdTbp(String idTbp);
}
