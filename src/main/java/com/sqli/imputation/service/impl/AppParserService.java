package com.sqli.imputation.service.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.factory.ParsingFactory;
import com.nerdforge.unxml.parsers.Parser;
import com.sqli.imputation.service.dto.AppChargeDTO;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppParserService {
    public static final int LOGIN_POSITION = 3;
    public static final int DAY_POSITION = 5;
    public static final int CHARGE_POSITION = 6;

    public List<AppChargeDTO> parse(String xml) {

        Parsing parsing = ParsingFactory.getInstance().create();
        Document document = parsing.xml().document(xml);

        Parser<ArrayNode> parser = parsing.arr("//return/item",
            parsing.arr("item", parsing.obj().attribute("key").attribute("value"))
        ).build();

        ArrayNode jsonNodes = parser.apply(document);
        return GetDtosFromArrayNode(jsonNodes);
    }

    private List<AppChargeDTO> GetDtosFromArrayNode(ArrayNode jsonNodes) {
        List<AppChargeDTO> appChargeDTOS = new ArrayList<>();
        jsonNodes.forEach(item -> {
            AppChargeDTO appChargeDTO = new AppChargeDTO();
            appChargeDTO.setAppLogin(item.get(LOGIN_POSITION).get("value").textValue());
            appChargeDTO.setDay(Integer.parseInt(item.get(DAY_POSITION).get("value").textValue()));
            appChargeDTO.setCharge(Double.parseDouble(item.get(CHARGE_POSITION).get("value").textValue()));
            appChargeDTOS.add(appChargeDTO);
        });
        return appChargeDTOS;
    }


}
