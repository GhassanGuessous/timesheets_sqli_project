package com.sqli.imputation.web.rest;

import com.sqli.imputation.config.Constants;
import com.sqli.imputation.domain.Imputation;
import com.sqli.imputation.service.ImputationService;
import com.sqli.imputation.service.NotificationService;
import com.sqli.imputation.service.dto.*;
import com.sqli.imputation.service.dto.jira.IssueTypeStatisticsDTO;
import com.sqli.imputation.service.dto.jira.JiraImputationDTO;
import com.sqli.imputation.service.dto.jira.PpmcProjectWorklogDTO;
import com.sqli.imputation.service.util.DateUtil;
import com.sqli.imputation.service.util.FileExtensionUtil;
import com.sqli.imputation.service.util.JsonUtil;
import com.sqli.imputation.web.rest.errors.BadRequestAlertException;
import com.sqli.imputation.web.rest.util.HeaderUtil;
import com.sqli.imputation.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.*;

/**
 * REST controller for managing Imputation.
 */
@RestController
@RequestMapping("/api")
public class ImputationResource {

    private final Logger log = LoggerFactory.getLogger(ImputationResource.class);

    private static final String AN_EMPTY_STRING = "";
    private static final String UPLOAD_A_PPMC_FILE_MESSAGE = "upload a ppmc file";
    private static final String NEW_UPLOAD = "newUpload";
    private static final String PROJECT_IS_REQUIRED = "Project is required";
    private static final String PROJECT_IS_NULL = "projectnull";
    private static final String ENTITY_NAME = "imputation";

    private final ImputationService imputationService;
    @Autowired
    private NotificationService notificationService;

    public ImputationResource(ImputationService imputationService) {
        this.imputationService = imputationService;
    }

