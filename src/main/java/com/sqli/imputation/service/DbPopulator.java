package com.sqli.imputation.service;

import org.springframework.web.client.RestTemplate;

public interface DbPopulator {

    void populate(RestTemplate restTemplate);
}
