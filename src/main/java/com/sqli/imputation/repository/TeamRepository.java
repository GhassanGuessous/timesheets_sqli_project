package com.sqli.imputation.repository;

import com.sqli.imputation.domain.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Team entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query(value = "select t from Team  t left join t.deliveryCoordinator delco left join t.projectType type " +
        "where t.name like %:key% or t.agresso like %:key% " +
        "or delco.firstName like %:key% or delco.lastName like %:key% or  type.name like %:key%")
    Page<Team> findByKey(@Param("key") String key, Pageable pageable);

    Optional<Team> findByDeliveryCoordinatorId(Long id);

    Team findByIdTbpLike(String idTbp);

    Team findByAgressoLike(String agresso);
}
