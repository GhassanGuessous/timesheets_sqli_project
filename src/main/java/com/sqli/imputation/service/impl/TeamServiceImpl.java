package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.AppTbpIdentifierService;
import com.sqli.imputation.service.TeamService;
import com.sqli.imputation.domain.Team;
import com.sqli.imputation.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing Team.
 */
@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    private final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);

    private final TeamRepository teamRepository;

    @Autowired
    private AppTbpIdentifierService appTbpIdentifierService;

    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    /**
     * Save a team.
     *
     * @param team the entity to save
     * @return the persisted entity
     */
    @Override
    public Team save(Team team) {
        log.debug("Request to save Team : {}", team);
        team.getAppTbpIdentifiers().forEach(appTbpIdentifier -> {
            appTbpIdentifier.setTeam(team);
            appTbpIdentifierService.save(appTbpIdentifier);
        });
        return teamRepository.save(team);
    }

    /**
     * Get all the teams.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Team> findAll(Pageable pageable) {
        log.debug("Request to get all Teams");
        return teamRepository.findAll(pageable);
    }

    @Override
    public List<Team> findAll() {
        log.debug("Request to get all Teams");
        return teamRepository.findAll();
    }


    /**
     * Get one team by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Team> findOne(Long id) {
        log.debug("Request to get Team : {}", id);
        return teamRepository.findById(id);
    }

    @Override
    public Optional<Team> findOneByDelco(Long id) {

        log.debug("Request to get Team by its delivery coordiinator : {}", id);
        return teamRepository.findByDeliveryCoordinatorId(id);
    }

    /**
     * Delete the team by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Team : {}", id);
        teamRepository.deleteById(id);
    }

    /**
     * Get all the teams with key.
     *
     * @param key the key to base searching on
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<Team> findByKey(String key, Pageable pageable) {
        log.debug("Request to get all Teams with key: "+ key);
        return teamRepository.findByKey(key, pageable);
    }
}
