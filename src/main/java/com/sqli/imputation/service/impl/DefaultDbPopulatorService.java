package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.domain.Correspondence;
import com.sqli.imputation.domain.Team;
import com.sqli.imputation.repository.ActivityRepository;
import com.sqli.imputation.repository.CollaboratorRepository;
import com.sqli.imputation.repository.CorrespondenceRepository;
import com.sqli.imputation.repository.TeamRepository;
import com.sqli.imputation.service.*;
import com.sqli.imputation.service.dto.*;
import com.sqli.imputation.service.dto.dbpopulator.team.ChargeTeamRestResponse;
import com.sqli.imputation.service.dto.dbpopulator.team.TeamRestResponse;
import com.sqli.imputation.service.dto.dbpopulator.activity.ActivityRestResponse;
import com.sqli.imputation.service.dto.dbpopulator.collaborator.CollaboratorRestResponse;
import com.sqli.imputation.service.dto.dbpopulator.projecttype.ProjectTypeRestResponse;
import com.sqli.imputation.web.rest.errors.TBPBadAuthentificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultDbPopulatorService implements DbPopulatorService {

    private static final String BAD_TBP_CREDENTIALS = "Bad TBP Credentials";
    private static final String EXCEL_FILE_PATH = "C:\\Users\\aaitbassou\\Desktop\\Imputation_projet\\STG-Comparateur_imputations_APP_Vs_PPMC.xls";

    @Autowired
    private TbpResourceService tbpResourceService;
    @Autowired
    private ActivityPopulatorService activityPopulatorService;
    @Autowired
    private CollaboratorPopulatorService collaboratorPopulatorService;
    @Autowired
    private ProjectTypePopulatorService projectTypePopulatorService;
    @Autowired
    private TeamPopulatorService teamPopulatorService;
    @Autowired
    private DefaultCorrespondenceMatcherService matcherService;
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


    @Override
    public void populate() {
        if (isDbEmpty()) {
            hitTbpWebService();
            persist();
            composeTeams();
            matcherService.match(correspondenceRepository.findAll(), EXCEL_FILE_PATH);
        }
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

    private void hitTbpWebService() {
        try {
            setActivities();
            setProjectTypes();
            setTeams();
            setCollaborators();
        } catch (Exception e) {
            throw new TBPBadAuthentificationException(BAD_TBP_CREDENTIALS);
        }
    }

    private void hitChargeTeamWebService(String idTeamTbp) {
        try {
            setChargeTeams(idTeamTbp);
        } catch (Exception e) {
            throw new TBPBadAuthentificationException(BAD_TBP_CREDENTIALS);
        }
    }

    private void persist() {
        persistActivities();
        persistProjectTypes();
        persistTeams();
        persistCollaborators();
    }

    private void composeTeams() {
        List<Team> teams = teamRepository.findAll();
        teams.forEach(team -> {
            hitChargeTeamWebService(team.getIdTbp());
            loopThroughChargeTeam(team);
        });
    }

    private void loopThroughChargeTeam(Team team) {
        chargeTeamRestResponse.getBody().getData().getCharge().forEach(chargeTeamDTO -> loopThroughCollabs(team, chargeTeamDTO));
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
        return collaborator.getTeam() != null && collaborator.getTeam().getId().equals(team.getId());
    }

    private void setActivities() {
        activityRestResponse = tbpResourceService.getAllActivities();
    }

    private void setProjectTypes() {
        projectTypeRestResponse = tbpResourceService.getAllProjectTypes();
    }

    private void setTeams() {
        teamRestResponse = tbpResourceService.getAllTeams();
    }

    private void setCollaborators() {
        collaboratorRestResponse = tbpResourceService.getAllCollaborators();
    }

    private void setChargeTeams(String idTeamTbp) {
        chargeTeamRestResponse = tbpResourceService.getTeamCharges(new TbpRequestBodyDTO(idTeamTbp, "2019-04-01", "2019-06-28", "", ""));
    }

    private void persistActivities() {
        activityRestResponse.getBody().getData().getActivites().forEach(activityDTO -> activityPopulatorService.populateDatabase(activityDTO));
    }

    private void persistProjectTypes() {
        projectTypeRestResponse.getBody().getData().getTypes().forEach(projectTypeDTO -> projectTypePopulatorService.populateDatabase(projectTypeDTO));
    }

    private void persistTeams() {
        teamRestResponse.getBody().getData().getProjets().forEach(teamDTO -> teamPopulatorService.populateDatabase(teamDTO));
    }

    private void persistCollaborators() {
        collaboratorRestResponse.getBody().getData().getCollaborateurs().forEach(collaboratorDTO -> collaboratorPopulatorService.populateDatabase(collaboratorDTO));
    }

    public List<ProjectTypeDTO> getProjectTypes() {
        return projectTypeRestResponse.getBody().getData().getTypes();
    }

    public List<ActivityDTO> getActivities() {
        return activityRestResponse.getBody().getData().getActivites();
    }
}
