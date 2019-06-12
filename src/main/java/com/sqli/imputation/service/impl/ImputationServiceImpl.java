package com.sqli.imputation.service.impl;

import com.sqli.imputation.config.Constants;
import com.sqli.imputation.domain.*;
import com.sqli.imputation.repository.TeamRepository;
import com.sqli.imputation.security.SecurityUtils;
import com.sqli.imputation.service.*;
import com.sqli.imputation.repository.ImputationRepository;
import com.sqli.imputation.service.dto.*;
import com.sqli.imputation.service.dto.jira.JiraImputationDTO;
import com.sqli.imputation.service.factory.RequestBodyFactory;
import com.sqli.imputation.service.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Imputation.
 */
@Service
@Transactional
public class ImputationServiceImpl implements ImputationService {

    private final Logger log = LoggerFactory.getLogger(ImputationServiceImpl.class);

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final int SUCCESS_STATUS = 200;
    private static final int LIST_IMPUTATIONS_POSITION = 0;
    private static final int STATUS_POSITION = 1;
    private static final int INCOMPATIBLE_MONTHS_STATUS = -1;
    private static final int ALL_GOOD_STATUS = 1;
    private static final int INVALID_FILE_STATUS = 0;
    private static final int APP_INDEX = 0;
    private static final int TBP_INDEX = 1;
    private static final int FIRST_ELEMENT_INDEX = 0;

    private final ImputationRepository imputationRepository;
    @Autowired
    private CollaboratorMonthlyImputationService monthlyImputationService;
    @Autowired
    private CollaboratorDailyImputationService dailyImputationService;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private AppResourceService appResourceService;
    @Autowired
    private AppImputationConverterService appConverterService;
    @Autowired
    private DefaultTbpImputationConverterService tbpImputationConverterService;
    @Autowired
    private TBPResourceService tbpResourceService;
    @Autowired
    private JiraResourceService jiraResourceService;
    @Autowired
    private TbpRequestComposerService tbpComposerService;
    @Autowired
    private AppRequestComposerService appComposerService;
    @Autowired
    private PpmcImputationConverterService ppmcImputationConverterService;
    @Autowired
    private RequestBodyFactory requestBodyFactory;
    @Autowired
    private ImputationConverterUtilService utilService;
    @Autowired
    private MailService mailService;
    @Autowired
    private NotificationService notificationService;

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
    private void update(Imputation imputation) {
        Set<CollaboratorMonthlyImputation> monthlyImputations = monthlyImputationService.findByImputationParams(imputation);
        if (monthlyImputations.isEmpty()) {
            save(imputation);
        } else {
            updateImputation(imputation, monthlyImputations);
        }
    }

    private void updateImputation(Imputation imputation, Set<CollaboratorMonthlyImputation> monthlyImputationsFromDB) {
        imputation.getMonthlyImputations().forEach(monthlyImputation -> {
            if (utilService.isMonthlyImputationExistForCurrentCollab(monthlyImputationsFromDB, monthlyImputation.getCollaborator())) {
                updateMonthlyImputation(monthlyImputationsFromDB, monthlyImputation);
            } else {
                addNewMonthlyImputation(monthlyImputationsFromDB, monthlyImputation);
            }
        });
    }

    private void addNewMonthlyImputation(Set<CollaboratorMonthlyImputation> monthlyImputationsFromDB, CollaboratorMonthlyImputation monthlyImputation) {
        monthlyImputation.setImputation(monthlyImputationsFromDB.iterator().next().getImputation());
        monthlyImputationService.save(monthlyImputation);
    }

