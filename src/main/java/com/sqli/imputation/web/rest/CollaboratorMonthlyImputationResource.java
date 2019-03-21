package com.sqli.imputation.web.rest;
import com.sqli.imputation.domain.CollaboratorMonthlyImputation;
import com.sqli.imputation.service.CollaboratorMonthlyImputationService;
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
 * REST controller for managing CollaboratorMonthlyImputation.
 */
@RestController
@RequestMapping("/api")
public class CollaboratorMonthlyImputationResource {

    private final Logger log = LoggerFactory.getLogger(CollaboratorMonthlyImputationResource.class);

    private static final String ENTITY_NAME = "collaboratorMonthlyImputation";

    private final CollaboratorMonthlyImputationService collaboratorMonthlyImputationService;

    public CollaboratorMonthlyImputationResource(CollaboratorMonthlyImputationService collaboratorMonthlyImputationService) {
        this.collaboratorMonthlyImputationService = collaboratorMonthlyImputationService;
    }

    /**
     * POST  /collaborator-monthly-imputations : Create a new collaboratorMonthlyImputation.
     *
     * @param collaboratorMonthlyImputation the collaboratorMonthlyImputation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new collaboratorMonthlyImputation, or with status 400 (Bad Request) if the collaboratorMonthlyImputation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/collaborator-monthly-imputations")
    public ResponseEntity<CollaboratorMonthlyImputation> createCollaboratorMonthlyImputation(@RequestBody CollaboratorMonthlyImputation collaboratorMonthlyImputation) throws URISyntaxException {
        log.debug("REST request to save CollaboratorMonthlyImputation : {}", collaboratorMonthlyImputation);
        if (collaboratorMonthlyImputation.getId() != null) {
            throw new BadRequestAlertException("A new collaboratorMonthlyImputation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CollaboratorMonthlyImputation result = collaboratorMonthlyImputationService.save(collaboratorMonthlyImputation);
        return ResponseEntity.created(new URI("/api/collaborator-monthly-imputations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /collaborator-monthly-imputations : Updates an existing collaboratorMonthlyImputation.
     *
     * @param collaboratorMonthlyImputation the collaboratorMonthlyImputation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated collaboratorMonthlyImputation,
     * or with status 400 (Bad Request) if the collaboratorMonthlyImputation is not valid,
     * or with status 500 (Internal Server Error) if the collaboratorMonthlyImputation couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/collaborator-monthly-imputations")
    public ResponseEntity<CollaboratorMonthlyImputation> updateCollaboratorMonthlyImputation(@RequestBody CollaboratorMonthlyImputation collaboratorMonthlyImputation) throws URISyntaxException {
        log.debug("REST request to update CollaboratorMonthlyImputation : {}", collaboratorMonthlyImputation);
        if (collaboratorMonthlyImputation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CollaboratorMonthlyImputation result = collaboratorMonthlyImputationService.save(collaboratorMonthlyImputation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, collaboratorMonthlyImputation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /collaborator-monthly-imputations : get all the collaboratorMonthlyImputations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of collaboratorMonthlyImputations in body
     */
    @GetMapping("/collaborator-monthly-imputations")
    public ResponseEntity<List<CollaboratorMonthlyImputation>> getAllCollaboratorMonthlyImputations(Pageable pageable) {
        log.debug("REST request to get a page of CollaboratorMonthlyImputations");
        Page<CollaboratorMonthlyImputation> page = collaboratorMonthlyImputationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/collaborator-monthly-imputations");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /collaborator-monthly-imputations/:id : get the "id" collaboratorMonthlyImputation.
     *
     * @param id the id of the collaboratorMonthlyImputation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the collaboratorMonthlyImputation, or with status 404 (Not Found)
     */
    @GetMapping("/collaborator-monthly-imputations/{id}")
    public ResponseEntity<CollaboratorMonthlyImputation> getCollaboratorMonthlyImputation(@PathVariable Long id) {
        log.debug("REST request to get CollaboratorMonthlyImputation : {}", id);
        Optional<CollaboratorMonthlyImputation> collaboratorMonthlyImputation = collaboratorMonthlyImputationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(collaboratorMonthlyImputation);
    }

    /**
     * DELETE  /collaborator-monthly-imputations/:id : delete the "id" collaboratorMonthlyImputation.
     *
     * @param id the id of the collaboratorMonthlyImputation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/collaborator-monthly-imputations/{id}")
    public ResponseEntity<Void> deleteCollaboratorMonthlyImputation(@PathVariable Long id) {
        log.debug("REST request to delete CollaboratorMonthlyImputation : {}", id);
        collaboratorMonthlyImputationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
