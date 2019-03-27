package com.sqli.imputation.service.impl;

import com.sqli.imputation.config.Constants;
import com.sqli.imputation.repository.ActivityRepository;
import com.sqli.imputation.service.*;
import com.sqli.imputation.service.db_populator.Team.TeamRestResponse;
import com.sqli.imputation.service.db_populator.activity.ActivityRestResponse;
import com.sqli.imputation.service.db_populator.collaborator.CollaboratorRestResponse;
import com.sqli.imputation.service.db_populator.projectType.ProjectTypeRestResponse;
import com.sqli.imputation.service.dto.ActivityDTO;
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

    private ResponseEntity<ActivityRestResponse> activityRestResponse;
    private ResponseEntity<CollaboratorRestResponse> collaboratorRestResponse;
    private ResponseEntity<ProjectTypeRestResponse> projectTypeRestResponse;
    private ResponseEntity<TeamRestResponse> teamRestResponseResponse;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Override
    public void populate(RestTemplate restTemplate) {
        // if(isDbEmpty()){
        hitTbpWebService(restTemplate);
        persist();
        //  }
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
            throw new TBPBadAuthentificationException("Bad TBP Credentials");
        }
    }

    private void persist() {
        //   persistActivities();
        //  persistProjectTypes();
        persistTeams();
        persistCollaborators();
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
        teamRestResponseResponse = restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + PROJETS_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), TeamRestResponse.class);
    }

    private void setCollaborators(RestTemplate restTemplate) {
        collaboratorRestResponse = restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + COLLABORATEURS_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), CollaboratorRestResponse.class);
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
        teamRestResponseResponse.getBody().getData().getProjets().forEach(teamDTO -> {
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
