package com.sqli.imputation.repository;

import com.sqli.imputation.domain.ProjectType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ProjectType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectTypeRepository extends JpaRepository<ProjectType, Long> {

    ProjectType findByNameLike(String name);
}
