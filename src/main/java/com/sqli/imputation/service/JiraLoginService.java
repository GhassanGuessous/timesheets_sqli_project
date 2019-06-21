package com.sqli.imputation.service;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

@Service
public class JiraLoginService {

    private static final String JIRA_AUTH_URL = "https://jira.nespresso.com/rest/gadget/1.0/login";
    public static final String JIRA_LOGIN_URL_FORMAT = "?os_username=%s&os_password=%s";

    public RestTemplate logIn(String login, String password) {
        RestTemplate restTemplate = new RestTemplate(ignoreSSL());
        String authURL = String.format(JIRA_AUTH_URL + JIRA_LOGIN_URL_FORMAT, login, password);
        restTemplate.exchange(authURL, HttpMethod.POST, HttpEntity.EMPTY, Object.class);
        return restTemplate;
    }

    public HttpComponentsClientHttpRequestFactory ignoreSSL() {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = null;
        try {
            sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Error while ignoring ssl certification");
        }

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom()
            .setSSLSocketFactory(csf)
            .build();
        HttpComponentsClientHttpRequestFactory requestFactory =
            new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        return requestFactory;
    }

    public HttpEntity<String> getTbpHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>("parameters", headers);
    }
}
