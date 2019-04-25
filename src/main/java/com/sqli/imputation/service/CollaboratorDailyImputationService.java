package com.sqli.imputation.service;

import com.sqli.imputation.domain.CollaboratorDailyImputation;

import com.sqli.imputation.domain.CollaboratorMonthlyImputation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing CollaboratorDailyImputation.
 */
public interface CollaboratorDailyImputationService {

    /**
     * Save a collaboratorDailyImputation.
     *
     * @param collaboratorDailyImputation the entity to save
     * @return the persisted entity
     */
    CollaboratorDailyImputation save(CollaboratorDailyImputation collaboratorDailyImputation);

    /**
     * Get all the collaboratorDailyImputations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CollaboratorDailyImputation> findAll(Pageable pageable);


    /**
     * Get the "id" collaboratorDailyImputation.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<CollaboratorDailyImputation> findOne(Long id);

    /**
     * Delete the "id" collaboratorDailyImputation.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * save all the imputations.
     *
     * @param monthlyImputation the imputation that contains the list to save
     */
    void saveAll(CollaboratorMonthlyImputation monthlyImputation);
}
