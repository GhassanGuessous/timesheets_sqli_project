package com.sqli.imputation.service;

import com.sqli.imputation.domain.Collaborator;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.dto.jira.PpmcProjectWorklogDTO;

import java.util.List;

public interface PpmcProjectWorklogStaticticsService {
    List<PpmcProjectWorklogDTO> getPpmcProjectWorkloged(TbpRequestBodyDTO requestBodyDTO, List<Collaborator> collaborators);
}
