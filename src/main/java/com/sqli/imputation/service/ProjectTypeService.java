package com.sqli.imputation.service;

import com.sqli.imputation.domain.ProjectType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing ProjectType.
 */
public interface ProjectTypeService {

    /**
     * Save a projectType.
     *
     * @param projectType the entity to save
     * @return the persisted entity
     */
    ProjectType save(ProjectType projectType);

    /**
     * Get all the projectTypes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ProjectType> findAll(Pageable pageable);


    /**
     * Get the "id" projectType.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<ProjectType> findOne(Long id);

    /**
     * Delete the "id" projectType.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
