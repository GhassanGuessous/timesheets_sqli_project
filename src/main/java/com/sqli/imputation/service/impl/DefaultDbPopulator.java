package com.sqli.imputation.service.impl;

import com.sqli.imputation.config.Constants;
import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.domain.Correspondence;
import com.sqli.imputation.domain.Team;
import com.sqli.imputation.repository.ActivityRepository;
import com.sqli.imputation.repository.CollaboratorRepository;
import com.sqli.imputation.repository.CorrespondenceRepository;
import com.sqli.imputation.repository.TeamRepository;
import com.sqli.imputation.service.*;
import com.sqli.imputation.service.db_populator.Team.ChargeTeamRestResponse;
import com.sqli.imputation.service.db_populator.Team.TeamRestResponse;
import com.sqli.imputation.service.db_populator.activity.ActivityRestResponse;
import com.sqli.imputation.service.db_populator.collaborator.CollaboratorRestResponse;
import com.sqli.imputation.service.db_populator.projectType.ProjectTypeRestResponse;
import com.sqli.imputation.service.dto.ActivityDTO;
import com.sqli.imputation.service.dto.ChargeCollaboratorDTO;
import com.sqli.imputation.service.dto.ChargeTeamDTO;
import com.sqli.imputation.service.dto.ProjectTypeDTO;
import com.sqli.imputation.web.rest.errors.TBPBadAuthentificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DefaultDbPopulator implements DbPopulator {

    private static final String PROJETS_URL = "projets";
    private static final String PROJETS_TYPES_URL = "projets/types";
    private static final String COLLABORATEURS_URL = "collaborateurs";
    private static final String ACTIVITES_URL = "activites";
    public static final String BAD_TBP_CREDENTIALS = "Bad TBP Credentials";
    public static final String SLASH = "/";
    public static final String CHARGE_URL = "charge";
    public static final String CHARGE_WITH_DATE_URL = "?date_debut=2019-02-01&date_fin=2019-02-28";

    @Autowired
    private ActivityPopulatorService activityPopulatorService;
    @Autowired
    private CollaboratorPopulatorService collaboratorPopulatorService;
    @Autowired
    private ProjectTypePopulatorService projectTypePopulatorService;
    @Autowired
    private TeamPopulatorService teamPopulatorService;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private CorrespondenceRepository correspondenceRepository;
    @Autowired
    private CollaboratorRepository collaboratorRepository;

    private ResponseEntity<ActivityRestResponse> activityRestResponse;
    private ResponseEntity<CollaboratorRestResponse> collaboratorRestResponse;
    private ResponseEntity<ProjectTypeRestResponse> projectTypeRestResponse;
    private ResponseEntity<TeamRestResponse> teamRestResponse;
    private ResponseEntity<ChargeTeamRestResponse> chargeTeamRestResponse;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Override
    public void populate(RestTemplate restTemplate) {
//        if (isDbEmpty()) {
            hitTbpWebService(restTemplate);
//            persist();
            composeTeams(restTemplate);
//        }
    }

    /**
     * test if essential data is available, by testing
     * one table; in this case activity table
     *
     * @return
     */
    private boolean isDbEmpty() {
        return activityRepository.findAll().isEmpty();
    }

    private void hitTbpWebService(RestTemplate restTemplate) {
        try {
            setActivities(restTemplate);
            setProjectTypes(restTemplate);
            setTeams(restTemplate);
            setCollaborators(restTemplate);
        } catch (Exception e) {
            throw new TBPBadAuthentificationException(BAD_TBP_CREDENTIALS);
        }
    }

    private void hitChargeTeamWebService(RestTemplate restTemplate, String idTeamTbp) {
        try {
            setChargeTeams(restTemplate, idTeamTbp);
        } catch (Exception e) {
            throw new TBPBadAuthentificationException(BAD_TBP_CREDENTIALS);
        }
    }

    private void persist() {
//        persistActivities();
//        persistProjectTypes();
//        persistTeams();
        persistCollaborators();
    }

    private void composeTeams(RestTemplate restTemplate) {
        List<Team> teams = teamRepository.findAll();
        teams.forEach(team -> {
            hitChargeTeamWebService(restTemplate, team.getIdTbp());
            loopThroughChargeTeam(team);
        });
    }

    private void loopThroughChargeTeam(Team team) {
        chargeTeamRestResponse.getBody().getData().getCharge().forEach(chargeTeamDTO -> {
            loopThroughCollabs(team, chargeTeamDTO);
        });
    }

    private void loopThroughCollabs(Team team, ChargeTeamDTO chargeTeamDTO) {
        chargeTeamDTO.getCollaborateurs().forEach(chargeCollaboratorDTO -> {
            Correspondence correspondence = findByIdTBP(chargeCollaboratorDTO);
            if (correspondence != null) {
                Collaborator collaborator = correspondence.getCollaborator();
                assignCollabToTeam(team, collaborator);
            }
        });
    }

    private void assignCollabToTeam(Team team, Collaborator collaborator) {
        if (isNotAlreadyMemberOfTeam(collaborator, team)) {
            collaborator.setTeam(team);
            collaboratorRepository.save(collaborator);
        }
    }

    private Correspondence findByIdTBP(ChargeCollaboratorDTO chargeCollaboratorDTO) {
        return correspondenceRepository.findByIdTBP(chargeCollaboratorDTO.getId());
    }

    private boolean isNotAlreadyMemberOfTeam(Collaborator collaborator, Team team) {
        if (collaborator.getTeam() != null
            && collaborator.getTeam().getId().equals(team.getId())) {
            return false;
        }
        return true;
    }

    private HttpEntity<String> getTbpHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(Constants.AUTHORIZATION, Constants.BASIC_AUTH);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        return entity;
    }

    private void setActivities(RestTemplate restTemplate) {
        activityRestResponse = restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + ACTIVITES_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), ActivityRestResponse.class);
    }

    private void setProjectTypes(RestTemplate restTemplate) {
        projectTypeRestResponse = restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + PROJETS_TYPES_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), ProjectTypeRestResponse.class);
    }

    private void setTeams(RestTemplate restTemplate) {
        teamRestResponse = restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + PROJETS_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), TeamRestResponse.class);
    }

    private void setCollaborators(RestTemplate restTemplate) {
        collaboratorRestResponse = restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + COLLABORATEURS_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), CollaboratorRestResponse.class);
    }

    private void setChargeTeams(RestTemplate restTemplate, String idTeamTbp) {
        chargeTeamRestResponse = restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + PROJETS_URL + SLASH + idTeamTbp + SLASH + CHARGE_URL
                + Constants.JSON_RESULT_FORMAT + CHARGE_WITH_DATE_URL,
            HttpMethod.GET, getTbpHttpHeaders(), ChargeTeamRestResponse.class);
    }

    private void persistActivities() {
        activityRestResponse.getBody().getData().getActivites().forEach(activityDTO -> {
            activityPopulatorService.populateDatabase(activityDTO);
        });
    }

    private void persistProjectTypes() {
        projectTypeRestResponse.getBody().getData().getTypes().forEach(projectTypeDTO -> {
            projectTypePopulatorService.populateDatabase(projectTypeDTO);
        });
    }

    private void persistTeams() {
        teamRestResponse.getBody().getData().getProjets().forEach(teamDTO -> {
            teamPopulatorService.populateDatabase(teamDTO);
        });
    }

    private void persistCollaborators() {
        collaboratorRestResponse.getBody().getData().getCollaborateurs().forEach(collaboratorDTO -> {
            collaboratorPopulatorService.populateDatabase(collaboratorDTO);
        });
    }

    public List<ProjectTypeDTO> getProjectTypes() {
        return projectTypeRestResponse.getBody().getData().getTypes();
    }

    public List<ActivityDTO> getActivities() {
        return activityRestResponse.getBody().getData().getActivites();
    }
}
