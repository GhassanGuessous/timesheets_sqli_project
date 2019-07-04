package com.sqli.imputation.web.rest;
import com.sqli.imputation.domain.Correspondence;
import com.sqli.imputation.service.CorrespondenceService;
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
 * REST controller for managing Correspondence.
 */
@RestController
@RequestMapping("/api")
public class CorrespondenceResource {

    private final Logger log = LoggerFactory.getLogger(CorrespondenceResource.class);

    private static final String ENTITY_NAME = "correspondence";

    private final CorrespondenceService correspondenceService;

    public CorrespondenceResource(CorrespondenceService correspondenceService) {
        this.correspondenceService = correspondenceService;
    }

    /**
     * POST  /correspondences : Create a new correspondence.
     *
     * @param correspondence the correspondence to create
     * @return the ResponseEntity with status 201 (Created) and with body the new correspondence, or with status 400 (Bad Request) if the correspondence has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/correspondences")
    public ResponseEntity<Correspondence> createCorrespondence(@RequestBody Correspondence correspondence) throws URISyntaxException {
        log.debug("REST request to save Correspondence : {}", correspondence);
        if (correspondence.getId() != null) {
            throw new BadRequestAlertException("A new correspondence cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Correspondence result = correspondenceService.save(correspondence);
        return ResponseEntity.created(new URI("/api/correspondences/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /correspondences : Updates an existing correspondence.
     *
     * @param correspondence the correspondence to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated correspondence,
     * or with status 400 (Bad Request) if the correspondence is not valid,
     * or with status 500 (Internal Server Error) if the correspondence couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/correspondences")
    public ResponseEntity<Correspondence> updateCorrespondence(@RequestBody Correspondence correspondence) throws URISyntaxException {
        log.debug("REST request to update Correspondence : {}", correspondence);
        if (correspondence.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Correspondence result = correspondenceService.save(correspondence);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, correspondence.getId().toString()))
            .body(result);
    }

    /**
     * GET  /correspondences : get all the correspondences.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of correspondences in body
     */
    @GetMapping("/correspondences")
    public ResponseEntity<List<Correspondence>> getAllCorrespondences(Pageable pageable) {
        log.debug("REST request to get a page of Correspondences");
        Page<Correspondence> page = correspondenceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/correspondences");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /correspondences/search : get all the correspondences with key.
     *
     * @param key the key to base searching on
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of correspondences in body
     */
    @GetMapping("/correspondences/search/{key}")
    public ResponseEntity<List<Correspondence>> getAllCorrespondences(@PathVariable String key, Pageable pageable) {
        log.debug("REST request to get a page of Correspondences with key");
        Page<Correspondence> page = correspondenceService.findByKey(key, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/correspondences");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /correspondences/:id : get the "id" correspondence.
     *
     * @param id the id of the correspondence to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the correspondence, or with status 404 (Not Found)
     */
    @GetMapping("/correspondences/{id}")
    public ResponseEntity<Correspondence> getCorrespondence(@PathVariable Long id) {
        log.debug("REST request to get Correspondence : {}", id);
        Optional<Correspondence> correspondence = correspondenceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(correspondence);
    }

    /**
     * DELETE  /correspondences/:id : delete the "id" correspondence.
     *
     * @param id the id of the correspondence to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/correspondences/{id}")
    public ResponseEntity<Void> deleteCorrespondence(@PathVariable Long id) {
        log.debug("REST request to delete Correspondence : {}", id);
        correspondenceService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
