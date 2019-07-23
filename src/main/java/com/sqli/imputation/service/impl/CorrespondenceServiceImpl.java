package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.CorrespondenceService;
import com.sqli.imputation.domain.Correspondence;
import com.sqli.imputation.repository.CorrespondenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing Correspondence.
 */
@Service
@Transactional
public class CorrespondenceServiceImpl implements CorrespondenceService {

    private final Logger log = LoggerFactory.getLogger(CorrespondenceServiceImpl.class);

    private final CorrespondenceRepository correspondenceRepository;

    public CorrespondenceServiceImpl(CorrespondenceRepository correspondenceRepository) {
        this.correspondenceRepository = correspondenceRepository;
    }

    /**
     * Save a correspondence.
     *
     * @param correspondence the entity to save
     * @return the persisted entity
     */
    @Override
    public Correspondence save(Correspondence correspondence) {
        log.debug("Request to save Correspondence : {}", correspondence);
        return correspondenceRepository.save(correspondence);
    }

    /**
     * Get all the correspondences.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Correspondence> findAll(Pageable pageable) {
        log.debug("Request to get all Correspondences");
        return correspondenceRepository.findAll(pageable);
    }


    /**
     * Get one correspondence by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Correspondence> findOne(Long id) {
        log.debug("Request to get Correspondence : {}", id);
        return correspondenceRepository.findById(id);
    }

    /**
     * Delete the correspondence by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Correspondence : {}", id);
        correspondenceRepository.deleteById(id);
    }

    /**
     * Get all the correspondences with key.
     *
     * @param key      the key to base searching on
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<Correspondence> findByKey(String key, Pageable pageable) {
        log.debug("Request to get all Correspondences with key: {}", key);
        return correspondenceRepository.findBykey(key, pageable);
    }
}
