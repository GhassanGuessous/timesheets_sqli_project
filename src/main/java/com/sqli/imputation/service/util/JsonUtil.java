package com.sqli.imputation.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqli.imputation.service.dto.AppRequestDTO;

import java.io.IOException;

public class JsonUtil {

    public static AppRequestDTO getAppRequestDTO(String requestDTO) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(requestDTO, AppRequestDTO.class);
    }
}
