package com.gl.ceir.config.dto;

public class DeliveryInfoNotification {
    private String callbackData;

    private DeliveryInfo deliveryInfo;

    public String getCallbackData() {
        return this.callbackData;
    }

    public void setCallbackData(String callbackData) {
        this.callbackData = callbackData;
    }

    public DeliveryInfo getDeliveryInfo() {
        return this.deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }
}

