package com.sqli.imputation.repository;

import com.sqli.imputation.domain.AppTbpIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Activity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppTbpIdentifierRepository extends JpaRepository<AppTbpIdentifier, Long> {

    AppTbpIdentifier findByAgresso(String agresso);

    AppTbpIdentifier findByIdTbp(String idTbp);
}
