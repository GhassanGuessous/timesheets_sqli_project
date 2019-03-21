package com.sqli.imputation.service;

import com.sqli.imputation.domain.ImputationType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing ImputationType.
 */
public interface ImputationTypeService {

    /**
     * Save a imputationType.
     *
     * @param imputationType the entity to save
     * @return the persisted entity
     */
    ImputationType save(ImputationType imputationType);

    /**
     * Get all the imputationTypes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ImputationType> findAll(Pageable pageable);


    /**
     * Get the "id" imputationType.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<ImputationType> findOne(Long id);

    /**
     * Delete the "id" imputationType.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
