package com.sqli.imputation.service;

import com.sqli.imputation.domain.Collaborator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Collaborator.
 */
public interface CollaboratorService {

    /**
     * Save a collaborator.
     *
     * @param collaborator the entity to save
     * @return the persisted entity
     */
    Collaborator save(Collaborator collaborator);

    /**
     * Get all the collaborators.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Collaborator> findAll(Pageable pageable);


    /**
     * Get the "id" collaborator.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Collaborator> findOne(Long id);

    /**
     * Delete the "id" collaborator.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Get the collaborators with a key.
     *
     * @param key the key to base searching on
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Collaborator> findByKey(String key, Pageable pageable);
}
