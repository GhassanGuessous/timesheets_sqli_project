package com.sqli.imputation.web.rest;
import com.sqli.imputation.domain.ProjectType;
import com.sqli.imputation.service.ProjectTypeService;
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
 * REST controller for managing ProjectType.
 */
@RestController
@RequestMapping("/api")
public class ProjectTypeResource {

    private final Logger log = LoggerFactory.getLogger(ProjectTypeResource.class);

    private static final String ENTITY_NAME = "projectType";

    private final ProjectTypeService projectTypeService;

    public ProjectTypeResource(ProjectTypeService projectTypeService) {
        this.projectTypeService = projectTypeService;
    }

    /**
     * POST  /project-types : Create a new projectType.
     *
     * @param projectType the projectType to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectType, or with status 400 (Bad Request) if the projectType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/project-types")
    public ResponseEntity<ProjectType> createProjectType(@RequestBody ProjectType projectType) throws URISyntaxException {
        log.debug("REST request to save ProjectType : {}", projectType);
        if (projectType.getId() != null) {
            throw new BadRequestAlertException("A new projectType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProjectType result = projectTypeService.save(projectType);
        return ResponseEntity.created(new URI("/api/project-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /project-types : Updates an existing projectType.
     *
     * @param projectType the projectType to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectType,
     * or with status 400 (Bad Request) if the projectType is not valid,
     * or with status 500 (Internal Server Error) if the projectType couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/project-types")
    public ResponseEntity<ProjectType> updateProjectType(@RequestBody ProjectType projectType) throws URISyntaxException {
        log.debug("REST request to update ProjectType : {}", projectType);
        if (projectType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ProjectType result = projectTypeService.save(projectType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, projectType.getId().toString()))
            .body(result);
    }

    /**
     * GET  /project-types : get all the projectTypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of projectTypes in body
     */
    @GetMapping("/project-types")
    public ResponseEntity<List<ProjectType>> getAllProjectTypes(Pageable pageable) {
        log.debug("REST request to get a page of ProjectTypes");
        Page<ProjectType> page = projectTypeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-types");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /project-types/:id : get the "id" projectType.
     *
     * @param id the id of the projectType to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectType, or with status 404 (Not Found)
     */
    @GetMapping("/project-types/{id}")
    public ResponseEntity<ProjectType> getProjectType(@PathVariable Long id) {
        log.debug("REST request to get ProjectType : {}", id);
        Optional<ProjectType> projectType = projectTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectType);
    }

    /**
     * DELETE  /project-types/:id : delete the "id" projectType.
     *
     * @param id the id of the projectType to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-types/{id}")
    public ResponseEntity<Void> deleteProjectType(@PathVariable Long id) {
        log.debug("REST request to delete ProjectType : {}", id);
        projectTypeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
