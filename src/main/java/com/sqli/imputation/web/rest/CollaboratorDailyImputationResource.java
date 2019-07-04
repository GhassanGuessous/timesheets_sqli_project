package com.sqli.imputation.web.rest;
import com.sqli.imputation.domain.CollaboratorDailyImputation;
import com.sqli.imputation.service.CollaboratorDailyImputationService;
import com.sqli.imputation.web.rest.errors.BadRequestAlertException;
import com.sqli.imputation.web.rest.util.HeaderUtil;
import com.sqli.imputation.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing CollaboratorDailyImputation.
 */
@RestController
@RequestMapping("/api")
public class CollaboratorDailyImputationResource {

    private final Logger log = LoggerFactory.getLogger(CollaboratorDailyImputationResource.class);

    private static final String ENTITY_NAME = "collaboratorDailyImputation";

    private final CollaboratorDailyImputationService collaboratorDailyImputationService;

    public CollaboratorDailyImputationResource(CollaboratorDailyImputationService collaboratorDailyImputationService) {
        this.collaboratorDailyImputationService = collaboratorDailyImputationService;
    }

    /**
     * POST  /collaborator-daily-imputations : Create a new collaboratorDailyImputation.
     *
     * @param collaboratorDailyImputation the collaboratorDailyImputation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new collaboratorDailyImputation, or with status 400 (Bad Request) if the collaboratorDailyImputation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/collaborator-daily-imputations")
    public ResponseEntity<CollaboratorDailyImputation> createCollaboratorDailyImputation(@RequestBody CollaboratorDailyImputation collaboratorDailyImputation) throws URISyntaxException {
        log.debug("REST request to save CollaboratorDailyImputation : {}", collaboratorDailyImputation);
        if (collaboratorDailyImputation.getId() != null) {
            throw new BadRequestAlertException("A new collaboratorDailyImputation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CollaboratorDailyImputation result = collaboratorDailyImputationService.save(collaboratorDailyImputation);
        return ResponseEntity.created(new URI("/api/collaborator-daily-imputations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /collaborator-daily-imputations : Updates an existing collaboratorDailyImputation.
     *
     * @param collaboratorDailyImputation the collaboratorDailyImputation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated collaboratorDailyImputation,
     * or with status 400 (Bad Request) if the collaboratorDailyImputation is not valid,
     * or with status 500 (Internal Server Error) if the collaboratorDailyImputation couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/collaborator-daily-imputations")
    public ResponseEntity<CollaboratorDailyImputation> updateCollaboratorDailyImputation(@RequestBody CollaboratorDailyImputation collaboratorDailyImputation) throws URISyntaxException {
        log.debug("REST request to update CollaboratorDailyImputation : {}", collaboratorDailyImputation);
        if (collaboratorDailyImputation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CollaboratorDailyImputation result = collaboratorDailyImputationService.save(collaboratorDailyImputation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, collaboratorDailyImputation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /collaborator-daily-imputations : get all the collaboratorDailyImputations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of collaboratorDailyImputations in body
     */
    @GetMapping("/collaborator-daily-imputations")
    public ResponseEntity<List<CollaboratorDailyImputation>> getAllCollaboratorDailyImputations(Pageable pageable) {
        log.debug("REST request to get a page of CollaboratorDailyImputations");
        Page<CollaboratorDailyImputation> page = collaboratorDailyImputationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/collaborator-daily-imputations");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /collaborator-daily-imputations/:id : get the "id" collaboratorDailyImputation.
     *
     * @param id the id of the collaboratorDailyImputation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the collaboratorDailyImputation, or with status 404 (Not Found)
     */
    @GetMapping("/collaborator-daily-imputations/{id}")
    public ResponseEntity<CollaboratorDailyImputation> getCollaboratorDailyImputation(@PathVariable Long id) {
        log.debug("REST request to get CollaboratorDailyImputation : {}", id);
        Optional<CollaboratorDailyImputation> collaboratorDailyImputation = collaboratorDailyImputationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(collaboratorDailyImputation);
    }

    /**
     * DELETE  /collaborator-daily-imputations/:id : delete the "id" collaboratorDailyImputation.
     *
     * @param id the id of the collaboratorDailyImputation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/collaborator-daily-imputations/{id}")
    public ResponseEntity<Void> deleteCollaboratorDailyImputation(@PathVariable Long id) {
        log.debug("REST request to delete CollaboratorDailyImputation : {}", id);
        collaboratorDailyImputationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
