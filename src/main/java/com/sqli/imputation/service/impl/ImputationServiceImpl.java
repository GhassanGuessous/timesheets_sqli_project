package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Team;
import com.sqli.imputation.repository.TeamRepository;
import com.sqli.imputation.domain.CollaboratorMonthlyImputation;
import com.sqli.imputation.domain.CollaboratorDailyImputation;
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

    public static final int INCOMPATIBLE_MONTHS_STATUS = -1;
    public static final int ALL_GOOD_STATUS = 1;
    public static final int INVALID_FILE_STATUS = 0;
    public static final int APP_INDEX = 0;
    public static final int TBP_INDEX = 1;
    private final Logger log = LoggerFactory.getLogger(ImputationServiceImpl.class);
    public static final int FIRST_ELEMENT_INDEX = 0;

    private final ImputationRepository imputationRepository;
    @Autowired
    private CollaboratorMonthlyImputationService monthlyImputationService;
    @Autowired
    private CollaboratorDailyImputationService dailyImputationService;
    @Autowired
    private TeamRepository teamRepository;
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
    @Autowired
    private MailService mailService;

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
        imputation = imputationRepository.save(imputation);
        monthlyImputationService.saveAll(imputation);
        return imputation;
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
     * Update the imputation by team.
     *
     * @param imputation the id of the entity
     */
    private void update(Imputation imputation, Team team) {
        if (team == null) throw new RuntimeException("Team can not be null");
        Set<CollaboratorMonthlyImputation> monthlyImputations = monthlyImputationService.findByRequestedParams(team.getAgresso(), imputation.getMonth(), imputation.getYear(), imputation.getImputationType().getName());
        if (monthlyImputations.isEmpty()) {
            save(imputation);
        } else {
            updateImputation(imputation, monthlyImputations);
        }
    }

    private void updateImputation(Imputation imputation, Set<CollaboratorMonthlyImputation> monthlyImputations) {
        imputation.getMonthlyImputations().forEach(monthlyImputation -> {
            if (utilService.isMonthlyImputationExistForCurrentCollab(monthlyImputations, monthlyImputation.getCollaborator())) {
                updateMonthlyImputation(monthlyImputations, monthlyImputation);
            } else {
                addNewMonthlyImputation(monthlyImputations, monthlyImputation);
            }
        });
    }

    private void addNewMonthlyImputation(Set<CollaboratorMonthlyImputation> monthlyImputations, CollaboratorMonthlyImputation monthlyImputation) {
        monthlyImputation.setImputation(monthlyImputations.iterator().next().getImputation());
        monthlyImputationService.save(monthlyImputation);
    }

    private void updateMonthlyImputation(Set<CollaboratorMonthlyImputation> monthlyImputations, CollaboratorMonthlyImputation monthlyImputation) {
        CollaboratorMonthlyImputation monthlyFromDB = utilService.findMonthlyImputationByCollab(monthlyImputations, monthlyImputation.getCollaborator());
        monthlyImputation.getDailyImputations().forEach(dailyImputation -> {
            if (utilService.isDailyImputationExist(monthlyFromDB.getDailyImputations(), dailyImputation.getDay())) {
                updateDailyImputation(monthlyFromDB, dailyImputation);
            } else {
                addNewDailyImputation(monthlyFromDB, dailyImputation);
            }
        });
        monthlyImputationService.save(monthlyFromDB);
    }

    private void addNewDailyImputation(CollaboratorMonthlyImputation monthlyFromDB, CollaboratorDailyImputation dailyImputation) {
        dailyImputation.setCollaboratorMonthlyImputation(monthlyFromDB);
        utilService.addDailyToMonthlyImputation(monthlyFromDB, dailyImputationService.save(dailyImputation));
        utilService.setTotalImputationOfCollab(monthlyFromDB, dailyImputation.getCharge());
    }

    private void updateDailyImputation(CollaboratorMonthlyImputation monthlyFromDB, CollaboratorDailyImputation dailyImputation) {
        CollaboratorDailyImputation dailyFromDB = utilService.findDailyImputationByCollab(monthlyFromDB.getDailyImputations(), dailyImputation.getDay());
        if (!dailyFromDB.getCharge().equals(dailyImputation.getCharge())) {
            utilService.setTotalImputationOfCollab(monthlyFromDB, dailyImputation.getCharge() - dailyFromDB.getCharge());
            dailyFromDB.setCharge(dailyImputation.getCharge());
            utilService.replaceDailyImputation(monthlyFromDB, dailyFromDB);
        }
    }

    /**
     * Get the App imputations.
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
            update(imputation, teamRepository.findByAgressoLike(appRequestDTO.getAgresso()));
        });
        return imputations;
    }

    /**
     * Get TPB imputations.
     *
     * @param tbpRequestBodyDTO
     * @return
     */
    @Override
    public List<Imputation> getTbpImputation(TbpRequestBodyDTO tbpRequestBodyDTO) {
        List<Imputation> imputations = new ArrayList<>();
        List<TbpRequestBodyDTO> requestBodies = composerService.tbpDividePeriod(tbpRequestBodyDTO);
        requestBodies.forEach(requestBody -> {
            List<ChargeTeamDTO> chargeTeamDTOS = tbpResourceService.getTeamCharges(requestBody).getBody().getData().getCharge();
            Imputation imputation = tbpImputationConverterService.convert(chargeTeamDTOS, requestBody);
            imputations.add(imputation);
            update(imputation, teamRepository.findByIdTbpLike(tbpRequestBodyDTO.getIdTbp()));
        });
        return imputations;
    }

    /**
     * Get PPMC imputations from Excel file.
     *
     * @param file
     * @return
     */
    @Override
    public Optional<Imputation> getPpmcImputation(MultipartFile file) {
        Optional<Imputation> ppmcImputation = ppmcImputationConverterService.getPpmcImputationFromExcelFile(file);
        if(ppmcImputation.isPresent()){
            save(ppmcImputation.get());
        }
        return ppmcImputation;
    }

    /**
     * Get comparison of APP & TBP imputataions
     *
     * @param appTbpRequest
     * @return
     */
    @Override
    public List<ImputationComparatorDTO> compareAppAndTbp(AppTbpRequestBodyDTO appTbpRequest) {
        Imputation[] imputations = getImputationToCompare(appTbpRequest);
        return utilService.compareImputations(imputations[APP_INDEX], imputations[TBP_INDEX]);
    }

    /**
     * Get advanced comparison of APP & TBP imputataions
     *
     * @param appTbpRequest
     * @return
     */
    @Override
    public List<ImputationComparatorAdvancedDTO> compareAppAndTbpAdvanced(AppTbpRequestBodyDTO appTbpRequest) {
        Imputation[] imputations = getImputationToCompare(appTbpRequest);
        return utilService.compareImputationsAdvanced(imputations[APP_INDEX], imputations[TBP_INDEX]);
    }

    private Imputation[] getImputationToCompare(AppTbpRequestBodyDTO appTbpRequest) {
        AppRequestDTO appRequestDTO = requestBodyFactory.createAppRequestDTO(appTbpRequest.getTeam().getAgresso(), appTbpRequest.getYear(), appTbpRequest.getMonth());
        TbpRequestBodyDTO tbpRequestBodyDTO = requestBodyFactory.createTbpRequestBodyDTO(appTbpRequest.getTeam().getIdTbp(), appTbpRequest.getYear(), appTbpRequest.getMonth());

        Imputation appImputation = getAppImputation(appRequestDTO).get(FIRST_ELEMENT_INDEX);
        Imputation tbpImputation = getTbpImputation(tbpRequestBodyDTO).get(FIRST_ELEMENT_INDEX);
        return new Imputation[]{appImputation, tbpImputation};
    }

    /**
     * @param file
     * @param appRequestDTO
     * @return an array contains :
     * first element : a list of DTOs of imputation comparison (full or empty)
     * second element : a status that describe what happened ;
     * # -1 : comparison of two different months
     * # 0 : something wrong happened while reading excel file
     * # 1 : all good
     */
    @Override
    public Object[] compareAppPpmc(MultipartFile file, AppRequestDTO appRequestDTO) {
        Optional<Imputation> ppmcImputation = getPpmcImputation(file);
        if (ppmcImputation.isPresent()) {
            if (!ppmcImputation.get().getMonth().equals(appRequestDTO.getMonth())) {
                return new Object[]{Collections.emptyList(), INCOMPATIBLE_MONTHS_STATUS};
            }
            Imputation appImputation = getAppImputation(appRequestDTO).get(FIRST_ELEMENT_INDEX);
            List<ImputationComparatorDTO> comparatorDTOS = utilService.compareImputations(appImputation, ppmcImputation.get());
            return new Object[]{comparatorDTOS, ALL_GOOD_STATUS};
        }
        return new Object[]{Collections.emptyList(), INVALID_FILE_STATUS};
    }

    /**
     * @param file
     * @param appRequestDTO
     * @return an array contains :
     * first element : a list of DTOs of advanced imputation comparison (full or empty)
     * second element : a status that describe what happened ;
     * # -1 : comparison of two different months
     * # 0 : something wrong happened while reading excel file
     * # 1 : all good
     */
    @Override
    public Object[] compareAppPpmcAdvanced(MultipartFile file, AppRequestDTO appRequestDTO) {
        Optional<Imputation> ppmcImputation = getPpmcImputation(file);
        if (ppmcImputation.isPresent()) {
            if (!ppmcImputation.get().getMonth().equals(appRequestDTO.getMonth())) {
                return new Object[]{Collections.emptyList(), INCOMPATIBLE_MONTHS_STATUS};
            }
            Imputation appImputation = getAppImputation(appRequestDTO).get(FIRST_ELEMENT_INDEX);
            List<ImputationComparatorAdvancedDTO> comparatorDTOS = utilService.compareImputationsAdvanced(appImputation, ppmcImputation.get());
            return new Object[]{comparatorDTOS, ALL_GOOD_STATUS};
        }
        return new Object[]{Collections.emptyList(), INVALID_FILE_STATUS};
    }

    @Override
    public void sendNotifications(List<NotificationDTO> notifications) {
        notifications.forEach(notification -> mailService.sendNotificationMail(notification));
    }

    @Override
    public List<ImputationComparatorDTO> getComparisonFromDB(AppRequestDTO appRequestDTO, String ppmcImputationType) {
        Optional<Imputation> ppmcImputation = findByRequestedParams(appRequestDTO, ppmcImputationType);
        if(ppmcImputation.isPresent()){
            Imputation appImputation = getAppImputation(appRequestDTO).get(FIRST_ELEMENT_INDEX);
            return utilService.compareImputations(appImputation, ppmcImputation.get());
        }
        return Collections.emptyList();
    }

    @Override
    public List<ImputationComparatorAdvancedDTO> getAdvancedComparisonFromDB(AppRequestDTO appRequestDTO, String ppmcImputationType) {
        Optional<Imputation> ppmcImputation = findByRequestedParams(appRequestDTO, ppmcImputationType);
        if(ppmcImputation.isPresent()){
            Imputation appImputation = getAppImputation(appRequestDTO).get(FIRST_ELEMENT_INDEX);
            return utilService.compareImputationsAdvanced(appImputation, ppmcImputation.get());
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<Imputation> findByRequestedParams(AppRequestDTO appRequestDTO, String ppmcImputationType) {
        Set<CollaboratorMonthlyImputation> monthlyImputations = monthlyImputationService.findByRequestedParams(appRequestDTO.getAgresso(), appRequestDTO.getMonth(), appRequestDTO.getYear(), ppmcImputationType);
        if (!monthlyImputations.isEmpty()) {
            return createImputation(appRequestDTO, ppmcImputationType, monthlyImputations);
        }
        return Optional.empty();
    }

    private Optional<Imputation> createImputation(AppRequestDTO appRequestDTO, String ppmcImputationType, Set<CollaboratorMonthlyImputation> monthlyImputations) {
        Imputation imputation = utilService.createImputation(
            appRequestDTO.getYear(), appRequestDTO.getMonth(), utilService.findImputationTypeByNameLike(ppmcImputationType)
        );
        imputation.setMonthlyImputations(monthlyImputations);
        return Optional.of(imputation);
    }
}
