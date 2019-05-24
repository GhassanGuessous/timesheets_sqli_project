package com.sqli.imputation.web.rest;
import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.CollaboratorService;
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
 * REST controller for managing Collaborator.
 */
@RestController
@RequestMapping("/api")
public class CollaboratorResource {

    private final Logger log = LoggerFactory.getLogger(CollaboratorResource.class);

    private static final String ENTITY_NAME = "collaborator";

    private final CollaboratorService collaboratorService;

    public CollaboratorResource(CollaboratorService collaboratorService) {
        this.collaboratorService = collaboratorService;
    }

    /**
     * POST  /collaborators : Create a new collaborator.
     *
     * @param collaborator the collaborator to create
     * @return the ResponseEntity with status 201 (Created) and with body the new collaborator, or with status 400 (Bad Request) if the collaborator has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/collaborators")
    public ResponseEntity<Collaborator> createCollaborator(@RequestBody Collaborator collaborator) throws URISyntaxException {
        log.debug("REST request to save Collaborator : {}", collaborator);
        if (collaborator.getId() != null) {
            throw new BadRequestAlertException("A new collaborator cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Collaborator result = collaboratorService.save(collaborator);
        return ResponseEntity.created(new URI("/api/collaborators/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /collaborators : Updates an existing collaborator.
     *
     * @param collaborator the collaborator to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated collaborator,
     * or with status 400 (Bad Request) if the collaborator is not valid,
     * or with status 500 (Internal Server Error) if the collaborator couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/collaborators")
    public ResponseEntity<Collaborator> updateCollaborator(@RequestBody Collaborator collaborator) throws URISyntaxException {
        log.debug("REST request to update Collaborator : {}", collaborator);
        if (collaborator.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Collaborator result = collaboratorService.save(collaborator);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, collaborator.getId().toString()))
            .body(result);
    }

    /**
     * GET  /collaborators : get all the collaborators.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of collaborators in body
     */
    @GetMapping("/collaborators")
    public ResponseEntity<List<Collaborator>> getAllCollaborators(Pageable pageable) {
        log.debug("REST request to get a page of Collaborators");
        Page<Collaborator> page = collaboratorService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/collaborators");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /collaborators : get all the collaborators without pagination.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of collaborators in body
     */
    @GetMapping("/all-collaborators")
    public ResponseEntity<List<Collaborator>> getAllCollaborators() {
        log.debug("REST request to get a page of Collaborators");
        List<Collaborator> collaborators = collaboratorService.findAll();
        return ResponseEntity.ok().body(collaborators);
    }

    /**
     * GET  /collaborators : get all the collaborators without correspendence.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of collaborators in body
     */
    @GetMapping("/collaborators-no-correspondence")
    public ResponseEntity<List<Collaborator>> getAllWithNoCorrespondence() {
        log.debug("REST request to get a page of Collaborators");
        List<Collaborator> collaborators = collaboratorService.getAllWithNoCorrespondence();
        return ResponseEntity.ok().body(collaborators);
    }

    /**
     * GET  /collaborators/search/key : get the collaborators with a key.
     *
     * @param key the key to base searching on
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of collaborators in body
     */
    @GetMapping("/collaborators/search/{key}")
    public ResponseEntity<List<Collaborator>> getCollaboratorsByKey(@PathVariable String key, Pageable pageable) {
        log.debug("REST request to get a page of Collaborators with key");
        Page<Collaborator> page = collaboratorService.findByKey(key, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/collaborators");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /collaborators/:id : get the "id" collaborator.
     *
     * @param id the id of the collaborator to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the collaborator, or with status 404 (Not Found)
     */
    @GetMapping("/collaborators/{id}")
    public ResponseEntity<Collaborator> getCollaborator(@PathVariable Long id) {
        log.debug("REST request to get Collaborator : {}", id);
        Optional<Collaborator> collaborator = collaboratorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(collaborator);
    }

    /**
     * DELETE  /collaborators/:id : delete the "id" collaborator.
     *
     * @param id the id of the collaborator to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/collaborators/{id}")
    public ResponseEntity<Void> deleteCollaborator(@PathVariable Long id) {
        log.debug("REST request to delete Collaborator : {}", id);
        collaboratorService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
