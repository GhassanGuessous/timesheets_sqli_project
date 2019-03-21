package com.sqli.imputation.service;

import com.sqli.imputation.domain.Project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Project.
 */
public interface ProjectService {

    /**
     * Save a project.
     *
     * @param project the entity to save
     * @return the persisted entity
     */
    Project save(Project project);

    /**
     * Get all the projects.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Project> findAll(Pageable pageable);

    /**
     * Get all the Project with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    Page<Project> findAllWithEagerRelationships(Pageable pageable);
    
    /**
     * Get the "id" project.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Project> findOne(Long id);

    /**
     * Delete the "id" project.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
