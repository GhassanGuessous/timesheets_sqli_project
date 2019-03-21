package com.sqli.imputation.web.rest;
import com.sqli.imputation.domain.ImputationType;
import com.sqli.imputation.service.ImputationTypeService;
import com.sqli.imputation.web.rest.errors.BadRequestAlertException;
import com.sqli.imputation.web.rest.util.HeaderUtil;
import com.sqli.imputation.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ImputationType.
 */
@RestController
@RequestMapping("/api")
public class ImputationTypeResource {

    private final Logger log = LoggerFactory.getLogger(ImputationTypeResource.class);

    private static final String ENTITY_NAME = "imputationType";

    private final ImputationTypeService imputationTypeService;

    public ImputationTypeResource(ImputationTypeService imputationTypeService) {
        this.imputationTypeService = imputationTypeService;
    }

    /**
     * POST  /imputation-types : Create a new imputationType.
     *
     * @param imputationType the imputationType to create
     * @return the ResponseEntity with status 201 (Created) and with body the new imputationType, or with status 400 (Bad Request) if the imputationType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/imputation-types")
    public ResponseEntity<ImputationType> createImputationType(@RequestBody ImputationType imputationType) throws URISyntaxException {
        log.debug("REST request to save ImputationType : {}", imputationType);
        if (imputationType.getId() != null) {
            throw new BadRequestAlertException("A new imputationType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ImputationType result = imputationTypeService.save(imputationType);
        return ResponseEntity.created(new URI("/api/imputation-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /imputation-types : Updates an existing imputationType.
     *
     * @param imputationType the imputationType to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated imputationType,
     * or with status 400 (Bad Request) if the imputationType is not valid,
     * or with status 500 (Internal Server Error) if the imputationType couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/imputation-types")
    public ResponseEntity<ImputationType> updateImputationType(@RequestBody ImputationType imputationType) throws URISyntaxException {
        log.debug("REST request to update ImputationType : {}", imputationType);
        if (imputationType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ImputationType result = imputationTypeService.save(imputationType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, imputationType.getId().toString()))
            .body(result);
    }

    /**
     * GET  /imputation-types : get all the imputationTypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of imputationTypes in body
     */
    @GetMapping("/imputation-types")
    public ResponseEntity<List<ImputationType>> getAllImputationTypes(Pageable pageable) {
        log.debug("REST request to get a page of ImputationTypes");
        Page<ImputationType> page = imputationTypeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/imputation-types");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /imputation-types/:id : get the "id" imputationType.
     *
     * @param id the id of the imputationType to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the imputationType, or with status 404 (Not Found)
     */
    @GetMapping("/imputation-types/{id}")
    public ResponseEntity<ImputationType> getImputationType(@PathVariable Long id) {
        log.debug("REST request to get ImputationType : {}", id);
        Optional<ImputationType> imputationType = imputationTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(imputationType);
    }

    /**
     * DELETE  /imputation-types/:id : delete the "id" imputationType.
     *
     * @param id the id of the imputationType to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/imputation-types/{id}")
    public ResponseEntity<Void> deleteImputationType(@PathVariable Long id) {
        log.debug("REST request to delete ImputationType : {}", id);
        imputationTypeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
