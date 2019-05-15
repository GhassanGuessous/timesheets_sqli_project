package com.sqli.imputation.service.factory;

import com.sqli.imputation.service.dto.AppRequestDTO;
import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import com.sqli.imputation.service.util.DateUtil;
import org.springframework.stereotype.Component;

@Component
public class RequestBodyFactory {

    private static final int FIRST_DAY_OF_MONTH = 1;

    public TbpRequestBodyDTO createTbpRequestBodyDTO(String idTbp, int year, int month) {
        TbpRequestBodyDTO tbpRequestBodyDTO = new TbpRequestBodyDTO(idTbp);
        tbpRequestBodyDTO.setStartDate(DateUtil.getDateOfFirstDay(year, month));
        tbpRequestBodyDTO.setEndDate(DateUtil.getDateOfLastDay(year, month));
        return tbpRequestBodyDTO;
    }

    public AppRequestDTO createAppRequestDTO(String agresso, int year, int month) {
        AppRequestDTO appRequestDTO = new AppRequestDTO(agresso, year, month, FIRST_DAY_OF_MONTH, DateUtil.getLastDayOfMonth(year, month));
        return appRequestDTO;
    }
}
