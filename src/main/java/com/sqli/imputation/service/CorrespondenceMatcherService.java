package com.sqli.imputation.service;

import com.sqli.imputation.domain.Correspondence;
import com.sqli.imputation.service.dto.CorrespondenceDTO;

import java.util.List;

public interface CorrespondenceMatcherService {

    List<CorrespondenceDTO> getCorrespondencesFromExcelFile(String fileName);

    void match(List<Correspondence> correspondences, String fileName);
}
