package com.sqli.imputation.repository;

import com.sqli.imputation.domain.CollaboratorDailyImputation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the CollaboratorDailyImputation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CollaboratorDailyImputationRepository extends JpaRepository<CollaboratorDailyImputation, Long> {

}
