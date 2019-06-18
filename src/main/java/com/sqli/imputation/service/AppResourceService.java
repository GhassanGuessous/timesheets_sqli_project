package com.sqli.imputation.service;

import com.sqli.imputation.service.dto.AppChargeDTO;
import com.sqli.imputation.service.dto.AppRequestDTO;

import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.util.List;

public interface AppResourceService {

    List<AppChargeDTO> getTeamCharges(AppRequestDTO requestBody) throws IOException, SOAPException;

}
