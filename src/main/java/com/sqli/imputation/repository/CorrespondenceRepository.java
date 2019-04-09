package com.sqli.imputation.repository;

import com.sqli.imputation.domain.Correspondence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Correspondence entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CorrespondenceRepository extends JpaRepository<Correspondence, Long> {

    Correspondence findByIdTBP(String id);

    Correspondence findByIdAPP(String appLogin);

    @Query(value = "select c from Correspondence c left join c.collaborator collab " +
        "where c.idAPP like %:key% or c.idPPMC like %:key% or c.idTBP like %:key%" +
        " or collab.firstname like %:key% or collab.lastname like %:key%")
    Page<Correspondence> findBykey(@Param("key") String key, Pageable pageable);
}
