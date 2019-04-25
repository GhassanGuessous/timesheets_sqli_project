package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Imputation;
import com.sqli.imputation.domain.Imputation;
import com.sqli.imputation.service.CollaboratorDailyImputationService;
import com.sqli.imputation.service.CollaboratorMonthlyImputationService;
import com.sqli.imputation.domain.CollaboratorMonthlyImputation;
import com.sqli.imputation.repository.CollaboratorMonthlyImputationRepository;
import com.sqli.imputation.service.dto.AppRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
        return collaboratorMonthlyImputationRepository.save(collaboratorMonthlyImputation);
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
    public Optional<Imputation> findByRequestedParams(AppRequestDTO appRequestDTO, String imputationType) {
        Optional<List<CollaboratorMonthlyImputation>> monthlyImputations = collaboratorMonthlyImputationRepository.findByRequestedParams(
            appRequestDTO.getAgresso(), appRequestDTO.getMonth(), appRequestDTO.getYear(), imputationType
        );
        if(monthlyImputations.isPresent()){
            Imputation imputation = utilService.createImputation(
                appRequestDTO.getYear(), appRequestDTO.getMonth(), utilService.findImputationTypeByNameLike(imputationType)
            );
            imputation.setMonthlyImputations(new HashSet<>(monthlyImputations.get()));
            return Optional.of(imputation);
        }
        return Optional.empty();
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
            monthlyImputation = save(monthlyImputation);
            dailyImputationService.saveAll(monthlyImputation);
        });
    }
}
