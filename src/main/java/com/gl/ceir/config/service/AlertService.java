package com.gl.ceir.config.service;

import com.gl.ceir.config.dto.AlertBuilder;
import com.gl.ceir.config.dto.AlertDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AlertService {
    private final Logger log = LogManager.getLogger(getClass());

    @Value("${eirs.alert.url}")
    private String alertApiUrl;

    private final RestTemplate restTemplate;

    @Autowired
    Environment env;

    @Autowired
    public AlertService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void raiseAlert(String alertId, String alertMessage, Integer userId) {
        String profile = this.env.getProperty("nwl.input.schema");
        AlertDTO alertDTO = AlertBuilder.createAlert(alertId, alertMessage, userId, profile);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<AlertDTO> entity = new HttpEntity(alertDTO, (MultiValueMap)headers);
        ResponseEntity<String> response = this.restTemplate.exchange(this.alertApiUrl, HttpMethod.POST, entity, String.class, new Object[0]);
        if (response.getStatusCode().is2xxSuccessful()) {
            this.log.info("Alert raised with response: " + (String)response.getBody());
        } else {
            this.log.error("Failed to raise alert with response: " + (String)response.getBody());
        }
    }
}