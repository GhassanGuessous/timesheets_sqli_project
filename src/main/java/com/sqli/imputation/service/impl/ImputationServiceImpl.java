package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.ImputationType;
import com.sqli.imputation.repository.ImputationTypeRepository;
import com.sqli.imputation.service.AppImputationConverterService;
import com.sqli.imputation.service.ImputationService;
import com.sqli.imputation.domain.Imputation;
import com.sqli.imputation.repository.ImputationRepository;
import com.sqli.imputation.service.dto.AppChargeDTO;
import com.sqli.imputation.service.dto.AppRequestDTO;
import com.sqli.imputation.service.factory.ImputationFactory;
import org.omg.CORBA.PRIVATE_MEMBER;
import com.sqli.imputation.service.TbpImputationConverterService;
import com.sqli.imputation.service.ImputationService;
import com.sqli.imputation.domain.Imputation;
import com.sqli.imputation.repository.ImputationRepository;
import com.sqli.imputation.service.TBPResourceService;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing Imputation.
 */
@Service
@Transactional
public class ImputationServiceImpl implements ImputationService {

    @Autowired
    private AppParserService appParserService;
    @Autowired
    private AppImputationConverterService appConverterService;

    private final Logger log = LoggerFactory.getLogger(ImputationServiceImpl.class);

    private final ImputationRepository imputationRepository;

    @Autowired
    private TbpImputationConverterService tbpImputationConverterService;
    @Autowired
    private TBPResourceService tbpResourceService;

    public ImputationServiceImpl(ImputationRepository imputationRepository) {
        this.imputationRepository = imputationRepository;
    }

    /**
     * Save a imputation.
     *
     * @param imputation the entity to save
     * @return the persisted entity
     */
    @Override
    public Imputation save(Imputation imputation) {
        log.debug("Request to save Imputation : {}", imputation);
        return imputationRepository.save(imputation);
    }

    /**
     * Get all the imputations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Imputation> findAll(Pageable pageable) {
        log.debug("Request to get all Imputations");
        return imputationRepository.findAll(pageable);
    }


    /**
     * Get one imputation by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Imputation> findOne(Long id) {
        log.debug("Request to get Imputation : {}", id);
        return imputationRepository.findById(id);
    }

    /**
     * Delete the imputation by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Imputation : {}", id);
        imputationRepository.deleteById(id);
    }

    /**
     * Get the App imputation.
     *
     * @param appRequestDTO the app imputation request
     * @return the entity
     */
    @Override
        public Imputation getAppImputation(AppRequestDTO appRequestDTO) {
        List<AppChargeDTO> appChargeDTOS= appParserService.parse();
        return appConverterService.convert(appRequestDTO,appChargeDTOS);
    }

    @Override
    public Imputation findTbpImputation(TbpRequestBodyDTO tbpRequestBodyDTO) {
        return tbpImputationConverterService.convert(tbpResourceService.getTeamCharges(tbpRequestBodyDTO).getBody().getData().getCharge(), tbpRequestBodyDTO);
    }
}
