package com.sqli.imputation.repository;

import com.sqli.imputation.domain.CollaboratorMonthlyImputation;
import com.sqli.imputation.domain.Imputation;
import com.sqli.imputation.service.dto.ImputationRequestDTO;
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
        "SELECT m from CollaboratorMonthlyImputation m, Collaborator c, AppTbpIdentifier i " +
            "WHERE m.collaborator.id = c.id " +
            "AND c.team.id = i.team.id " +
            "AND i.agresso LIKE :#{#imputationRequestDTO.agresso} " +
            "AND m.imputation.imputationType.name LIKE :#{#imputationRequestDTO.type} " +
            "AND m.imputation.year = :#{#imputationRequestDTO.year} " +
            "AND m.imputation.month = :#{#imputationRequestDTO.month} " +
            "ORDER BY m.total ASC"
    )
    Set<CollaboratorMonthlyImputation> findByImputationAndTeam(@Param("imputationRequestDTO") ImputationRequestDTO imputationRequestDTO);

    @Query(value =
        "SELECT m from CollaboratorMonthlyImputation m, Collaborator c, AppTbpIdentifier i " +
            "WHERE m.collaborator.id = c.id " +
            "AND c.team.id = i.team.id " +
            "AND i.idTbp LIKE :#{#imputationRequestDTO.agresso} " +
            "AND m.imputation.imputationType.name LIKE :#{#imputationRequestDTO.type} " +
            "AND m.imputation.year = :#{#imputationRequestDTO.year} " +
            "AND m.imputation.month = :#{#imputationRequestDTO.month} " +
            "ORDER BY m.total ASC"
    )
    Set<CollaboratorMonthlyImputation> findByImputationAndTeamTbp(@Param("imputationRequestDTO") ImputationRequestDTO imputationRequestDTO);

    @Query(value =
        "SELECT m from CollaboratorMonthlyImputation m " +
            "WHERE m.imputation.imputationType.name LIKE :#{#imputation.imputationType.name} " +
            "AND m.imputation.year = :#{#imputation.year} " +
            "AND m.imputation.month = :#{#imputation.month}"
    )
    Set<CollaboratorMonthlyImputation> findByImputationParams(@Param("imputation")Imputation imputation);
}
