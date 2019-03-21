package com.sqli.imputation.service;

import com.sqli.imputation.domain.Correspondence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Correspondence.
 */
public interface CorrespondenceService {

    /**
     * Save a correspondence.
     *
     * @param correspondence the entity to save
     * @return the persisted entity
     */
    Correspondence save(Correspondence correspondence);

    /**
     * Get all the correspondences.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Correspondence> findAll(Pageable pageable);


    /**
     * Get the "id" correspondence.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Correspondence> findOne(Long id);

    /**
     * Delete the "id" correspondence.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
