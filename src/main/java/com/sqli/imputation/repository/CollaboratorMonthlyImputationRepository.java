package com.sqli.imputation.repository;

import com.sqli.imputation.domain.CollaboratorMonthlyImputation;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;


/**
 * Spring Data  repository for the CollaboratorMonthlyImputation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CollaboratorMonthlyImputationRepository extends JpaRepository<CollaboratorMonthlyImputation, Long> {

    @Query(value =
        "SELECT m from CollaboratorMonthlyImputation m, Collaborator c " +
            "WHERE m.collaborator.id = c.id " +
            "AND m.collaborator.team.agresso LIKE :agresso " +
            "AND m.imputation.imputationType.name LIKE :type " +
            "AND m.imputation.year = :year " +
            "AND m.imputation.month = :month " +
            "ORDER BY m.total ASC"
    )
    Set<CollaboratorMonthlyImputation> findByImputationAndTeam(@Param("agresso") String agresso, @Param("month") int month, @Param("year") int year, @Param("type") String imputationType);

    @Query(value =
        "SELECT m from CollaboratorMonthlyImputation m " +
            "WHERE m.imputation.imputationType.name LIKE :type " +
            "AND m.imputation.year = :year " +
            "AND m.imputation.month = :month"
    )
    Set<CollaboratorMonthlyImputation> findByImputationParams( @Param("month") int month, @Param("year") int year, @Param("type") String imputationType);
}
