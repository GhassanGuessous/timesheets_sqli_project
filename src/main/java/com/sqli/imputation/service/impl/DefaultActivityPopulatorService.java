package com.sqli.imputation.service.impl;

import com.sqli.imputation.domain.Activity;
import com.sqli.imputation.service.ActivityPopulatorService;
import com.sqli.imputation.service.ActivityService;
import com.sqli.imputation.service.dto.ActivityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultActivityPopulatorService implements ActivityPopulatorService {

    @Autowired
    private ActivityService activityService;

    @Override
    public Activity populateDatabase(ActivityDTO activityDTO) {
        Activity activity = clone(activityDTO);
        activity = activityService.save(activity);
        return activity;
    }

    private Activity clone(ActivityDTO activityDTO) {
        Activity activity = new Activity();
        activity.setName(activityDTO.getLibelle());
        return activity;
    }


}
