package com.sqli.imputation.repository;

import com.sqli.imputation.domain.Correspondence;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Correspondence entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CorrespondenceRepository extends JpaRepository<Correspondence, Long> {

    Correspondence findByIdTBP(String id);
}
