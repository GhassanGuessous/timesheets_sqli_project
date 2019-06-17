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
import com.sqli.imputation.web.rest.errors.BadRequestAlertException;
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

    public static final String APP = "app";
    public static final String TBP = "tbp";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ENTITY_NAME = "imputation";
    private static final int UNAUTHORIZED_STATUS = 401;
    private static final int UNAUTHORIZED_AUTHORITY_STATUS = 405;
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
    public List<Imputation> getTbpImputation(TbpRequestBodyDTO tbpRequestBodyDTO) {
        List<Imputation> imputations = new ArrayList<>();
        Team team = teamRepository.findByIdTbpLike(tbpRequestBodyDTO.getIdTbp());
        List<TbpRequestBodyDTO> requestBodies = tbpComposerService.divideTbpPeriod(tbpRequestBodyDTO);
        requestBodies.forEach(requestBody -> {
            getTbpImputationForTeam(imputations, team, requestBody);
        });
        return imputations;
    }

    private void getTbpImputationForTeam(List<Imputation> imputations, Team team, TbpRequestBodyDTO requestBody) {
        try {
            getTbpImputationFromWS(imputations, requestBody);
        } catch (HttpClientErrorException e) {
            throwTbpErrors(e.getStatusCode().value());
            ImputationRequestDTO imputationRequestDTO = new ImputationRequestDTO(
                team.getAgresso(), DateUtil.getMonth(requestBody.getStartDate()), DateUtil.getYear(requestBody.getStartDate()), Constants.TBP_IMPUTATION_TYPE
            );
            getImputationFromDB(imputations, imputationRequestDTO);
        }
    }

    private void getTbpImputationFromWS(List<Imputation> imputations, TbpRequestBodyDTO requestBody) {
        List<ChargeTeamDTO> chargeTeamDTOS = tbpResourceService.getTeamCharges(requestBody).getBody().getData().getCharge();
        Imputation imputation = tbpImputationConverterService.createImputation(requestBody);
        tbpImputationConverterService.convertChargesToImputation(chargeTeamDTOS, imputation);
        imputations.add(imputation);
        update(imputation);
    }

    private void throwTbpErrors(int errorStatus) {
        if (errorStatus == UNAUTHORIZED_STATUS) {
            throw new BadRequestAlertException("Tbp credentials", ENTITY_NAME, "tbp_bad_credentials");
        }
        if (errorStatus == UNAUTHORIZED_AUTHORITY_STATUS) {
            throw new BadRequestAlertException("Tbp authority", ENTITY_NAME, "tbp_bad_authority");
        }
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
    public List<ImputationComparatorDTO>  compareAppAndTbp(AppTbpRequestBodyDTO appTbpRequest) {
        Map<String, Imputation> imputations = getImputationToCompare(appTbpRequest);
        List<ImputationComparatorDTO> comparatorDTOS = utilService.compareImputations(imputations.get(APP), imputations.get(TBP));
        return comparatorDTOS;
    }

    /**
     * Get advanced comparison of APP & TBP imputataions
     *
     * @param appTbpRequest
     * @return
     */
    @Override
    public List<ImputationComparatorAdvancedDTO> compareAppAndTbpAdvanced(AppTbpRequestBodyDTO appTbpRequest) {
        Map<String, Imputation> imputations = getImputationToCompare(appTbpRequest);
        List<ImputationComparatorAdvancedDTO> comparatorAdvancedDTOS = utilService.compareImputationsAdvanced(imputations.get(APP), imputations.get(TBP));
        return comparatorAdvancedDTOS;
    }

    private Map<String, Imputation> getImputationToCompare(AppTbpRequestBodyDTO appTbpRequest) {
        AppRequestDTO appRequestDTO = requestBodyFactory.createAppRequestDTO(appTbpRequest.getTeam().getAgresso(), appTbpRequest.getYear(), appTbpRequest.getMonth());
        TbpRequestBodyDTO tbpRequestBodyDTO = requestBodyFactory.createTbpRequestBodyDTO(appTbpRequest.getTeam().getIdTbp(), appTbpRequest.getYear(), appTbpRequest.getMonth());
        setRequestBodyCredentials(appTbpRequest, tbpRequestBodyDTO);

        Imputation appImputation = getAppImputation(appRequestDTO).get(FIRST_ELEMENT_INDEX);
        Imputation tbpImputation = getTbpImputation(tbpRequestBodyDTO).get(FIRST_ELEMENT_INDEX);

        Map<String, Imputation> imputations = new HashMap<>();
        imputations.put("app", appImputation);
        imputations.put("tbp", tbpImputation);
        return imputations;
    }

    private void setRequestBodyCredentials(AppTbpRequestBodyDTO appTbpRequest, TbpRequestBodyDTO tbpRequestBodyDTO) {
        tbpRequestBodyDTO.setUsername(appTbpRequest.getUsername());
        tbpRequestBodyDTO.setPassword(appTbpRequest.getPassword());
    }

    /**
     * @param file
     * @param appRequestDTO
     * @return
     */
    @Override
    public List<ImputationComparatorDTO> compareAppPpmc(MultipartFile file, AppRequestDTO appRequestDTO) {
        Optional<Imputation> ppmcImputation = getPpmcImputation(file, appRequestDTO.getAgresso());
        if (!ppmcImputation.isPresent()) {
            throw new BadRequestAlertException("Invalid PPMC file", ENTITY_NAME, "invalidPPMC");
        } else {
            if (!ppmcImputation.get().getMonth().equals(appRequestDTO.getMonth())) {
                throw new BadRequestAlertException("Different months", ENTITY_NAME, "differentMonths");
            }
            Imputation appImputation = getAppImputation(appRequestDTO).get(FIRST_ELEMENT_INDEX);
            List<ImputationComparatorDTO> comparatorDTOS = utilService.compareImputations(appImputation, ppmcImputation.get());
            return comparatorDTOS;
        }
    }

    /**
     *
     * @param file
     * @param appRequestDTO
     * @return
     */
    @Override
    public List<ImputationComparatorAdvancedDTO> compareAppPpmcAdvanced(MultipartFile file, AppRequestDTO appRequestDTO) {
        Optional<Imputation> ppmcImputation = getPpmcImputation(file, appRequestDTO.getAgresso());
        if (!ppmcImputation.isPresent()) {
            throw new BadRequestAlertException("Invalid PPMC file", ENTITY_NAME, "invalidPPMC");
        } else {
            if (!ppmcImputation.get().getMonth().equals(appRequestDTO.getMonth())) {
                throw new BadRequestAlertException("Different months", ENTITY_NAME, "differentMonths");
            }
            Imputation appImputation = getAppImputation(appRequestDTO).get(FIRST_ELEMENT_INDEX);
            List<ImputationComparatorAdvancedDTO> comparatorDTOS = utilService.compareImputationsAdvanced(appImputation, ppmcImputation.get());
            return comparatorDTOS;
        }
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
