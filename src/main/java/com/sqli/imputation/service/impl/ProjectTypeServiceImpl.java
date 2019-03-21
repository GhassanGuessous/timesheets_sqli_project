package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.ProjectTypeService;
import com.sqli.imputation.domain.ProjectType;
import com.sqli.imputation.repository.ProjectTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing ProjectType.
 */
@Service
@Transactional
public class ProjectTypeServiceImpl implements ProjectTypeService {

    private final Logger log = LoggerFactory.getLogger(ProjectTypeServiceImpl.class);

    private final ProjectTypeRepository projectTypeRepository;

    public ProjectTypeServiceImpl(ProjectTypeRepository projectTypeRepository) {
        this.projectTypeRepository = projectTypeRepository;
    }

    /**
     * Save a projectType.
     *
     * @param projectType the entity to save
     * @return the persisted entity
     */
    @Override
    public ProjectType save(ProjectType projectType) {
        log.debug("Request to save ProjectType : {}", projectType);
        return projectTypeRepository.save(projectType);
    }

    /**
     * Get all the projectTypes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectType> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectTypes");
        return projectTypeRepository.findAll(pageable);
    }


    /**
     * Get one projectType by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectType> findOne(Long id) {
        log.debug("Request to get ProjectType : {}", id);
        return projectTypeRepository.findById(id);
    }

    /**
     * Delete the projectType by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProjectType : {}", id);
        projectTypeRepository.deleteById(id);
    }
}
