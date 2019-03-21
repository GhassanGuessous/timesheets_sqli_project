package com.sqli.imputation.repository;

import com.sqli.imputation.domain.DeliveryCoordinator;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the DeliveryCoordinator entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeliveryCoordinatorRepository extends JpaRepository<DeliveryCoordinator, Long> {

}
