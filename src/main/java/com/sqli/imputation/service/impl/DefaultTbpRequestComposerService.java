package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.TbpRequestComposerService;
import com.sqli.imputation.service.dto.TbpPeriodDTO;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.util.DateUtil;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.sqli.imputation.service.util.DateUtil.getNumberMonthsBetweenDates;

@Service
public class DefaultTbpRequestComposerService implements TbpRequestComposerService {

    private static final String START_STRING = "start";
    private static final String END_STRING = "end";
    private static final String DELIMITER = "-";
    private static final String TWO_DIGITS_FORMAT = "%02d";

    @Override
    public List<TbpRequestBodyDTO> divideTbpPeriod(TbpRequestBodyDTO tbpRequestBodyDTO) {
        List<TbpRequestBodyDTO> bodyDTOS = new ArrayList<>();
        if (isMultipleMonths(tbpRequestBodyDTO)) {
            bodyDTOS = getRequestedPeriod(tbpRequestBodyDTO);
        } else {
            bodyDTOS.add(tbpRequestBodyDTO);
        }
        return bodyDTOS;
    }

    private List<TbpRequestBodyDTO> getRequestedPeriod(TbpRequestBodyDTO tbpRequestBodyDTO) {
        Map<Integer, Map<String, Integer>> months;
        List<TbpRequestBodyDTO> bodyDTOS;
        int numberOfMonths = DateUtil.getNumberMonthsBetweenDates(tbpRequestBodyDTO.getStartDate(), tbpRequestBodyDTO.getEndDate());
        months = DateUtil.getStartEndDatesOfMonths(new TbpPeriodDTO(tbpRequestBodyDTO.getStartDate(), tbpRequestBodyDTO.getEndDate(), numberOfMonths));
        bodyDTOS = getTbpRequestBodyDTOS(months, DateUtil.getYear(tbpRequestBodyDTO.getStartDate()), tbpRequestBodyDTO);
        return bodyDTOS;
    }

    private List<TbpRequestBodyDTO> getTbpRequestBodyDTOS(Map<Integer, Map<String, Integer>> months, int year, TbpRequestBodyDTO dto) {
        List<TbpRequestBodyDTO> bodyDTOS = new ArrayList<>();
        months.forEach((month, startEndMap) -> {
            StringBuilder startDateBuilder = new StringBuilder(year + DELIMITER);
            StringBuilder endDateBuilder = new StringBuilder(year + DELIMITER) ;
            startDateBuilder.append(composeDayAndMonth(month, startEndMap, START_STRING));
            endDateBuilder.append(composeDayAndMonth(month, startEndMap, END_STRING));
            TbpRequestBodyDTO bodyDTO = new TbpRequestBodyDTO(dto.getIdTbp(), startDateBuilder.toString(), endDateBuilder.toString(), dto.getUsername(), dto.getPassword());
            bodyDTOS.add(bodyDTO);
        });
        return bodyDTOS;
    }

    private String composeDayAndMonth(Integer month, Map<String, Integer> startEndMap, String startOrEnd) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format(TWO_DIGITS_FORMAT, month) + DELIMITER);
        builder.append(String.format(TWO_DIGITS_FORMAT, startEndMap.get(startOrEnd)));
        return builder.toString();
    }

    private boolean isMultipleMonths(TbpRequestBodyDTO tbpRequestBodyDTO) {
        return getNumberMonthsBetweenDates(tbpRequestBodyDTO.getStartDate(), tbpRequestBodyDTO.getEndDate()) != 0;
    }
}
