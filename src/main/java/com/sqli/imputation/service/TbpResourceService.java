package com.sqli.imputation.service;

import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.db_populator.Team.ChargeTeamRestResponse;
import com.sqli.imputation.service.dto.db_populator.Team.TeamRestResponse;
import com.sqli.imputation.service.dto.db_populator.activity.ActivityRestResponse;
import com.sqli.imputation.service.dto.db_populator.collaborator.CollaboratorRestResponse;
import com.sqli.imputation.service.dto.db_populator.projectType.ProjectTypeRestResponse;
import org.springframework.http.ResponseEntity;

public interface TbpResourceService {

    ResponseEntity<ActivityRestResponse> getAllActivities();

    ResponseEntity<ProjectTypeRestResponse> getAllProjectTypes();

    ResponseEntity<TeamRestResponse> getAllTeams();

    ResponseEntity<CollaboratorRestResponse> getAllCollaborators();

    ResponseEntity<ChargeTeamRestResponse> getTeamCharges(TbpRequestBodyDTO requestBody);


}
