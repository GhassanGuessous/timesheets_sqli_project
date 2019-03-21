package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.CollaboratorDailyImputationService;
import com.sqli.imputation.domain.CollaboratorDailyImputation;
import com.sqli.imputation.repository.CollaboratorDailyImputationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing CollaboratorDailyImputation.
 */
@Service
@Transactional
public class CollaboratorDailyImputationServiceImpl implements CollaboratorDailyImputationService {

    private final Logger log = LoggerFactory.getLogger(CollaboratorDailyImputationServiceImpl.class);

    private final CollaboratorDailyImputationRepository collaboratorDailyImputationRepository;

    public CollaboratorDailyImputationServiceImpl(CollaboratorDailyImputationRepository collaboratorDailyImputationRepository) {
        this.collaboratorDailyImputationRepository = collaboratorDailyImputationRepository;
    }

    /**
     * Save a collaboratorDailyImputation.
     *
     * @param collaboratorDailyImputation the entity to save
     * @return the persisted entity
     */
    @Override
    public CollaboratorDailyImputation save(CollaboratorDailyImputation collaboratorDailyImputation) {
        log.debug("Request to save CollaboratorDailyImputation : {}", collaboratorDailyImputation);
        return collaboratorDailyImputationRepository.save(collaboratorDailyImputation);
    }

    /**
     * Get all the collaboratorDailyImputations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CollaboratorDailyImputation> findAll(Pageable pageable) {
        log.debug("Request to get all CollaboratorDailyImputations");
        return collaboratorDailyImputationRepository.findAll(pageable);
    }


    /**
     * Get one collaboratorDailyImputation by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CollaboratorDailyImputation> findOne(Long id) {
        log.debug("Request to get CollaboratorDailyImputation : {}", id);
        return collaboratorDailyImputationRepository.findById(id);
    }

    /**
     * Delete the collaboratorDailyImputation by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete CollaboratorDailyImputation : {}", id);
        collaboratorDailyImputationRepository.deleteById(id);
    }
}
