package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.DeliveryCoordinatorService;
import com.sqli.imputation.domain.DeliveryCoordinator;
import com.sqli.imputation.repository.DeliveryCoordinatorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing DeliveryCoordinator.
 */
@Service
@Transactional
public class DeliveryCoordinatorServiceImpl implements DeliveryCoordinatorService {

    private final Logger log = LoggerFactory.getLogger(DeliveryCoordinatorServiceImpl.class);

    private final DeliveryCoordinatorRepository deliveryCoordinatorRepository;

    public DeliveryCoordinatorServiceImpl(DeliveryCoordinatorRepository deliveryCoordinatorRepository) {
        this.deliveryCoordinatorRepository = deliveryCoordinatorRepository;
    }

    /**
     * Save a deliveryCoordinator.
     *
     * @param deliveryCoordinator the entity to save
     * @return the persisted entity
     */
    @Override
    public DeliveryCoordinator save(DeliveryCoordinator deliveryCoordinator) {
        log.debug("Request to save DeliveryCoordinator : {}", deliveryCoordinator);
        return deliveryCoordinatorRepository.save(deliveryCoordinator);
    }

    /**
     * Get all the deliveryCoordinators.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<DeliveryCoordinator> findAll() {
        log.debug("Request to get all DeliveryCoordinators");
        return deliveryCoordinatorRepository.findAll();
    }


    /**
     * Get one deliveryCoordinator by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<DeliveryCoordinator> findOne(Long id) {
        log.debug("Request to get DeliveryCoordinator : {}", id);
        return deliveryCoordinatorRepository.findById(id);
    }

    /**
     * Delete the deliveryCoordinator by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete DeliveryCoordinator : {}", id);
        deliveryCoordinatorRepository.deleteById(id);
    }
}
