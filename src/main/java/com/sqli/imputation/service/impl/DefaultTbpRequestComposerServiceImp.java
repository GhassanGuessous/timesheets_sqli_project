package com.sqli.imputation.service.impl;

import com.sqli.imputation.service.TbpRequestComposerService;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.util.DateUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sqli.imputation.service.util.DateUtil.END;
import static com.sqli.imputation.service.util.DateUtil.getNumberMonthsBetweenDates;

@Service
public class DefaultTbpRequestComposerServiceImp implements TbpRequestComposerService {

    private static final String START = "start";
    private static final String DELIMITER = "-";
    private static final String TWO_DIGITS_FORMAT = "%02d";

    @Override
    public List<TbpRequestBodyDTO> dividePeriod(TbpRequestBodyDTO tbpRequestBodyDTO) {
        List<TbpRequestBodyDTO> bodyDTOS = new ArrayList<>();
        Map<Integer, Map<String, Integer>> months;

        if(isMultipleMonths(tbpRequestBodyDTO)){
            int numberOfMonths = DateUtil.getNumberMonthsBetweenDates(tbpRequestBodyDTO.getStartDate(), tbpRequestBodyDTO.getEndDate());
            months = DateUtil.getMonths(tbpRequestBodyDTO.getStartDate(), tbpRequestBodyDTO.getEndDate(), numberOfMonths);
            bodyDTOS = getTbpRequestBodyDTOS(months, DateUtil.getYear(tbpRequestBodyDTO.getStartDate()), tbpRequestBodyDTO.getIdTbp());
        }else{
            bodyDTOS.add(tbpRequestBodyDTO);
        }
        return bodyDTOS;
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
}
