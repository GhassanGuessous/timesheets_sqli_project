package com.sqli.imputation.repository;

import com.sqli.imputation.domain.Collaborator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Collaborator entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    @Query(value = "SELECT c FROM Collaborator c left  join c.team team left join c.activity activity" +
        " where c.firstname like %:key% or c.lastname like %:key% or c.email like %:key% " +
        "or team.name like %:key% or activity.name like %:key%")
    Page<Collaborator> findByKey(@Param("key") String key, Pageable pageable);

    @Query(value = "select c from Collaborator c where c.id not in (select co.collaborator.id from Correspondence co)")
    List<Collaborator> findAllWithNoCorrespondence();
}
