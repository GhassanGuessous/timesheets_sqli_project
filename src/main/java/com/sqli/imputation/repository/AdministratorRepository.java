package com.sqli.imputation.repository;

import com.sqli.imputation.domain.Administrator;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Administrator entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

}
