package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Imputation;
import com.sqli.imputation.service.CollaboratorDailyImputationService;
import com.sqli.imputation.service.CollaboratorMonthlyImputationService;
import com.sqli.imputation.domain.CollaboratorMonthlyImputation;
import com.sqli.imputation.repository.CollaboratorMonthlyImputationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Service Implementation for managing CollaboratorMonthlyImputation.
 */
@Service
@Transactional
public class CollaboratorMonthlyImputationServiceImpl implements CollaboratorMonthlyImputationService {

    private final Logger log = LoggerFactory.getLogger(CollaboratorMonthlyImputationServiceImpl.class);

    private final CollaboratorMonthlyImputationRepository collaboratorMonthlyImputationRepository;
    @Autowired
    private CollaboratorDailyImputationService dailyImputationService;
    @Autowired
    private ImputationConverterUtilService utilService;

    public CollaboratorMonthlyImputationServiceImpl(CollaboratorMonthlyImputationRepository collaboratorMonthlyImputationRepository) {
        this.collaboratorMonthlyImputationRepository = collaboratorMonthlyImputationRepository;
    }

    /**
     * Save a collaboratorMonthlyImputation.
     *
     * @param collaboratorMonthlyImputation the entity to save
     * @return the persisted entity
     */
    @Override
    public CollaboratorMonthlyImputation save(CollaboratorMonthlyImputation collaboratorMonthlyImputation) {
        log.debug("Request to save CollaboratorMonthlyImputation : {}", collaboratorMonthlyImputation);
        CollaboratorMonthlyImputation monthlyImputation = collaboratorMonthlyImputationRepository.save(collaboratorMonthlyImputation);
        dailyImputationService.saveAll(monthlyImputation);
        return monthlyImputation;
    }

    /**
     * Get all the collaboratorMonthlyImputations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CollaboratorMonthlyImputation> findAll(Pageable pageable) {
        log.debug("Request to get all CollaboratorMonthlyImputations");
        return collaboratorMonthlyImputationRepository.findAll(pageable);
    }


    /**
     * Get one collaboratorMonthlyImputation by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CollaboratorMonthlyImputation> findOne(Long id) {
        log.debug("Request to get CollaboratorMonthlyImputation : {}", id);
        return collaboratorMonthlyImputationRepository.findById(id);
    }

    /**
     * Delete the collaboratorMonthlyImputation by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete CollaboratorMonthlyImputation : {}", id);
        collaboratorMonthlyImputationRepository.deleteById(id);
    }

    @Override
    public Set<CollaboratorMonthlyImputation> findByRequestedParams(String agresso, int month, int year, String imputationType) {
        return collaboratorMonthlyImputationRepository.findByRequestedParams(agresso, month, year, imputationType);
    }

    /**
     * save all the imputations.
     *
     * @param imputation the imputation that contains the list to save
     */
    @Override
    public void saveAll(Imputation imputation) {
        imputation.getMonthlyImputations().forEach(monthlyImputation -> {
            monthlyImputation.setImputation(imputation);
            save(monthlyImputation);
        });
    }
}
