package com.sqli.imputation.service.impl;

import com.sqli.imputation.config.Constants;
import com.sqli.imputation.service.TBPResourceService;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.db_populator.Team.ChargeTeamRestResponse;
import com.sqli.imputation.service.dto.db_populator.Team.TeamRestResponse;
import com.sqli.imputation.service.dto.db_populator.activity.ActivityRestResponse;
import com.sqli.imputation.service.dto.db_populator.collaborator.CollaboratorRestResponse;
import com.sqli.imputation.service.dto.db_populator.projectType.ProjectTypeRestResponse;
import com.sqli.imputation.web.rest.ImputationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DefaultTBPResourceService implements TBPResourceService {

    private final Logger log = LoggerFactory.getLogger(ImputationResource.class);

    private static final int ID_TBP_POSITION = 0;
    private static final int START_DATE_POSITION = 1;
    private static final int END_DATE_POSITION = 2;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Autowired
    private RestTemplate restTemplate;
    private static final String PROJETS_URL = "projets";
    private static final String PROJETS_TYPES_URL = "projets/types";
    private static final String COLLABORATEURS_URL = "collaborateurs";
    private static final String ACTIVITES_URL = "activites";
    private static final String SLASH = "/";
    private static final String CHARGE_URL = "charge";
    private static final String CHARGE_WITH_DATE_URL = "?date_debut=2019-02-01&date_fin=2019-02-28";
    private static final String START_DATE_URL = "?date_debut=";
    private static final String END_DATE_URL = "&date_fin=";

    private HttpEntity<String> getTbpHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(Constants.AUTHORIZATION, Constants.BASIC_AUTH);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        return entity;
    }

    @Override
    public ResponseEntity<ActivityRestResponse> getAllActivities() {
        return restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + ACTIVITES_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), ActivityRestResponse.class);
    }

    @Override
    public ResponseEntity<ProjectTypeRestResponse> getAllProjectTypes() {
        return restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + PROJETS_TYPES_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), ProjectTypeRestResponse.class);
    }

    @Override
    public ResponseEntity<TeamRestResponse> getAllTeams() {
        return restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + PROJETS_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), TeamRestResponse.class);
    }

    @Override
    public ResponseEntity<CollaboratorRestResponse> getAllCollaborators() {
        return  restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + COLLABORATEURS_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), CollaboratorRestResponse.class);
    }

    @Override
    public ResponseEntity<ChargeTeamRestResponse> getTeamCharges(TbpRequestBodyDTO requestBody) {
        return restTemplate.exchange(composeTbpURL(requestBody),
            HttpMethod.GET, getTbpHttpHeaders(), ChargeTeamRestResponse.class);
    }

    private String composeTbpURL(TbpRequestBodyDTO requestBody){
        StringBuilder stringBuilder = new StringBuilder(Constants.TBP_URL_WEB_SERVICE + PROJETS_URL + SLASH );

        if(requestBody.getIdTbp() != null){
            stringBuilder.append(requestBody.getIdTbp());
            stringBuilder.append(SLASH + CHARGE_URL + Constants.JSON_RESULT_FORMAT);
        }

        if(requestBody.getStartDate() != null)
            stringBuilder.append(START_DATE_URL + requestBody.getStartDate());

        if(requestBody.getEndDate() != null)
            stringBuilder.append(END_DATE_URL + requestBody.getEndDate());

        return stringBuilder.toString();
    }
}
