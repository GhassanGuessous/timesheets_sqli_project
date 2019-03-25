package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Team;
import com.sqli.imputation.service.TeamPopulatorService;
import com.sqli.imputation.service.TeamService;
import com.sqli.imputation.service.dto.TeamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultTeamPopulatorService implements TeamPopulatorService {

    @Autowired
    private TeamService teamService;

    @Override
    public Team populateDatabase(TeamDTO teamDTO) {
        Team team = clone(teamDTO);
        return teamService.save(team);
    }

    private Team clone(TeamDTO teamDTO) {
        Team team = new Team();
        team.setName(teamDTO.getLibelle());
        return team;
    }
}
