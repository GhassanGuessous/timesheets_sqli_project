package com.sqli.imputation.service;

import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.dbpopulator.team.ChargeTeamRestResponse;
import com.sqli.imputation.service.dto.dbpopulator.team.TeamRestResponse;
import com.sqli.imputation.service.dto.dbpopulator.activity.ActivityRestResponse;
import com.sqli.imputation.service.dto.dbpopulator.collaborator.CollaboratorRestResponse;
import com.sqli.imputation.service.dto.dbpopulator.projecttype.ProjectTypeRestResponse;
import org.springframework.http.ResponseEntity;

public interface TbpResourceService {

    ResponseEntity<ActivityRestResponse> getAllActivities();

    ResponseEntity<ProjectTypeRestResponse> getAllProjectTypes();

    ResponseEntity<TeamRestResponse> getAllTeams();

    ResponseEntity<CollaboratorRestResponse> getAllCollaborators();

    ResponseEntity<ChargeTeamRestResponse> getTeamCharges(TbpRequestBodyDTO requestBody);


}
