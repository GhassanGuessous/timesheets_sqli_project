package com.sqli.imputation.repository;

import com.sqli.imputation.domain.Collaborator;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Collaborator entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {

}
