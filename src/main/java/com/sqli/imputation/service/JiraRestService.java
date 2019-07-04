package com.sqli.imputation.service;

import com.sqli.imputation.service.dto.jira.JiraIssuesResponseDTO;
import com.sqli.imputation.service.dto.jira.WorklogDTO;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

@Service
public class JiraRestService {

    private static final String JIRA_AUTH_URL = "https://jira.nespresso.com/rest/gadget/1.0/login";
    public static final String JIRA_LOGIN_URL_FORMAT = "?os_username=%s&os_password=%s";
    public static final String WORKLOG_URL = "/worklog";

    @Autowired
    private RestTemplate restTemplate;

    public void logIn(String login, String password) {
        restTemplate = new RestTemplate(ignoreSSL());
        String authURL = String.format(JIRA_AUTH_URL + JIRA_LOGIN_URL_FORMAT, login, password);
        restTemplate.exchange(authURL, HttpMethod.POST, HttpEntity.EMPTY, Object.class);
    }

    private HttpComponentsClientHttpRequestFactory ignoreSSL() {
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

    private HttpEntity<String> getTbpHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>("parameters", headers);
    }

    public ResponseEntity<JiraIssuesResponseDTO> getStories(String url) {
        return restTemplate.exchange(url, HttpMethod.GET, getTbpHttpHeaders(), JiraIssuesResponseDTO.class);
    }

    public ResponseEntity<WorklogDTO> getIssueWorklogs(String issueDTOSelf) {
        return restTemplate.exchange(issueDTOSelf + WORKLOG_URL, HttpMethod.GET, getTbpHttpHeaders(), WorklogDTO.class);
    }
}
