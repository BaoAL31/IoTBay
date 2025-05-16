package model;

import java.util.Date;

public class Delivery {
    private String trackingId;
    private String orderId;
    private String userId;
    private String status;
    private Date estimatedDeliveryDate;
    private String carrier;

    public Delivery() {}

    public Delivery(String trackingId, String orderId, String userId, String status, Date estimatedDeliveryDate, String carrier) {
        this.trackingId = trackingId;
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.carrier = carrier;
    }

    // Getters and Setters
    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(Date estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
}
