package com.sqli.imputation.service;

import com.sqli.imputation.domain.Imputation;
import org.springframework.web.multipart.MultipartFile;

public interface PpmcImputationConverterService {

    Imputation getPpmcImputationFromExcelFile(MultipartFile file);
}
