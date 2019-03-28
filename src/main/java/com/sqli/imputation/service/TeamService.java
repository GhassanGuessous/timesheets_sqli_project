package com.sqli.imputation.service;

import com.sqli.imputation.domain.Team;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Team.
 */
public interface TeamService {

    /**
     * Save a team.
     *
     * @param team the entity to save
     * @return the persisted entity
     */
    Team save(Team team);

    /**
     * Get all the teams.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Team> findAll(Pageable pageable);


    /**
     * Get the "id" team.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Team> findOne(Long id);

    /**
     * Delete the "id" team.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Get all the teams with key.
     *
     * @param key the key to base searching on.
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Team> findByKey(String key, Pageable pageable);
}
