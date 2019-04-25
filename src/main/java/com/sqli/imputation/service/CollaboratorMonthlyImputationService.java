package com.sqli.imputation.service;

import com.sqli.imputation.domain.CollaboratorMonthlyImputation;

import com.sqli.imputation.domain.Imputation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing CollaboratorMonthlyImputation.
 */
public interface CollaboratorMonthlyImputationService {

    /**
     * Save a collaboratorMonthlyImputation.
     *
     * @param collaboratorMonthlyImputation the entity to save
     * @return the persisted entity
     */
    CollaboratorMonthlyImputation save(CollaboratorMonthlyImputation collaboratorMonthlyImputation);

    /**
     * Get all the collaboratorMonthlyImputations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CollaboratorMonthlyImputation> findAll(Pageable pageable);


    /**
     * Get the "id" collaboratorMonthlyImputation.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<CollaboratorMonthlyImputation> findOne(Long id);

    /**
     * Delete the "id" collaboratorMonthlyImputation.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * save all the imputations.
     *
     * @param imputation the imputation that contains the list to save
     */
    void saveAll(Imputation imputation);
}
