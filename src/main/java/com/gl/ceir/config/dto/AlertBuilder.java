package com.gl.ceir.config.dto;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class AlertBuilder {
    public static AlertDTO createAlert(String alertId, String alertMessage, Integer userId, String profile) {
        String ip, serverName;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            ip = localHost.getHostAddress();
            serverName = localHost.getHostName();
        } catch (UnknownHostException e) {
            ip = "127.0.0.1";
            serverName = "UnknownHost";
        }
        String MODULE_NAME = profile + ":national_whitelist";
        return AlertDTO.builder()
                .alertId(alertId)
                .alertMessage(alertMessage)
                .alertProcess(MODULE_NAME)
                .description("")
                .featureName("National Whitelist")
                .ip(ip)
                .priority("High")
                .remarks("")
                .serverName(serverName)
                .status(Integer.valueOf(0))
                .txnId(UUID.randomUUID().toString())
                .userId(userId)
                .build();
    }
}