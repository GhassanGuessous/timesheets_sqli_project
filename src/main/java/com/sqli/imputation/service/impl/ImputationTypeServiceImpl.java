package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.ImputationTypeService;
import com.sqli.imputation.domain.ImputationType;
import com.sqli.imputation.repository.ImputationTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing ImputationType.
 */
@Service
@Transactional
public class ImputationTypeServiceImpl implements ImputationTypeService {

    private final Logger log = LoggerFactory.getLogger(ImputationTypeServiceImpl.class);

    private final ImputationTypeRepository imputationTypeRepository;

    public ImputationTypeServiceImpl(ImputationTypeRepository imputationTypeRepository) {
        this.imputationTypeRepository = imputationTypeRepository;
    }

    /**
     * Save a imputationType.
     *
     * @param imputationType the entity to save
     * @return the persisted entity
     */
    @Override
    public ImputationType save(ImputationType imputationType) {
        log.debug("Request to save ImputationType : {}", imputationType);
        return imputationTypeRepository.save(imputationType);
    }

    /**
     * Get all the imputationTypes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ImputationType> findAll(Pageable pageable) {
        log.debug("Request to get all ImputationTypes");
        return imputationTypeRepository.findAll(pageable);
    }


    /**
     * Get one imputationType by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ImputationType> findOne(Long id) {
        log.debug("Request to get ImputationType : {}", id);
        return imputationTypeRepository.findById(id);
    }

    /**
     * Delete the imputationType by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ImputationType : {}", id);
        imputationTypeRepository.deleteById(id);
    }
}
