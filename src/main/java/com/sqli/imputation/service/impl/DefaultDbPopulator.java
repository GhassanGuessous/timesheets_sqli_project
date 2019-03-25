package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.ActivityPopulatorService;
import com.sqli.imputation.service.CollaboratorPopulatorService;
import com.sqli.imputation.service.DbPopulator;
import com.sqli.imputation.service.db_populator.activity.ActivityRestResponse;
import com.sqli.imputation.service.db_populator.collaborator.CollaboratorRestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DefaultDbPopulator implements DbPopulator {

    @Autowired
    ActivityPopulatorService activityPopulatorService;
    @Autowired
    CollaboratorPopulatorService collaboratorPopulatorService;

    ResponseEntity<ActivityRestResponse> activityRestResponse;
    ResponseEntity<CollaboratorRestResponse> collaboratorRestResponse;

    public static final String AUTHORIZATION = "Authorization";
    public static final String BASIC_AUTH = "Basic Kraouine/*TBP*/Ironm@n2019";
    public static final String TBP_URL_WEB_SERVICE = "http://tbp-maroc.sqli.com/restService/public/";
    public static final String JSON_RESULT_FORMAT = ".json";

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
        activityRestResponse.getBody().getData().getActivites().forEach(activityDTO -> {
            activityPopulatorService.populateDatabase(activityDTO);
        });

        collaboratorRestResponse.getBody().getData().getCollaborateurs().forEach(collaboratorDTO -> {
            collaboratorPopulatorService.populateDatabase(collaboratorDTO);
        });
    }

    private void hitTbpWebService(RestTemplate restTemplate) {
        activityRestResponse = restTemplate.exchange(TBP_URL_WEB_SERVICE + "activites" + JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), ActivityRestResponse.class);

        collaboratorRestResponse = restTemplate.exchange(TBP_URL_WEB_SERVICE + "collaborateurs" + JSON_RESULT_FORMAT,
            HttpMethod.GET, getTbpHttpHeaders(), CollaboratorRestResponse.class);
    }

    private HttpEntity<String> getTbpHttpHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(AUTHORIZATION, BASIC_AUTH);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        return entity;
    }
}
