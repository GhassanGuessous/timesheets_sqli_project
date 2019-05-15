package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.AppRequestComposerService;
import com.sqli.imputation.service.dto.AppRequestDTO;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;


@Service
public class DefaultAppRequestComposerService implements AppRequestComposerService {

    private static final int FIRST_MONTH = 1;
    private static final int LAST_MONTH = 12;
    private static final int DEFAULT_START_DAY = 1;

    @Override
    public List<AppRequestDTO> divideAppPeriod(AppRequestDTO appRequestDTO) {
        List<AppRequestDTO> appRequestDTOS = new ArrayList<>();
        if (isMultipleMonths(appRequestDTO)) {
            getAppRequestDTOS(appRequestDTOS, appRequestDTO);
        } else {
            appRequestDTOS.add(appRequestDTO);
        }
        return appRequestDTOS;
    }

    private List<AppRequestDTO> getAppRequestDTOS(List<AppRequestDTO> appRequestDTOS, AppRequestDTO appRequestDTO) {
        appRequestDTOS.add(createFirstAppRequestDTO(appRequestDTO));
        setAppRequestForFirstMonth(appRequestDTO);
        while (appRequestDTO.getManDay() > 0) {
            appRequestDTOS.add(createAppRequestDTO(appRequestDTO));
            setAppRequest(appRequestDTO);
        }
        return appRequestDTOS;
    }

    private AppRequestDTO createFirstAppRequestDTO(AppRequestDTO appRequestDTO) {
        /* Adding the DEFAULT_START_DAY to include the start day requested*/
        return new AppRequestDTO(appRequestDTO.getAgresso(), appRequestDTO.getYear(), appRequestDTO.getMonth(), appRequestDTO.getStartDay(), getRequestedManDay(appRequestDTO) + DEFAULT_START_DAY);
    }

    private AppRequestDTO createAppRequestDTO(AppRequestDTO appRequestDTO) {
        return new AppRequestDTO(appRequestDTO.getAgresso(), appRequestDTO.getYear(), appRequestDTO.getMonth(), appRequestDTO.getStartDay(), initializeManDay(appRequestDTO));
    }

    private void setAppRequestForFirstMonth(AppRequestDTO appRequestDTO) {
        appRequestDTO.setManDay(remainingManDays(appRequestDTO) - DEFAULT_START_DAY);
        setRequestDateParameters(appRequestDTO);
    }

    private void setAppRequest(AppRequestDTO appRequestDTO) {
        appRequestDTO.setManDay(appRequestDTO.getManDay() - (getNumberOfDays(appRequestDTO.getYear(), appRequestDTO.getMonth())));
        setRequestDateParameters(appRequestDTO);
    }

    private void setRequestDateParameters(AppRequestDTO appRequestDTO) {
        if (appRequestDTO.getMonth() == LAST_MONTH) {
            appRequestDTO.setYear(appRequestDTO.getYear() + 1);
            appRequestDTO.setMonth(FIRST_MONTH);
        } else {
            appRequestDTO.setMonth(appRequestDTO.getMonth() + 1);
        }
        appRequestDTO.setStartDay(DEFAULT_START_DAY);
    }

    private int getRequestedManDay(AppRequestDTO appRequestDTO) {
        return getNumberOfDays(appRequestDTO.getYear(), appRequestDTO.getMonth()) - appRequestDTO.getStartDay();
    }

    private int initializeManDay(AppRequestDTO appRequestDTO) {
        if (isAllDaysOfMonthRequested(appRequestDTO)) {
            return getNumberOfDays(appRequestDTO.getYear(), appRequestDTO.getMonth());
        } else {
            return appRequestDTO.getManDay();
        }
    }

    private boolean isAllDaysOfMonthRequested(AppRequestDTO appRequestDTO) {
        return appRequestDTO.getManDay() >= getNumberOfDays(appRequestDTO.getYear(), appRequestDTO.getMonth());
    }

    private boolean isMultipleMonths(AppRequestDTO appRequestDTO) {
        return remainingManDays(appRequestDTO) > 1;
    }

    private int remainingManDays(AppRequestDTO appRequestDTO) {
        return appRequestDTO.getManDay() - (getRequestedManDay(appRequestDTO));
    }

    private int getNumberOfDays(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }
}
