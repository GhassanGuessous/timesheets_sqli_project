package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.*;
import com.sqli.imputation.service.db_populator.Team.TeamRestResponse;
import com.sqli.imputation.service.db_populator.activity.ActivityRestResponse;
import com.sqli.imputation.service.db_populator.collaborator.CollaboratorRestResponse;
import com.sqli.imputation.service.db_populator.projectType.ProjectTypeRestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DefaultDbPopulator implements DbPopulator {

    @Autowired
    private ActivityPopulatorService activityPopulatorService;
    @Autowired
    private CollaboratorPopulatorService collaboratorPopulatorService;
    @Autowired
    private ProjectTypePopulatorService projectTypePopulatorService;
    @Autowired
    private TeamPopulatorService teamPopulatorService;

    private ResponseEntity<ActivityRestResponse> activityRestResponse;
    private ResponseEntity<CollaboratorRestResponse> collaboratorRestResponse;
    private ResponseEntity<ProjectTypeRestResponse> projectTypeRestResponse;
    private ResponseEntity<TeamRestResponse> teamRestResponseResponse;

    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC_AUTH = "Basic Kraouine/*TBP*/Ironm@n2019";
    private static final String TBP_URL_WEB_SERVICE = "http://tbp-maroc.sqli.com/restService/public/";
    private static final String JSON_RESULT_FORMAT = ".json";

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Override
    public void populate(RestTemplate restTemplate) {
        hitTbpWebService(restTemplate);
        persist();
    }

    private void persist() {
//        persistActivities();
//
//        persistCollaborators();
//
//        persistProjectTypes();
//
//        persistTeams();
    }

    private void persistTeams() {
        teamRestResponseResponse.getBody().getData().getProjets().forEach(teamDTO -> {
            teamPopulatorService.populateDatabase(teamDTO);
        });
    }

    private void persistProjectTypes() {
        projectTypeRestResponse.getBody().getData().getTypes().forEach(projectTypeDTO -> {
            projectTypePopulatorService.populateDatabase(projectTypeDTO);
        });
    }

    private void persistCollaborators() {
        collaboratorRestResponse.getBody().getData().getCollaborateurs().forEach(collaboratorDTO -> {
            collaboratorPopulatorService.populateDatabase(collaboratorDTO);
        });
    }

    private void persistActivities() {
        activityRestResponse.getBody().getData().getActivites().forEach(activityDTO -> {
            activityPopulatorService.populateDatabase(activityDTO);
        });
    }

    private void hitTbpWebService(RestTemplate restTemplate) {
        activityRestResponse = restTemplate.exchange(TBP_URL_WEB_SERVICE + "activites" + JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), ActivityRestResponse.class);

        collaboratorRestResponse = restTemplate.exchange(TBP_URL_WEB_SERVICE + "collaborateurs" + JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), CollaboratorRestResponse.class);

        projectTypeRestResponse = restTemplate.exchange(TBP_URL_WEB_SERVICE + "projets/types" + JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), ProjectTypeRestResponse.class);

        teamRestResponseResponse = restTemplate.exchange(TBP_URL_WEB_SERVICE + "projets" + JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), TeamRestResponse.class);
    }

    private HttpEntity<String> getTbpHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(AUTHORIZATION, BASIC_AUTH);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        return entity;
    }
}
