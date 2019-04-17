package com.sqli.imputation.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqli.imputation.domain.Imputation;
import com.sqli.imputation.service.ImputationService;
import com.sqli.imputation.service.dto.AppRequestDTO;
import com.sqli.imputation.service.dto.ImputationComparatorDTO;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.impl.FilePPMCStorageService;
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
import sun.invoke.empty.Empty;

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

    public static final String AN_EMPTY_STRING = "";
    private final Logger log = LoggerFactory.getLogger(ImputationResource.class);

    private static final String ENTITY_NAME = "imputation";

    private final ImputationService imputationService;

    @Autowired
    FilePPMCStorageService storageService;

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
        if (appRequestDTO.getAgresso().equals(AN_EMPTY_STRING)){
            throw new BadRequestAlertException("Project is required", ENTITY_NAME, "projectnull");
        } else{
            List<Imputation> imputations = imputationService.getAppImputation(appRequestDTO);
            return ResponseEntity.ok().body(imputations);
        }
    }

    @PostMapping("/imputations/tbp")
    public ResponseEntity<List<Imputation>> getTbpImputation(@RequestBody TbpRequestBodyDTO tbpRequestBodyDTO) {
        log.debug("REST request to get Imputation charge given a team and a date : {}", tbpRequestBodyDTO);

        String startDate = tbpRequestBodyDTO.getStartDate();
        String endDate = tbpRequestBodyDTO.getEndDate();

        if (tbpRequestBodyDTO.getIdTbp() == null) {
            throw new BadRequestAlertException("Project is required", ENTITY_NAME, "projectnull");
        } else if (startDate.equals(AN_EMPTY_STRING) || endDate.equals(AN_EMPTY_STRING)) {
            throw new BadRequestAlertException("Both start date & end date are required", ENTITY_NAME, "datenull");
        } else if(DateUtil.isDatesOrderNotValid(startDate, endDate)){
            throw new BadRequestAlertException("End date should be greater than started date", ENTITY_NAME, "orderdates");
        } else if(DateUtil.isDifferentYears(startDate, endDate)){
            throw new BadRequestAlertException("Different years", ENTITY_NAME, "different_years");
        } else {
            List<Imputation> imputations = imputationService.getTbpImputation(tbpRequestBodyDTO);
            return ResponseEntity.ok().body(imputations);
        }
    }

    @PostMapping("/imputations/ppmc")
    public ResponseEntity<Optional<Imputation>> getPPMCImputation(@RequestParam("file") MultipartFile file) {
        String extension = FileExtensionUtil.getExtension(file.getOriginalFilename());
        if (FileExtensionUtil.isNotValidExcelExtension(extension)) {
            throw new BadRequestAlertException("File type not supported", ENTITY_NAME, "extension_support");
        } else {
            Optional<Imputation> imputation = imputationService.getPpmcImputation(file);
            if(imputation.isPresent()) return ResponseEntity.ok().body(imputation);
            else throw new BadRequestAlertException("Invalid PPMC file", ENTITY_NAME, "invalidPPMC");
        }
    }

    @PostMapping("/imputations/compare-app-ppmc")
    public ResponseEntity<List<ImputationComparatorDTO>> get_app_ppmc_comparison(
        @RequestParam("file") MultipartFile file, @RequestParam("appRequestBody") String requestDTO
    ) throws IOException {
        AppRequestDTO appRequestDTO = JsonUtil.getAppRequestDTO(requestDTO);
        String extension = FileExtensionUtil.getExtension(file.getOriginalFilename());
        if (appRequestDTO.getAgresso().equals(AN_EMPTY_STRING)){
            throw new BadRequestAlertException("Project is required", ENTITY_NAME, "projectnull");
        } else if (FileExtensionUtil.isNotValidExcelExtension(extension)) {
            throw new BadRequestAlertException("File type not supported", ENTITY_NAME, "extension_support");
        } else {
            List<ImputationComparatorDTO> comparatorDTOS = imputationService.compare_app_ppmc(file, appRequestDTO);
            if(comparatorDTOS.isEmpty()) {
                throw new BadRequestAlertException("Invalid PPMC file", ENTITY_NAME, "invalidPPMC");
            }
            return ResponseEntity.ok().body(comparatorDTOS);
        }
    }
}
