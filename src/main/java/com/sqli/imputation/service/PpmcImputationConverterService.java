package com.sqli.imputation.service;

import com.sqli.imputation.domain.Imputation;
import com.sqli.imputation.domain.Team;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface PpmcImputationConverterService {

    Optional<Imputation> getPpmcImputationFromExcelFile(MultipartFile file, Team team);
}
