package com.sqli.imputation.service;

import com.sqli.imputation.domain.DeliveryCoordinator;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing DeliveryCoordinator.
 */
public interface DeliveryCoordinatorService {

    /**
     * Save a deliveryCoordinator.
     *
     * @param deliveryCoordinator the entity to save
     * @return the persisted entity
     */
    DeliveryCoordinator save(DeliveryCoordinator deliveryCoordinator);

    /**
     * Get all the deliveryCoordinators.
     *
     * @return the list of entities
     */
    List<DeliveryCoordinator> findAll();


    /**
     * Get the "id" deliveryCoordinator.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<DeliveryCoordinator> findOne(Long id);

    /**
     * Delete the "id" deliveryCoordinator.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
