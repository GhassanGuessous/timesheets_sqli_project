package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.*;
import com.sqli.imputation.domain.Imputation;
import com.sqli.imputation.repository.ImputationRepository;
import com.sqli.imputation.service.dto.*;
import com.sqli.imputation.service.factory.RequestBodyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * Service Implementation for managing Imputation.
 */
@Service
@Transactional
public class ImputationServiceImpl implements ImputationService {

    private final Logger log = LoggerFactory.getLogger(ImputationServiceImpl.class);
    public static final int FIRST_ELEMENT_INDEX = 0;

    private final ImputationRepository imputationRepository;
    @Autowired
    private AppParserService appParserService;
    @Autowired
    private AppImputationConverterService appConverterService;
    @Autowired
    private TbpImputationConverterService tbpImputationConverterService;
    @Autowired
    private TBPResourceService tbpResourceService;
    @Autowired
    private TbpRequestComposerService composerService;
    @Autowired
    private PpmcImputationConverterService ppmcImputationConverterService;
    @Autowired
    private RequestBodyFactory requestBodyFactory;
    @Autowired
    private ImputationConverterUtilService utilService;

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
    public List<Imputation> getAppImputation(AppRequestDTO appRequestDTO) {
        List<Imputation> imputations = new ArrayList<>();
        List<AppRequestDTO> appRequestDTOS = composerService.appDividePeriod(appRequestDTO);
        appRequestDTOS.forEach(dto -> {
            List<AppChargeDTO> appChargeDTOS = appParserService.parse();
            Imputation imputation = appConverterService.convert(dto, appChargeDTOS);
            imputations.add(imputation);
        });
        return imputations;
    }

    @Override
    public List<Imputation> getTbpImputation(TbpRequestBodyDTO tbpRequestBodyDTO) {
        List<Imputation> imputations = new ArrayList<>();
        List<TbpRequestBodyDTO> requestBodies = composerService.tbpDividePeriod(tbpRequestBodyDTO);
        requestBodies.forEach(requestBody -> {
            List<ChargeTeamDTO> chargeTeamDTOS = tbpResourceService.getTeamCharges(requestBody).getBody().getData().getCharge();
            Imputation imputation = tbpImputationConverterService.convert(chargeTeamDTOS, requestBody);
            imputations.add(imputation);
        });
        return imputations;
    }

    @Override
    public Optional<Imputation> getPpmcImputation(MultipartFile file) {
        return ppmcImputationConverterService.getPpmcImputationFromExcelFile(file);
    }

    @Override
    public List<ImputationComparatorDTO> compareAppAndTbp(AppTbpRequestBodyDTO appTbpRequest) {
        AppRequestDTO appRequestDTO = requestBodyFactory.createAppRequestDTO(appTbpRequest.getTeam().getAgresso(), appTbpRequest.getYear(), appTbpRequest.getMonth());
        TbpRequestBodyDTO tbpRequestBodyDTO = requestBodyFactory.createTbpRequestBodyDTO(appTbpRequest.getTeam().getIdTbp(), appTbpRequest.getYear(), appTbpRequest.getMonth());

        Imputation appImputation = getAppImputation(appRequestDTO).get(FIRST_ELEMENT_INDEX);
        Imputation tbpImputation = getTbpImputation(tbpRequestBodyDTO).get(FIRST_ELEMENT_INDEX);
        List<ImputationComparatorDTO> comparatorDTOS = utilService.compareImputations(appImputation, tbpImputation);
        return comparatorDTOS;
    }

    @Override
    public List<ImputationComparatorDTO> compare_app_ppmc(MultipartFile file, AppRequestDTO appRequestDTO) {
        Optional<Imputation> ppmcImputation = getPpmcImputation(file);
        if(ppmcImputation.isPresent()) {
            //compare month
            Imputation appImputation = getAppImputation(appRequestDTO).get(FIRST_ELEMENT_INDEX);
            List<ImputationComparatorDTO> comparatorDTOS = utilService.compareImputations(appImputation, ppmcImputation.get());
            return comparatorDTOS;
        }
        return Collections.EMPTY_LIST;
    }
}
