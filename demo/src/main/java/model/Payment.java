package model;

import java.sql.Timestamp;

public class Payment {
    private int paymentId;
    private int orderId;
    private String method;
    private String cardNumber;
    private double amount;
    private Timestamp paidAt;
    private String status;

    // Constructor
    public Payment(int paymentId, int orderId, String method, String cardNumber, double amount, Timestamp paidAt, String status) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.method = method;
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Timestamp paidAt) {
        this.paidAt = paidAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", method='" + method + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", amount=" + amount +
                ", paidAt=" + paidAt +
                ", status='" + status + '\'' +
                '}';
    }
}