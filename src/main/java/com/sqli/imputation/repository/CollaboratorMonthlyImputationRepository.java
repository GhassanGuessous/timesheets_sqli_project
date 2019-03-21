package com.sqli.imputation.repository;

import com.sqli.imputation.domain.CollaboratorMonthlyImputation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the CollaboratorMonthlyImputation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CollaboratorMonthlyImputationRepository extends JpaRepository<CollaboratorMonthlyImputation, Long> {

}
