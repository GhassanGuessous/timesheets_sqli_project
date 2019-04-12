package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.TbpRequestComposerService;
import com.sqli.imputation.service.dto.AppRequestDTO;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.util.DateUtil;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;

import static com.sqli.imputation.service.util.DateUtil.END;
import static com.sqli.imputation.service.util.DateUtil.getNumberMonthsBetweenDates;

@Service
public class DefaultTbpRequestComposerService implements TbpRequestComposerService {

    private static final String START = "start";
    private static final String DELIMITER = "-";
    private static final String TWO_DIGITS_FORMAT = "%02d";
    public static final int FIRST_MONTH = 1;
    public static final int LAST_MONTH = 12;
    public static final int DEFAULT_START_DAY = 1;

    @Override
    public List<TbpRequestBodyDTO> tbpDividePeriod(TbpRequestBodyDTO tbpRequestBodyDTO) {
        List<TbpRequestBodyDTO> bodyDTOS = new ArrayList<>();
        Map<Integer, Map<String, Integer>> months;
        if (isMultipleMonths(tbpRequestBodyDTO)) {
            int numberOfMonths = DateUtil.getNumberMonthsBetweenDates(tbpRequestBodyDTO.getStartDate(), tbpRequestBodyDTO.getEndDate());
            months = DateUtil.getMonths(tbpRequestBodyDTO.getStartDate(), tbpRequestBodyDTO.getEndDate(), numberOfMonths);
            bodyDTOS = getTbpRequestBodyDTOS(months, DateUtil.getYear(tbpRequestBodyDTO.getStartDate()), tbpRequestBodyDTO.getIdTbp());
        } else {
            bodyDTOS.add(tbpRequestBodyDTO);
        }
        return bodyDTOS;
    }

    @Override
    public List<AppRequestDTO> appDividePeriod(AppRequestDTO appRequestDTO) {
        List<AppRequestDTO> appRequestDTOS = new ArrayList<>();
        if (isMultipleMonths(appRequestDTO)) {
            getAppRequestDTOS(appRequestDTOS, appRequestDTO);
        } else {
            appRequestDTOS.add(appRequestDTO);
        }
        return appRequestDTOS;
    }

    private List<TbpRequestBodyDTO> getTbpRequestBodyDTOS(Map<Integer, Map<String, Integer>> months, int year, String idTbp) {
        List<TbpRequestBodyDTO> bodyDTOS = new ArrayList<>();
        months.forEach((month, startEndMap) -> {
            StringBuilder startDateBuilder = new StringBuilder(year + DELIMITER);
            StringBuilder endDateBuilder = new StringBuilder(year + DELIMITER);
            composeStartDate(month, startEndMap, startDateBuilder);
            composeEndDate(month, startEndMap, endDateBuilder);
            bodyDTOS.add(new TbpRequestBodyDTO(idTbp, startDateBuilder.toString(), endDateBuilder.toString()));
        });
        return bodyDTOS;
    }

    private void composeEndDate(Integer month, Map<String, Integer> startEndMap, StringBuilder endDateBuilder) {
        endDateBuilder.append(String.format(TWO_DIGITS_FORMAT, month) + DELIMITER);
        endDateBuilder.append(String.format(TWO_DIGITS_FORMAT, startEndMap.get(END)));
    }

    private void composeStartDate(Integer month, Map<String, Integer> startEndMap, StringBuilder startDateBuilder) {
        startDateBuilder.append(String.format(TWO_DIGITS_FORMAT, month) + DELIMITER);
        startDateBuilder.append(String.format(TWO_DIGITS_FORMAT, startEndMap.get(START)));
    }

    private boolean isMultipleMonths(TbpRequestBodyDTO tbpRequestBodyDTO) {
        return getNumberMonthsBetweenDates(tbpRequestBodyDTO.getStartDate(), tbpRequestBodyDTO.getEndDate()) != 0;
    }

    private List<AppRequestDTO> getAppRequestDTOS(List<AppRequestDTO> appRequestDTOS, AppRequestDTO appRequestDTO) {
        appRequestDTOS.add(createFirstAppRequestDTO(appRequestDTO));
        setAppRequestForFirstMonth(appRequestDTO);
        while (appRequestDTO.getManDay() > 1) {
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
        return remainingManDays(appRequestDTO) > 0;
    }

    private int remainingManDays(AppRequestDTO appRequestDTO) {
        return appRequestDTO.getManDay() - (getRequestedManDay(appRequestDTO));
    }

    private int getNumberOfDays(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }
}
