package com.sqli.imputation.service;

import com.sqli.imputation.domain.Activity;
import com.sqli.imputation.service.dto.ActivityDTO;

public interface ActivityPopulatorService {

    Activity populateDatabase(ActivityDTO activityDTO);
}
