package com.sqli.imputation.web.rest;
import com.sqli.imputation.domain.DeliveryCoordinator;
import com.sqli.imputation.service.DeliveryCoordinatorService;
import com.sqli.imputation.web.rest.errors.BadRequestAlertException;
import com.sqli.imputation.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing DeliveryCoordinator.
 */
@RestController
@RequestMapping("/api")
public class DeliveryCoordinatorResource {

    private final Logger log = LoggerFactory.getLogger(DeliveryCoordinatorResource.class);

    private static final String ENTITY_NAME = "deliveryCoordinator";

    private final DeliveryCoordinatorService deliveryCoordinatorService;

    public DeliveryCoordinatorResource(DeliveryCoordinatorService deliveryCoordinatorService) {
        this.deliveryCoordinatorService = deliveryCoordinatorService;
    }

    /**
     * POST  /delivery-coordinators : Create a new deliveryCoordinator.
     *
     * @param deliveryCoordinator the deliveryCoordinator to create
     * @return the ResponseEntity with status 201 (Created) and with body the new deliveryCoordinator, or with status 400 (Bad Request) if the deliveryCoordinator has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/delivery-coordinators")
    public ResponseEntity<DeliveryCoordinator> createDeliveryCoordinator(@RequestBody DeliveryCoordinator deliveryCoordinator) throws URISyntaxException {
        log.debug("REST request to save DeliveryCoordinator : {}", deliveryCoordinator);
        if (deliveryCoordinator.getId() != null) {
            throw new BadRequestAlertException("A new deliveryCoordinator cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DeliveryCoordinator result = deliveryCoordinatorService.save(deliveryCoordinator);
        return ResponseEntity.created(new URI("/api/delivery-coordinators/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /delivery-coordinators : Updates an existing deliveryCoordinator.
     *
     * @param deliveryCoordinator the deliveryCoordinator to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated deliveryCoordinator,
     * or with status 400 (Bad Request) if the deliveryCoordinator is not valid,
     * or with status 500 (Internal Server Error) if the deliveryCoordinator couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/delivery-coordinators")
    public ResponseEntity<DeliveryCoordinator> updateDeliveryCoordinator(@RequestBody DeliveryCoordinator deliveryCoordinator) throws URISyntaxException {
        log.debug("REST request to update DeliveryCoordinator : {}", deliveryCoordinator);
        if (deliveryCoordinator.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        DeliveryCoordinator result = deliveryCoordinatorService.save(deliveryCoordinator);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, deliveryCoordinator.getId().toString()))
            .body(result);
    }

    /**
     * GET  /delivery-coordinators : get all the deliveryCoordinators.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of deliveryCoordinators in body
     */
    @GetMapping("/delivery-coordinators")
    public List<DeliveryCoordinator> getAllDeliveryCoordinators() {
        log.debug("REST request to get all DeliveryCoordinators");
        return deliveryCoordinatorService.findAll();
    }

    /**
     * GET  /delivery-coordinators/:id : get the "id" deliveryCoordinator.
     *
     * @param id the id of the deliveryCoordinator to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the deliveryCoordinator, or with status 404 (Not Found)
     */
    @GetMapping("/delivery-coordinators/{id}")
    public ResponseEntity<DeliveryCoordinator> getDeliveryCoordinator(@PathVariable Long id) {
        log.debug("REST request to get DeliveryCoordinator : {}", id);
        Optional<DeliveryCoordinator> deliveryCoordinator = deliveryCoordinatorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(deliveryCoordinator);
    }

    /**
     * DELETE  /delivery-coordinators/:id : delete the "id" deliveryCoordinator.
     *
     * @param id the id of the deliveryCoordinator to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/delivery-coordinators/{id}")
    public ResponseEntity<Void> deleteDeliveryCoordinator(@PathVariable Long id) {
        log.debug("REST request to delete DeliveryCoordinator : {}", id);
        deliveryCoordinatorService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