    private void updateMonthlyImputation(Set<CollaboratorMonthlyImputation> monthlyImputationsFromDB, CollaboratorMonthlyImputation monthlyImputation) {
        CollaboratorMonthlyImputation monthlyFromDB = utilService.findMonthlyImputationByCollab(monthlyImputationsFromDB, monthlyImputation.getCollaborator());
        monthlyImputation.getDailyImputations().forEach(dailyImputation -> {
            if (utilService.isDailyImputationExist(monthlyFromDB.getDailyImputations(), dailyImputation.getDay())) {
                updateDailyImputation(monthlyFromDB, dailyImputation);
            } else {
                addNewDailyImputation(monthlyFromDB, dailyImputation);
            }
        });
        monthlyImputationService.update(monthlyFromDB);
    }

    private void addNewDailyImputation(CollaboratorMonthlyImputation monthlyFromDB, CollaboratorDailyImputation dailyImputation) {
        dailyImputation.setCollaboratorMonthlyImputation(monthlyFromDB);
        utilService.addDailyToMonthlyImputation(monthlyFromDB, dailyImputationService.save(dailyImputation));
        utilService.setTotalOfMonthlyImputation(monthlyFromDB, dailyImputation.getCharge());
    }

    private void updateDailyImputation(CollaboratorMonthlyImputation monthlyFromDB, CollaboratorDailyImputation dailyImputation) {
        CollaboratorDailyImputation dailyFromDB = utilService.findDailyImputationByDay(monthlyFromDB.getDailyImputations(), dailyImputation.getDay());
        if (!dailyFromDB.getCharge().equals(dailyImputation.getCharge())) {
            utilService.setTotalOfMonthlyImputation(monthlyFromDB, dailyImputation.getCharge() - dailyFromDB.getCharge());
            dailyFromDB.setCharge(dailyImputation.getCharge());
            dailyImputationService.save(dailyFromDB);
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
        List<AppRequestDTO> appRequestDTOS = appComposerService.divideAppPeriod(appRequestDTO);
        appRequestDTOS.forEach(dto -> {
            try {
                getAppImputationFromWS(imputations, dto);
            } catch (HttpClientErrorException e) {
                ImputationRequestDTO imputationRequestDTO = getImputationRequestDTO(dto, Constants.APP_IMPUTATION_TYPE);
                getImputationFromDB(imputations, imputationRequestDTO);
            }
        });
        return imputations;
    }

    private void getAppImputationFromWS(List<Imputation> imputations, AppRequestDTO dto) {
        List<AppChargeDTO> appChargeDTOS = appResourceService.getTeamCharges(dto);
        Imputation imputation = appConverterService.convert(dto, appChargeDTOS);
        imputations.add(imputation);
        update(imputation);
    }

    /**
     * Get TPB imputations.
     *
     * @param tbpRequestBodyDTO
     * @return
     */
    @Override
    public Object[] getTbpImputation(TbpRequestBodyDTO tbpRequestBodyDTO) {
        List<Imputation> imputations = new ArrayList<>();
        final int[] status = {SUCCESS_STATUS};
        Team team = teamRepository.findByIdTbpLike(tbpRequestBodyDTO.getIdTbp());
        List<TbpRequestBodyDTO> requestBodies = tbpComposerService.divideTbpPeriod(tbpRequestBodyDTO);
        requestBodies.forEach(requestBody -> {
            try {
                getTbpImputationFromWS(imputations, requestBody);
            } catch (HttpClientErrorException e) {
                status[0] = e.getStatusCode().value();
                ImputationRequestDTO imputationRequestDTO = new ImputationRequestDTO(team.getAgresso(), DateUtil.getMonth(requestBody.getStartDate()), DateUtil.getYear(requestBody.getStartDate()), Constants.TBP_IMPUTATION_TYPE);
                getImputationFromDB(imputations, imputationRequestDTO);
            }
        });
        return new Object[]{imputations, status[0]};
    }

    private void getTbpImputationFromWS(List<Imputation> imputations, TbpRequestBodyDTO requestBody) {
        List<ChargeTeamDTO> chargeTeamDTOS = tbpResourceService.getTeamCharges(requestBody).getBody().getData().getCharge();
        Imputation imputation = tbpImputationConverterService.createImputation(requestBody);
        tbpImputationConverterService.convertChargesToImputation(chargeTeamDTOS, imputation);
        imputations.add(imputation);
        update(imputation);
    }

    private void getImputationFromDB(List<Imputation> imputations, ImputationRequestDTO imputationRequestDTO) {
        Optional<Imputation> imputationOptional = findByTeam(imputationRequestDTO);
        imputationOptional.ifPresent(imputations::add);
    }

    /**
     * Get PPMC imputations from Excel file.
     *
     * @param file
     * @return
     */
    @Override
    public Optional<Imputation> getPpmcImputation(MultipartFile file, String agresso) {
        Team team = teamRepository.findByAgressoLike(agresso);
        Optional<Imputation> ppmcImputation = ppmcImputationConverterService.getPpmcImputationFromExcelFile(file, team);
        if (ppmcImputation.isPresent()) {
            update(ppmcImputation.get());
            getOnlySelectedTeamWhenIsAdmin(ppmcImputation.get(), team);
        }
        return ppmcImputation;
    }

    private void getOnlySelectedTeamWhenIsAdmin(Imputation ppmcImputation, Team team) {
        if (SecurityUtils.isCurrentUserInRole(ROLE_ADMIN)) {
            getOnlySelectedTeam(ppmcImputation, team);
        }
    }

    private Imputation getOnlySelectedTeam(Imputation imputation, Team team) {
        Set<CollaboratorMonthlyImputation> forSelectedTeam = imputation.getMonthlyImputations().stream().filter(
            monthly -> monthly.getCollaborator().getTeam().getId().equals(team.getId())
        ).collect(Collectors.toSet());
        imputation.setMonthlyImputations(forSelectedTeam);
        return imputation;
    }

    @Override
    public JiraImputationDTO getJiraImputation(AppTbpRequestBodyDTO requestBodyDTO) {
        return jiraResourceService.getAllStories(requestBodyDTO);
    }


    /**
     * Get comparison of APP & TBP imputataions
     *
     * @param appTbpRequest
     * @return
     */
    @Override
    public Object[] compareAppAndTbp(AppTbpRequestBodyDTO appTbpRequest) {
        Object[] result = getImputationToCompare(appTbpRequest);
        Imputation[] imputations = (Imputation[]) result[FIRST_ELEMENT_INDEX];
        List<ImputationComparatorDTO> comparatorDTOS = utilService.compareImputations(imputations[APP_INDEX], imputations[TBP_INDEX]);
        return new Object[]{comparatorDTOS, result[STATUS_POSITION]};
    }

    /**
     * Get advanced comparison of APP & TBP imputataions
     *
     * @param appTbpRequest
     * @return
     */
    @Override
    public Object[] compareAppAndTbpAdvanced(AppTbpRequestBodyDTO appTbpRequest) {
        Object[] result = getImputationToCompare(appTbpRequest);
        Imputation[] imputations = (Imputation[]) result[FIRST_ELEMENT_INDEX];
        List<ImputationComparatorAdvancedDTO> comparatorAdvancedDTOS = utilService.compareImputationsAdvanced(imputations[APP_INDEX], imputations[TBP_INDEX]);
        return new Object[]{comparatorAdvancedDTOS, result[STATUS_POSITION]};
    }

    private Object[] getImputationToCompare(AppTbpRequestBodyDTO appTbpRequest) {
        AppRequestDTO appRequestDTO = requestBodyFactory.createAppRequestDTO(appTbpRequest.getTeam().getAgresso(), appTbpRequest.getYear(), appTbpRequest.getMonth());
        TbpRequestBodyDTO tbpRequestBodyDTO = requestBodyFactory.createTbpRequestBodyDTO(appTbpRequest.getTeam().getIdTbp(), appTbpRequest.getYear(), appTbpRequest.getMonth());
        setRequestBodyCredentials(appTbpRequest, tbpRequestBodyDTO);

        Imputation appImputation = getAppImputation(appRequestDTO).get(FIRST_ELEMENT_INDEX);
        Object[] result = getTbpImputation(tbpRequestBodyDTO);
        List<Imputation> imputations = (List<Imputation>) result[LIST_IMPUTATIONS_POSITION];
        int status = (int) result[STATUS_POSITION];
        Imputation tbpImputation = (status == SUCCESS_STATUS) ? imputations.get(FIRST_ELEMENT_INDEX) : new Imputation();
        return new Object[]{new Imputation[]{appImputation, tbpImputation}, status};
    }

    private void setRequestBodyCredentials(AppTbpRequestBodyDTO appTbpRequest, TbpRequestBodyDTO tbpRequestBodyDTO) {
        tbpRequestBodyDTO.setUsername(appTbpRequest.getUsername());
        tbpRequestBodyDTO.setPassword(appTbpRequest.getPassword());
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
        Optional<Imputation> ppmcImputation = getPpmcImputation(file, appRequestDTO.getAgresso());
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
        Optional<Imputation> ppmcImputation = getPpmcImputation(file, appRequestDTO.getAgresso());
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
        notifications.forEach(dto -> {
            Optional<Notification> notificationFromDB = notificationService.find(dto);
            if (!notificationFromDB.isPresent()) {
                Notification notification = notificationService.createFromDTO(dto);
                notificationService.save(notification);
            }
            mailService.sendNotificationMail(dto);
        });
    }

    @Override
    public List<ImputationComparatorDTO> getComparisonFromDB(AppRequestDTO appRequestDTO, String ppmcImputationType) {
        ImputationRequestDTO imputationRequestDTO = getImputationRequestDTO(appRequestDTO, ppmcImputationType);
        Optional<Imputation> ppmcImputation = findByTeam(imputationRequestDTO);
        if (ppmcImputation.isPresent()) {
            Imputation appImputation = getAppImputation(appRequestDTO).get(FIRST_ELEMENT_INDEX);
            return utilService.compareImputations(appImputation, ppmcImputation.get());
        }
        return Collections.emptyList();
    }

    private ImputationRequestDTO getImputationRequestDTO(AppRequestDTO appRequestDTO, String ppmcImputationType) {
        return new ImputationRequestDTO(appRequestDTO.getAgresso(), appRequestDTO.getYear(), appRequestDTO.getMonth(), ppmcImputationType);
    }

    @Override
    public List<ImputationComparatorAdvancedDTO> getAdvancedComparisonFromDB(AppRequestDTO appRequestDTO, String ppmcImputationType) {
        ImputationRequestDTO imputationRequestDTO = getImputationRequestDTO(appRequestDTO, ppmcImputationType);
        Optional<Imputation> ppmcImputation = findByTeam(imputationRequestDTO);
        if (ppmcImputation.isPresent()) {
            Imputation appImputation = getAppImputation(appRequestDTO).get(FIRST_ELEMENT_INDEX);
            return utilService.compareImputationsAdvanced(appImputation, ppmcImputation.get());
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<Imputation> findByTeam(ImputationRequestDTO imputationRequestDTO) {
        Set<CollaboratorMonthlyImputation> monthlyImputations = monthlyImputationService.findByImputationAndTeam(imputationRequestDTO);
        if (!monthlyImputations.isEmpty()) {
            return createImputation(imputationRequestDTO, monthlyImputations);
        }
        return Optional.empty();
    }

    private Optional<Imputation> createImputation(ImputationRequestDTO imputationRequestDTO, Set<CollaboratorMonthlyImputation> monthlyImputations) {
        Imputation imputation = utilService.createImputation(
            imputationRequestDTO.getYear(), imputationRequestDTO.getMonth(), utilService.findImputationTypeByNameLike(imputationRequestDTO.getType())
        );
        imputation.setMonthlyImputations(monthlyImputations);
        return Optional.of(imputation);
    }
}
