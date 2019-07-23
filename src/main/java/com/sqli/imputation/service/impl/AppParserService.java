package com.sqli.imputation.service.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nerdforge.unxml.Parsing;
import com.nerdforge.unxml.factory.ParsingFactory;
import com.nerdforge.unxml.parsers.Parser;
import com.sqli.imputation.service.dto.AppChargeDTO;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

@Service
public class AppParserService {
    private static final int LOGIN_POSITION = 3;
    private static final int DAY_POSITION = 5;
    private static final int CHARGE_POSITION = 6;
    private static final String VALUE = "value";

    public List<AppChargeDTO> parse(String xml) {

        Parsing parsing = ParsingFactory.getInstance().create();
        Document document = parsing.xml().document(xml);

        Parser<ArrayNode> parser = parsing.arr("//return/item",
            parsing.arr("item", parsing.obj().attribute("key").attribute(VALUE))
        ).build();

        ArrayNode jsonNodes = parser.apply(document);
        return getDtosFromArrayNode(jsonNodes);
    }

    private List<AppChargeDTO> getDtosFromArrayNode(ArrayNode jsonNodes) {
        List<AppChargeDTO> appChargeDTOS = new ArrayList<>();
        jsonNodes.forEach(item -> {
            AppChargeDTO appChargeDTO = new AppChargeDTO();
            appChargeDTO.setAppLogin(item.get(LOGIN_POSITION).get(VALUE).textValue());
            appChargeDTO.setDay(Integer.parseInt(item.get(DAY_POSITION).get(VALUE).textValue()));
            appChargeDTO.setCharge(Double.parseDouble(item.get(CHARGE_POSITION).get(VALUE).textValue()));
            appChargeDTOS.add(appChargeDTO);
        });
        return appChargeDTOS;
    }


}
