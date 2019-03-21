package com.sqli.imputation.repository;

import com.sqli.imputation.domain.ImputationType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ImputationType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImputationTypeRepository extends JpaRepository<ImputationType, Long> {

}
