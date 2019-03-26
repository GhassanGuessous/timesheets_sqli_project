package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.ProjectType;
import com.sqli.imputation.domain.Team;
import com.sqli.imputation.repository.ProjectTypeRepository;
import com.sqli.imputation.service.TeamPopulatorService;
import com.sqli.imputation.service.TeamService;
import com.sqli.imputation.service.dto.ProjectTypeDTO;
import com.sqli.imputation.service.dto.TeamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultTeamPopulatorService implements TeamPopulatorService {

    @Autowired
    private TeamService teamService;
    @Autowired
    private DefaultDbPopulator defaultDbPopulator;
    @Autowired
    private ProjectTypeRepository projectTypeRepository;

    @Override
    public Team populateDatabase(TeamDTO teamDTO) {
        Team team = clone(teamDTO);
        return teamService.save(team);
    }

    private Team clone(TeamDTO teamDTO) {
        Team team = new Team();
        team.setName(teamDTO.getLibelle());
        team.setAgresso(teamDTO.getAgresso());
        team.setProjectType(findProjectType(teamDTO.getType()));
        return team;
    }

    private ProjectType findProjectType(String id) {
        Long idDTO = Long.valueOf(Integer.parseInt(id));
        return projectTypeRepository.findByNameLike(findProjectTypeByIdFromWs(idDTO).getLibelle());
    }

    private ProjectTypeDTO findProjectTypeByIdFromWs(Long id) {
        return defaultDbPopulator.getProjectTypes().stream().filter(typeDTO -> typeDTO.getId() == id).findFirst().get();
    }
}
