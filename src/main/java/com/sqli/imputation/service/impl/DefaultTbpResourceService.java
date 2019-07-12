package com.sqli.imputation.service.impl;

import com.sqli.imputation.config.Constants;
import com.sqli.imputation.service.TbpResourceService;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.db_populator.Team.ChargeTeamRestResponse;
import com.sqli.imputation.service.dto.db_populator.Team.TeamRestResponse;
import com.sqli.imputation.service.dto.db_populator.activity.ActivityRestResponse;
import com.sqli.imputation.service.dto.db_populator.collaborator.CollaboratorRestResponse;
import com.sqli.imputation.service.dto.db_populator.projectType.ProjectTypeRestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DefaultTbpResourceService implements TbpResourceService {

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
    private static final String START_DATE_URL = "?date_debut=";
    private static final String END_DATE_URL = "&date_fin=";

    private HttpEntity<String> getTbpHttpHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(Constants.AUTHORIZATION, "Basic " + username + "/*TBP*/" + password);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        return entity;
    }

    @Override
    public ResponseEntity<ActivityRestResponse> getAllActivities() {
        return restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + ACTIVITES_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders("Kraouine", "Ironm@n102019"), ActivityRestResponse.class);
    }

    @Override
    public ResponseEntity<ProjectTypeRestResponse> getAllProjectTypes() {
        return restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + PROJETS_TYPES_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders("Kraouine", "Ironm@n102019"), ProjectTypeRestResponse.class);
    }

    @Override
    public ResponseEntity<TeamRestResponse> getAllTeams() {
        return restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + PROJETS_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders("Kraouine", "Ironm@n102019"), TeamRestResponse.class);
    }

    @Override
    public ResponseEntity<CollaboratorRestResponse> getAllCollaborators() {
        return  restTemplate.exchange(Constants.TBP_URL_WEB_SERVICE + COLLABORATEURS_URL + Constants.JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders("Kraouine", "Ironm@n102019"), CollaboratorRestResponse.class);
    }

    @Override
    public ResponseEntity<ChargeTeamRestResponse> getTeamCharges(TbpRequestBodyDTO requestBody) {
        return restTemplate.exchange(composeTbpURL(requestBody),
            HttpMethod.GET, getTbpHttpHeaders(requestBody.getUsername(), requestBody.getPassword()), ChargeTeamRestResponse.class);
    }

    private String composeTbpURL(TbpRequestBodyDTO requestBody){
        StringBuilder stringBuilder = new StringBuilder(Constants.TBP_URL_WEB_SERVICE + PROJETS_URL + SLASH);

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
