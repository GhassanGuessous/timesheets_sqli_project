package com.sqli.imputation.service;

import com.sqli.imputation.domain.Imputation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Imputation.
 */
public interface ImputationService {

    /**
     * Save a imputation.
     *
     * @param imputation the entity to save
     * @return the persisted entity
     */
    Imputation save(Imputation imputation);

    /**
     * Get all the imputations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Imputation> findAll(Pageable pageable);


    /**
     * Get the "id" imputation.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Imputation> findOne(Long id);

    /**
     * Delete the "id" imputation.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
