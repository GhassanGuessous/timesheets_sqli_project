package com.sqli.imputation.service.impl;

import com.sqli.imputation.config.Constants;
import com.sqli.imputation.domain.*;
import com.sqli.imputation.repository.TeamRepository;
import com.sqli.imputation.security.SecurityUtils;
import com.sqli.imputation.service.*;
import com.sqli.imputation.repository.ImputationRepository;
import com.sqli.imputation.service.dto.*;
import com.sqli.imputation.service.dto.jira.IssueTypeStatisticsDTO;
import com.sqli.imputation.service.dto.jira.JiraImputationDTO;
import com.sqli.imputation.service.dto.jira.PpmcProjectWorklogDTO;
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

import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Imputation.
 */
@Service
@Transactional
public class ImputationServiceImpl implements ImputationService {

    private final Logger log = LoggerFactory.getLogger(ImputationServiceImpl.class);

    private static final String APP = "app";
    private static final String TBP = "tbp";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ENTITY_NAME = "imputation";
    private static final int UNAUTHORIZED_STATUS = 401;
    private static final int UNAUTHORIZED_AUTHORITY_STATUS = 405;
    private static final int NOT_FOUND_STATUS = 400;
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
    private TbpResourceService tbpResourceService;
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
    @Autowired
    private JiraStatisticsService jiraStatisticsService;
    @Autowired
    private CollaboratorService collaboratorService;
    @Autowired
    private AppTbpIdentifierService appTbpIdentifierService;

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
            utilService.setTotalOfMonthlyImputation(monthlyFromDB, getDifferenceBetweenOldAndNewCharge(dailyImputation, dailyFromDB));
            dailyFromDB.setCharge(dailyImputation.getCharge());
            dailyImputationService.save(dailyFromDB);
            utilService.replaceDailyImputation(monthlyFromDB, dailyFromDB);
        }
    }

    private double getDifferenceBetweenOldAndNewCharge(CollaboratorDailyImputation dailyImputation, CollaboratorDailyImputation dailyFromDB) {
        return dailyImputation.getCharge() - dailyFromDB.getCharge();
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
        appRequestDTOS.forEach(dto -> getAppImputationByPeriod(imputations, dto));
        return imputations;
    }

    private void getAppImputationByPeriod(List<Imputation> imputations, AppRequestDTO dto) {
        try {
            getAppImputationFromWS(imputations, dto);
        } catch (HttpClientErrorException | IOException | SOAPException e) {
            ImputationRequestDTO imputationRequestDTO = getImputationRequestDTO(dto, Constants.APP_IMPUTATION_TYPE);
            getImputationFromDB(imputations, imputationRequestDTO);
        }
    }

    private void getAppImputationFromWS(List<Imputation> imputations, AppRequestDTO dto) throws IOException, SOAPException {
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
        List<TbpRequestBodyDTO> requestBodies = tbpComposerService.divideTbpPeriod(tbpRequestBodyDTO);
        requestBodies.forEach(requestBody -> getTbpImputationByPeriod(imputations, tbpRequestBodyDTO.getIdTbp(), requestBody));
        return imputations;
    }

    private void getTbpImputationByPeriod(List<Imputation> imputations, String idTbp, TbpRequestBodyDTO requestBody) {
        try {
            getTbpImputationFromWS(imputations, requestBody);
        } catch (HttpClientErrorException e) {
            throwTbpErrors(e.getStatusCode().value());
            //I set idTbp instead of agresso (the dto has agresso and not id tbp)
            //because we should find the team by idd tbp
            //and because both idTbp and agresso are strings
            ImputationRequestDTO imputationRequestDTO = new ImputationRequestDTO(
                idTbp, DateUtil.getMonth(requestBody.getStartDate()), DateUtil.getYear(requestBody.getStartDate()), Constants.TBP_IMPUTATION_TYPE
            );
            getTbpImputationFromDB(imputations, imputationRequestDTO);
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

    private void getTbpImputationFromDB(List<Imputation> imputations, ImputationRequestDTO imputationRequestDTO) {
        Optional<Imputation> imputationOptional = findByTeamTbp(imputationRequestDTO);
        imputationOptional.ifPresent(imputations::add);
    }

    /**
     * Get PPMC imputations from Excel file.
     *
     * @param file
     * @return
     */
    @Override
    public Optional<Imputation> getPpmcImputation(String agresso, MultipartFile file) {
        Team team = appTbpIdentifierService.findByAgresso(agresso).getTeam();
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
    public List<JiraImputationDTO> getJiraImputation(TbpRequestBodyDTO requestBodyDTO) {
        List<JiraImputationDTO> jiraImputations = new ArrayList<>();
        List<TbpRequestBodyDTO> requestBodies = tbpComposerService.divideTbpPeriod(requestBodyDTO);
        requestBodies.forEach(requestBody -> {
            try {
                List<Collaborator> collaborators = collaboratorService.findByTeamIdTbp(requestBodyDTO.getIdTbp());
                JiraImputationDTO jiraImputationDTO = jiraResourceService.getJiraImputation(collaborators, requestBody);
                jiraImputations.add(jiraImputationDTO);
            } catch (HttpClientErrorException e) {
                throwJiraErrors(e.getStatusCode().value());
            }
        });
        return jiraImputations;
    }

    private void throwJiraErrors(int statusCode) {
        if (statusCode == UNAUTHORIZED_STATUS) {
            throw new BadRequestAlertException("Jira credentials", ENTITY_NAME, "jira_bad_credentials");
        }
        if (statusCode == NOT_FOUND_STATUS) {
            throw new BadRequestAlertException("JIRA bad url", ENTITY_NAME, "jira_bad_url");
        }
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
        AppRequestDTO appRequestDTO = requestBodyFactory.createAppRequestDTO(appTbpRequest.getAppTbpIdentifier().getAgresso(), appTbpRequest.getYear(), appTbpRequest.getMonth());
        TbpRequestBodyDTO tbpRequestBodyDTO = requestBodyFactory.createTbpRequestBodyDTO(appTbpRequest.getAppTbpIdentifier().getIdTbp(), appTbpRequest.getYear(), appTbpRequest.getMonth());
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
        Optional<Imputation> ppmcImputation = getPpmcImputation(appRequestDTO.getAgresso(), file);
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
        Optional<Imputation> ppmcImputation = getPpmcImputation(appRequestDTO.getAgresso(), file);
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

    @Override
    public Optional<Imputation> findByTeamTbp(ImputationRequestDTO imputationRequestDTO) {
        Set<CollaboratorMonthlyImputation> monthlyImputations = monthlyImputationService.findByImputationAndTeamTbp(imputationRequestDTO);
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

    @Override
    public List<PpmcProjectWorklogDTO> getPpmcProjectWorkloged(TbpRequestBodyDTO requestBodyDTO) {
        List<PpmcProjectWorklogDTO> ppmcProjectWorklogDTOS = new ArrayList<>();
        try {
            ppmcProjectWorklogDTOS = getPpmcProjectWorklogDTOS(requestBodyDTO);
        } catch (HttpClientErrorException e) {
            throwJiraErrors(e.getStatusCode().value());
        }
        return ppmcProjectWorklogDTOS;
    }

    private List<PpmcProjectWorklogDTO> getPpmcProjectWorklogDTOS(TbpRequestBodyDTO requestBodyDTO) {
        List<PpmcProjectWorklogDTO> ppmcProjectWorklogDTOS;
        if (requestBodyDTO.getIdTbp() == null || requestBodyDTO.getIdTbp().isEmpty()) {
            ppmcProjectWorklogDTOS = jiraStatisticsService.getPpmcProjectWorkloged(collaboratorService.findAll(), requestBodyDTO);
        }else{
            ppmcProjectWorklogDTOS = jiraStatisticsService.getPpmcProjectWorkloged(collaboratorService.findByTeamIdTbp(requestBodyDTO.getIdTbp()), requestBodyDTO);
        }
        return ppmcProjectWorklogDTOS;
    }

    @Override
    public List<IssueTypeStatisticsDTO> getIssueTypeStatistics(TbpRequestBodyDTO requestBodyDTO) {
        List<IssueTypeStatisticsDTO> issueTypeStatisticsDTOS = new ArrayList<>();
        try {
            issueTypeStatisticsDTOS = getIssueTypeStatisticsDTOS(requestBodyDTO);
        } catch (HttpClientErrorException e) {
            throwJiraErrors(e.getStatusCode().value());
        }
        return issueTypeStatisticsDTOS;
    }

    private List<IssueTypeStatisticsDTO> getIssueTypeStatisticsDTOS(TbpRequestBodyDTO requestBodyDTO) {
        List<IssueTypeStatisticsDTO> issueTypeStatisticsDTOS;
        if (requestBodyDTO.getIdTbp() == null || requestBodyDTO.getIdTbp().isEmpty()) {
            issueTypeStatisticsDTOS = jiraStatisticsService.getIssueTypeWorkloged(collaboratorService.findAll(), requestBodyDTO);
        }else{
            issueTypeStatisticsDTOS = jiraStatisticsService.getIssueTypeWorkloged(collaboratorService.findByTeamIdTbp(requestBodyDTO.getIdTbp()), requestBodyDTO);
        }
        return issueTypeStatisticsDTOS;
    }
}