    /**
     * POST  /imputations : Create a new imputation.
     *
     * @param imputation the imputation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new imputation, or with status 400 (Bad Request) if the imputation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/imputations")
    public ResponseEntity<Imputation> createImputation(@RequestBody Imputation imputation) throws URISyntaxException {
        log.debug("REST request to save Imputation : {}", imputation);
        if (imputation.getId() != null) {
            throw new BadRequestAlertException("A new imputation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Imputation result = imputationService.save(imputation);
        return ResponseEntity.created(new URI("/api/imputations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /imputations : Updates an existing imputation.
     *
     * @param imputation the imputation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated imputation,
     * or with status 400 (Bad Request) if the imputation is not valid,
     * or with status 500 (Internal Server Error) if the imputation couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/imputations")
    public ResponseEntity<Imputation> updateImputation(@RequestBody Imputation imputation) throws URISyntaxException {
        log.debug("REST request to update Imputation : {}", imputation);
        if (imputation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Imputation result = imputationService.save(imputation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, imputation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /imputations : get all the imputations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of imputations in body
     */
    @GetMapping("/imputations")
    public ResponseEntity<List<Imputation>> getAllImputations(Pageable pageable) {
        log.debug("REST request to get a page of Imputations");
        Page<Imputation> page = imputationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/imputations");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /imputations/:id : get the "id" imputation.
     *
     * @param id the id of the imputation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the imputation, or with status 404 (Not Found)
     */
    @GetMapping("/imputations/{id}")
    public ResponseEntity<Imputation> getImputation(@PathVariable Long id) {
        log.debug("REST request to get Imputation : {}", id);
        Optional<Imputation> imputation = imputationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(imputation);
    }

    /**
     * DELETE  /imputations/:id : delete the "id" imputation.
     *
     * @param id the id of the imputation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/imputations/{id}")
    public ResponseEntity<Void> deleteImputation(@PathVariable Long id) {
        log.debug("REST request to delete Imputation : {}", id);
        imputationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * POST  /imputations/app : get app imputation from webservice.
     *
     * @param appRequestDTO the app imputation request
     * @return the ResponseEntity with imputation of type APP
     */
    @PostMapping("/imputations/app")
    public ResponseEntity<List<Imputation>> getAppImputation(@RequestBody AppRequestDTO appRequestDTO) {
        log.debug("REST request to get APP Imputation : {}", appRequestDTO);
        if (appRequestDTO.getAgresso().equals(AN_EMPTY_STRING)) {
            throw new BadRequestAlertException(PROJECT_IS_REQUIRED, ENTITY_NAME, PROJECT_IS_NULL);
        } else {
            List<Imputation> imputations = imputationService.getAppImputation(appRequestDTO);
            return ResponseEntity.ok().body(imputations);
        }
    }

    /**
     * POST  /imputations/tbp : get tbp imputation from webservice.
     *
     * @param tbpRequestBodyDTO
     * @return
     */
    @PostMapping("/imputations/tbp")
    public ResponseEntity<List<Imputation>> getTbpImputation(@RequestBody TbpRequestBodyDTO tbpRequestBodyDTO) {
        log.debug("REST request to get Imputation charge given a team and a date : {}", tbpRequestBodyDTO);

        String startDate = tbpRequestBodyDTO.getStartDate();
        String endDate = tbpRequestBodyDTO.getEndDate();

        if (tbpRequestBodyDTO.getIdTbp() == null) {
            throw new BadRequestAlertException(PROJECT_IS_REQUIRED, ENTITY_NAME, PROJECT_IS_NULL);
        } else if (DateUtil.isNotValidDates(startDate, endDate)) {
            throw new BadRequestAlertException("Both start date & end date are required", ENTITY_NAME, "datenull");
        } else if (DateUtil.isDatesOrderNotValid(startDate, endDate)) {
            throw new BadRequestAlertException("End date should be greater than started date", ENTITY_NAME, "orderdates");
        } else if (DateUtil.isDifferentYears(startDate, endDate)) {
            throw new BadRequestAlertException("Different years", ENTITY_NAME, "different_years");
        } else if (isNotValidTBPCredentials(tbpRequestBodyDTO.getUsername(), tbpRequestBodyDTO.getPassword())) {
            throw new BadRequestAlertException("Tbp invalid inputs", ENTITY_NAME, "tbp_invalid_inputs");
        } else {
            List<Imputation> imputations = imputationService.getTbpImputation(tbpRequestBodyDTO);
            return ResponseEntity.ok().body(imputations);
        }
    }

    private boolean isNotValidTBPCredentials(String username, String password) {
        return (username == null || password == null) || (username.isEmpty() || password.isEmpty());
    }

    @PostMapping("/imputations/ppmc")
    public ResponseEntity<Optional<Imputation>> getPPMCImputation(
        @RequestParam("file") MultipartFile file, @RequestParam("appRequestBody") String requestDTO
    ) throws IOException {
        AppRequestDTO appRequestDTO = JsonUtil.getAppRequestDTO(requestDTO);
        String extension = FileExtensionUtil.getExtension(file.getOriginalFilename());

        if (appRequestDTO.getAgresso().equals(AN_EMPTY_STRING)) {
            throw new BadRequestAlertException(PROJECT_IS_REQUIRED, ENTITY_NAME, PROJECT_IS_NULL);
        } else if (FileExtensionUtil.isNotValidExcelExtension(extension)) {
            throw new BadRequestAlertException("File type not supported", ENTITY_NAME, "extension_support");
        } else {
            Optional<Imputation> ppmcImputation = imputationService.getPpmcImputation(appRequestDTO.getAgresso(), file);
            if (!ppmcImputation.isPresent()) {
                throw new BadRequestAlertException("Invalid PPMC file", ENTITY_NAME, "invalidPPMC");
            }
            return ResponseEntity.ok().body(ppmcImputation);
        }
    }

    @PostMapping("/imputations/ppmc-database")
    public ResponseEntity<Optional<Imputation>> getPPMCImputationFromDB(@RequestBody AppRequestDTO appRequestDTO) {
        if (appRequestDTO.getAgresso().equals(AN_EMPTY_STRING)) {
            throw new BadRequestAlertException(PROJECT_IS_REQUIRED, ENTITY_NAME, PROJECT_IS_NULL);
        } else {
            ImputationRequestDTO imputationRequestDTO = new ImputationRequestDTO(appRequestDTO.getAgresso(), appRequestDTO.getYear(), appRequestDTO.getMonth(), Constants.PPMC_IMPUTATION_TYPE);
            Optional<Imputation> ppmcImputation = imputationService.findByTeam(imputationRequestDTO);
            if (!ppmcImputation.isPresent()) {
                throw new BadRequestAlertException(UPLOAD_A_PPMC_FILE_MESSAGE, ENTITY_NAME, NEW_UPLOAD);
            }
            return ResponseEntity.ok().body(ppmcImputation);
        }
    }

    /**
     * POST  /imputations/jira : get jira imputation from webservice.
     *
     * @param requestBodyDTO the JIRA imputation request
     * @return the ResponseEntity with imputation of type JIRA
     */
    @PostMapping("/imputations/jira")
    public ResponseEntity<List<JiraImputationDTO>> getJiraImputation(@RequestBody TbpRequestBodyDTO requestBodyDTO) {
        log.debug("REST request to get JIRA Imputation : {}", requestBodyDTO);

        String startDate = requestBodyDTO.getStartDate();
        String endDate = requestBodyDTO.getEndDate();

        if (requestBodyDTO.getIdTbp() == null) {
            throw new BadRequestAlertException(PROJECT_IS_REQUIRED, ENTITY_NAME, PROJECT_IS_NULL);
        } else if (DateUtil.isNotValidDates(startDate, endDate)) {
            throw new BadRequestAlertException("Both start date & end date are required", ENTITY_NAME, "datenull");
        } else if (DateUtil.isDatesOrderNotValid(startDate, endDate)) {
            throw new BadRequestAlertException("End date should be greater than started date", ENTITY_NAME, "orderdates");
        } else if (isNotValidTBPCredentials(requestBodyDTO.getUsername(), requestBodyDTO.getPassword())) {
            throw new BadRequestAlertException("Tbp invalid inputs", ENTITY_NAME, "tbp_invalid_inputs");
        } else {
            List<JiraImputationDTO> jiraImputations = imputationService.getJiraImputation(requestBodyDTO);
            return ResponseEntity.ok().body(jiraImputations);
        }
    }

    /**
     * POST  /imputations/compare-app-tbp : Basic comparison of APP and TBP imputations.
     *
     * @param appTbpRequest the comparison request
     * @return the ResponseEntity with compared imputations of type APP
     */
    @PostMapping("/imputations/compare-app-tbp")
    public ResponseEntity<List<ImputationComparatorDTO>> compareAppAndTbpImputations(@RequestBody AppTbpRequestBodyDTO appTbpRequest) {
        log.debug("REST request to get APP - TBP Imputation : {}", appTbpRequest);
        if (appTbpRequest.getTeam() == null) {
            throw new BadRequestAlertException(PROJECT_IS_REQUIRED, ENTITY_NAME, PROJECT_IS_NULL);
        } else if (isNotValidTBPCredentials(appTbpRequest.getUsername(), appTbpRequest.getPassword())) {
            throw new BadRequestAlertException("Tbp invalid inputs", ENTITY_NAME, "tbp_invalid_inputs");
        } else {
            List<ImputationComparatorDTO> comparatorDTOS = imputationService.compareAppAndTbp(appTbpRequest);
            return ResponseEntity.ok().body(comparatorDTOS);
        }
    }

    /**
     * POST  /imputations/compare-app-tbp-advanced : Advanced comparison of APP and TBP imputations.
     *
     * @param appTbpRequest the comparison request
     * @return the ResponseEntity with compared imputations of type APP
     */
    @PostMapping("/imputations/compare-app-tbp-advanced")
    public ResponseEntity<List<ImputationComparatorAdvancedDTO>> compareAppAndTbpImputationsAdvanced(@RequestBody AppTbpRequestBodyDTO appTbpRequest) {
        log.debug("REST request to get APP - TBP Imputation : {}", appTbpRequest);
        if (appTbpRequest.getTeam() == null) {
            throw new BadRequestAlertException(PROJECT_IS_REQUIRED, ENTITY_NAME, PROJECT_IS_NULL);
        } else if (isNotValidTBPCredentials(appTbpRequest.getUsername(), appTbpRequest.getPassword())) {
            throw new BadRequestAlertException("Tbp invalid inputs", ENTITY_NAME, "tbp_invalid_inputs");
        } else {
            List<ImputationComparatorAdvancedDTO> comparatorDTOS = imputationService.compareAppAndTbpAdvanced(appTbpRequest);
            return ResponseEntity.ok().body(comparatorDTOS);
        }
    }

    /**
     * POST  /imputations/compare-app-ppmc : Basic comparison of APP and PPMC imputations.
     *
     * @param file
     * @param requestDTO
     * @return
     * @throws IOException
     */
    @PostMapping("/imputations/compare-app-ppmc")
    public ResponseEntity<List<ImputationComparatorDTO>> getAppPpmcComparison(
        @RequestParam("file") MultipartFile file, @RequestParam("appRequestBody") String requestDTO
    ) throws IOException {
        return getAppPpmcResponseEntity(file, requestDTO, false);
    }

    /**
     * POST  /imputations/compare-app-ppmc-advanced : Advanced comparison of APP and PPMC imputations.
     *
     * @param file
     * @param requestDTO
     * @return
     * @throws IOException
     */
    @PostMapping("/imputations/compare-app-ppmc-advanced")
    public ResponseEntity<List<ImputationComparatorAdvancedDTO>> getAdvancedAppPpmcComparison(
        @RequestParam("file") MultipartFile file, @RequestParam("appRequestBody") String requestDTO
    ) throws IOException {
        return getAppPpmcResponseEntity(file, requestDTO, true);
    }

    private <T> ResponseEntity<List<T>> getAppPpmcResponseEntity(MultipartFile file, String requestDTO, boolean isAdvanced) throws IOException {
        AppRequestDTO appRequestDTO = JsonUtil.getAppRequestDTO(requestDTO);
        String extension = FileExtensionUtil.getExtension(file.getOriginalFilename());
        if (appRequestDTO.getAgresso().equals(AN_EMPTY_STRING)) {
            throw new BadRequestAlertException(PROJECT_IS_REQUIRED, ENTITY_NAME, PROJECT_IS_NULL);
        } else if (FileExtensionUtil.isNotValidExcelExtension(extension)) {
            throw new BadRequestAlertException("File type not supported", ENTITY_NAME, "extension_support");
        } else {
            return compareAppPpmc(file, appRequestDTO, isAdvanced);
        }
    }

    private <T> ResponseEntity<List<T>> compareAppPpmc(MultipartFile file, AppRequestDTO appRequestDTO, boolean isAdvanced) {
        List<T> comparatorDTOS = isAdvanced ?
            (List<T>) imputationService.compareAppPpmcAdvanced(file, appRequestDTO) : (List<T>) imputationService.compareAppPpmc(file, appRequestDTO);
        return ResponseEntity.ok().body(comparatorDTOS);
    }

    /**
     * POST  /imputations/comparison-app-ppmc-database : comparison of APP and PPMC imputations from DB.
     *
     * @param appRequestDTO
     * @return
     */
    @PostMapping("/imputations/comparison-app-ppmc-database")
    public ResponseEntity<List<ImputationComparatorDTO>> getAppPpmcComparisonFromDB(@RequestBody AppRequestDTO appRequestDTO) {
        if (appRequestDTO.getAgresso().equals(AN_EMPTY_STRING)) {
            throw new BadRequestAlertException(PROJECT_IS_REQUIRED, ENTITY_NAME, PROJECT_IS_NULL);
        } else {
            return getComparisonFromDB(appRequestDTO);
        }
    }

    private ResponseEntity<List<ImputationComparatorDTO>> getComparisonFromDB(AppRequestDTO appRequestDTO) {
        List<ImputationComparatorDTO> comparatorDTOS = imputationService.getComparisonFromDB(appRequestDTO, Constants.PPMC_IMPUTATION_TYPE);
        if (comparatorDTOS.isEmpty()) {
            throw new BadRequestAlertException(UPLOAD_A_PPMC_FILE_MESSAGE, ENTITY_NAME, NEW_UPLOAD);
        }
        return ResponseEntity.ok().body(comparatorDTOS);
    }

    /**
     * POST  /imputations/comparison-app-ppmc-advanced-database : Advanced comparison of APP and PPMC imputations from DB.
     *
     * @param appRequestDTO
     * @return
     */
    @PostMapping("/imputations/comparison-app-ppmc-advanced-database")
    public ResponseEntity<List<ImputationComparatorAdvancedDTO>> getAdvancedAppPpmcComparisonFromDB(@RequestBody AppRequestDTO appRequestDTO) {
        if (appRequestDTO.getAgresso().equals(AN_EMPTY_STRING)) {
            throw new BadRequestAlertException(PROJECT_IS_REQUIRED, ENTITY_NAME, PROJECT_IS_NULL);
        } else {
            return getAdvancedComparisonFromDB(appRequestDTO);
        }
    }

    private ResponseEntity<List<ImputationComparatorAdvancedDTO>> getAdvancedComparisonFromDB(AppRequestDTO appRequestDTO) {
        List<ImputationComparatorAdvancedDTO> advancedComparatorDTOS = imputationService.getAdvancedComparisonFromDB(appRequestDTO, Constants.PPMC_IMPUTATION_TYPE);
        if (advancedComparatorDTOS.isEmpty()) {
            throw new BadRequestAlertException(UPLOAD_A_PPMC_FILE_MESSAGE, ENTITY_NAME, NEW_UPLOAD);
        }
        return ResponseEntity.ok().body(advancedComparatorDTOS);
    }

    @PostMapping("/imputations/notify")
    public ResponseEntity<Void> sendNotificationsToCollabs(@RequestBody List<NotificationDTO> notifications) {
        imputationService.sendNotifications(notifications);
        return ResponseEntity.ok().headers(HeaderUtil.createEmailSendingAlert(ENTITY_NAME)).build();
    }

    @PostMapping("/imputations/statistics")
    public ResponseEntity<List<StatisticsDTO>> getTeamNotifications(@RequestBody TeamYearDTO teamYearDTO) {
        List<StatisticsDTO> statisticsDTOS = notificationService.getStatistics(teamYearDTO);
        return ResponseEntity.ok().body(statisticsDTOS);
    }

    @PostMapping("/imputations/gap-variation-statistics")
    public ResponseEntity<List<NotificationGapVariationDTO>> getTeamNotificationGapVariation(@RequestBody TeamYearDTO teamYearDTO) {
        List<NotificationGapVariationDTO> gapVariationDTOS = notificationService.getNotificationGapVariation(teamYearDTO);
        return ResponseEntity.ok().body(gapVariationDTOS);
    }

    @PostMapping("/imputations/ppmc-project-worklogs-statistics")
    public ResponseEntity<List<PpmcProjectWorklogDTO>> getPpmcProjectWorkloged(@RequestBody TbpRequestBodyDTO requestBodyDTO) {

        String startDate = requestBodyDTO.getStartDate();
        String endDate = requestBodyDTO.getEndDate();

        if (DateUtil.isNotValidDates(startDate, endDate)) {
            throw new BadRequestAlertException("Both start date & end date are required", ENTITY_NAME, "datenull");
        } else if (DateUtil.isDatesOrderNotValid(startDate, endDate)) {
            throw new BadRequestAlertException("End date should be greater than started date", ENTITY_NAME, "orderdates");
        } else if (isNotValidTBPCredentials(requestBodyDTO.getUsername(), requestBodyDTO.getPassword())) {
            throw new BadRequestAlertException("Tbp invalid inputs", ENTITY_NAME, "tbp_invalid_inputs");
        } else {
            log.debug("request to Get Ppmc Project Workloged");
            return ResponseEntity.ok().body(imputationService.getPpmcProjectWorkloged(requestBodyDTO));
        }
    }

    @PostMapping("/imputations/issue-type-statistics")
    public ResponseEntity<List<IssueTypeStatisticsDTO>> getIssueTypeStatistics(@RequestBody TbpRequestBodyDTO requestBodyDTO) {

        String startDate = requestBodyDTO.getStartDate();
        String endDate = requestBodyDTO.getEndDate();

        if (DateUtil.isNotValidDates(startDate, endDate)) {
            throw new BadRequestAlertException("Both start date & end date are required", ENTITY_NAME, "datenull");
        } else if (DateUtil.isDatesOrderNotValid(startDate, endDate)) {
            throw new BadRequestAlertException("End date should be greater than started date", ENTITY_NAME, "orderdates");
        } else if (isNotValidTBPCredentials(requestBodyDTO.getUsername(), requestBodyDTO.getPassword())) {
            throw new BadRequestAlertException("Tbp invalid inputs", ENTITY_NAME, "tbp_invalid_inputs");
        } else {
            log.debug("request to Get Ppmc Project Workloged");
            return ResponseEntity.ok().body(imputationService.getIssueTypeStatistics(requestBodyDTO));
        }
    }
}
