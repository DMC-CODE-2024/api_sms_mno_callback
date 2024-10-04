package com.gl.ceir.config.controller;

import com.gl.ceir.config.dto.DeliveryInfoNotification;
import com.gl.ceir.config.dto.DeliveryInfoNotificationDto;
import com.gl.ceir.config.model.app.CfgFeatureAlert;
import com.gl.ceir.config.model.app.Notification;
import com.gl.ceir.config.model.constants.DeliveryStatus;
import com.gl.ceir.config.repository.app.CfgFeatureAlertRepository;
import com.gl.ceir.config.repository.app.NotificationRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/callback"})
public class CallbackController {
    @Value("${smart-response-string}")
    private String smartResponseString;

    private static final Logger logger = LogManager.getLogger(com.gl.ceir.config.controller.CallbackController.class);

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    CfgFeatureAlertRepository cfgFeatureAlertRepository;

    @RequestMapping(path = {"/kanel"}, method = {RequestMethod.GET})
    public ResponseEntity<String> kanelCallback(@RequestParam(required = true) String myId, @RequestParam(required = true) String operatorName, @RequestParam(required = true) String answer, @RequestParam(required = true) int status, @RequestParam(required = true) Long dlrvTime) {
        try {
            LocalDateTime deliveryTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dlrvTime.longValue()), ZoneId.systemDefault());
            logger.info("Callback from kanel: operatorName: " + operatorName + ", answer: " + answer + ", corelationId: " + myId + ", status: " + DeliveryStatus.fromValue(status) + ", deliveryTime: " + deliveryTime);
            Notification noti = this.notificationRepository.findByCorelationIdAndOperatorName(myId, operatorName);
            if (noti != null) {
                noti.setDeliveryTime(LocalDateTime.now());
                noti.setDeliveryStatus(status);
                this.notificationRepository.save(noti);
            }
            return new ResponseEntity("OK", HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Exception in kanel callback api: " + e);
            Optional<CfgFeatureAlert> alert = this.cfgFeatureAlertRepository.findByAlertId("alert1201");
            logger.error("Raising alert1207: Exception in " + operatorName + " callback-> " + e);
            System.out.println("Raising alert1207: Exception in " + operatorName + " callback-> " + e);
            if (alert.isPresent())
                raiseAnAlert(((CfgFeatureAlert)alert.get()).getAlertId(), operatorName, "SMS_MODULE", 0);
            return new ResponseEntity("Request Failed. Please try again later", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @RequestMapping(path = {"/smart"}, method = {RequestMethod.POST})
    public ResponseEntity<String> smartCallback(@RequestBody DeliveryInfoNotificationDto deliveryInfoNotificationDto) {
        try {
            logger.info("Callback from smart: " + deliveryInfoNotificationDto.toString());
            DeliveryInfoNotification deliveryInfoNotification = deliveryInfoNotificationDto.getDeliveryInfoNotification();
            if (deliveryInfoNotification != null && deliveryInfoNotification.getDeliveryInfo() != null && deliveryInfoNotification.getDeliveryInfo().getDeliveryStatus() != null && deliveryInfoNotification.getDeliveryInfo().getAddress() != null) {
                String deliveryStatus = deliveryInfoNotification.getDeliveryInfo().getDeliveryStatus();
                if (deliveryStatus.equals(this.smartResponseString)) {
                    String msisdn = deliveryInfoNotification.getDeliveryInfo().getAddress().replace("tel:+", "");
                    logger.info("deliveryStatus: " + deliveryStatus + ", deliveryAddress: " + msisdn + ", corelationId: " + deliveryInfoNotificationDto.getDeliveryInfoNotification().getCallbackData());
                    Notification noti = this.notificationRepository.findByCorelationIdAndOperatorName(deliveryInfoNotificationDto.getDeliveryInfoNotification().getCallbackData(), "smart");
                    if (noti != null) {
                        noti.setDeliveryTime(LocalDateTime.now());
                        noti.setDeliveryStatus(1);
                        this.notificationRepository.save(noti);
                    }
                } else {
                    Optional<CfgFeatureAlert> optional = this.cfgFeatureAlertRepository.findByAlertId("alert1201");
                    logger.error("Raising alert1207: Exception in smart callback-> Unknown status: " + deliveryStatus);
                    System.out.println("Raising alert1207: Exception in smart callback-> Unknown status: " + deliveryStatus);
                    if (optional.isPresent())
                        raiseAnAlert(((CfgFeatureAlert)optional.get()).getAlertId(), "smart: Unknown status: " + deliveryStatus, "SMS_MODULE", 0);
                }
                return new ResponseEntity("OK", HttpStatus.OK);
            }
            Optional<CfgFeatureAlert> alert = this.cfgFeatureAlertRepository.findByAlertId("alert1201");
            logger.error("Raising alert1207: Exception in smart callback-> Delivery status not found or null");
            System.out.println("Raising alert1207: Exception in smart callback-> Delivery status not found or null");
            if (alert.isPresent())
                raiseAnAlert(((CfgFeatureAlert)alert.get()).getAlertId(), "smart: Delivery status not found or null", "SMS_MODULE", 0);
            return new ResponseEntity("Delivery status not found or null", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.info("Exception in smart callback api: {}" + e);
            Optional<CfgFeatureAlert> alert = this.cfgFeatureAlertRepository.findByAlertId("alert1201");
            logger.error("Raising alert1207: Exception in smart callback->" + e);
            System.out.println("Raising alert1207: Exception in smart callback->" + e);
            if (alert.isPresent())
                raiseAnAlert(((CfgFeatureAlert)alert.get()).getAlertId(), "smart", "SMS_MODULE", 0);
            return new ResponseEntity("Request Failed. Please try again later", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public static LocalDateTime unixTimeToLocalDateTime(long unixTime) {
        Instant instant = Instant.ofEpochSecond(unixTime);
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTime), ZoneId.systemDefault());
    }

    public void raiseAnAlert(String alertCode, String alertMessage, String alertProcess, int userId) {
        try {
            String path = System.getenv("APP_HOME") + "alert/start.sh";
            ProcessBuilder pb = new ProcessBuilder(new String[] { path, alertCode, alertMessage, alertProcess, String.valueOf(userId) });
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            String response = null;
            while ((line = reader.readLine()) != null)
                response = response + response;
            logger.info("Alert is generated :response " + response);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Not able to execute Alert mgnt jar " + ex.getLocalizedMessage() + " ::: " + ex.getMessage());
        }
    }
}

