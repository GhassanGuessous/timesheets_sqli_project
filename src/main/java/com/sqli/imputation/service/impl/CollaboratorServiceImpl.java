package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.CollaboratorService;
import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.repository.CollaboratorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing Collaborator.
 */
@Service
@Transactional
public class CollaboratorServiceImpl implements CollaboratorService {

    private final Logger log = LoggerFactory.getLogger(CollaboratorServiceImpl.class);

    private final CollaboratorRepository collaboratorRepository;

    public CollaboratorServiceImpl(CollaboratorRepository collaboratorRepository) {
        this.collaboratorRepository = collaboratorRepository;
    }

    /**
     * Save a collaborator.
     *
     * @param collaborator the entity to save
     * @return the persisted entity
     */
    @Override
    public Collaborator save(Collaborator collaborator) {
        log.debug("Request to save Collaborator : {}", collaborator);
        return collaboratorRepository.save(collaborator);
    }

    /**
     * Get all the collaborators.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Collaborator> findAll(Pageable pageable) {
        log.debug("Request to get all Collaborators");
        return collaboratorRepository.findAll(pageable);
    }


    /**
     * Get one collaborator by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Collaborator> findOne(Long id) {
        log.debug("Request to get Collaborator : {}", id);
        return collaboratorRepository.findById(id);
    }

    /**
     * Delete the collaborator by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Collaborator : {}", id);
        collaboratorRepository.deleteById(id);
    }
}
