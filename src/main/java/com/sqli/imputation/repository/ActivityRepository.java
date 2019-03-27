package com.sqli.imputation.repository;

import com.sqli.imputation.domain.Activity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Activity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Activity findByNameLike(String name);
}
