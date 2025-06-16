/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 * PaymentTransaction model class
 *
 * @author DangPH - CE180896
 */
public class PaymentTransaction {
    private int transactionID;
    private Integer orderID; // Can be null if it is a refund transaction
    private Integer refundRequestID; // Can be null if it is a payment transaction
    private String transactionType; // 'payment', 'refund'
    private String provider; // 'VNPAY'
    private String providerTransactionID; // Transaction ID from the provider
    private String bankAccountInfo; // Bank account information for refunds
    private String responseData; // Response data from payment gateway
    private String requestData; // Request data sent to payment gateway
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Additional information
    private String userName;
    private String courseName;

    // Constructors
    public PaymentTransaction() {
    }

    public PaymentTransaction(String transactionType, String provider) {
        this.transactionType = transactionType;
        this.provider = provider;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public Integer getRefundRequestID() {
        return refundRequestID;
    }

    public void setRefundRequestID(Integer refundRequestID) {
        this.refundRequestID = refundRequestID;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderTransactionID() {
        return providerTransactionID;
    }

    public void setProviderTransactionID(String providerTransactionID) {
        this.providerTransactionID = providerTransactionID;
    }

    public String getBankAccountInfo() {
        return bankAccountInfo;
    }

    public void setBankAccountInfo(String bankAccountInfo) {
        this.bankAccountInfo = bankAccountInfo;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // Helper methods
    public boolean isPayment() {
        return "payment".equals(transactionType);
    }

    public boolean isRefund() {
        return "refund".equals(transactionType);
    }

    @Override
    public String toString() {
        return "PaymentTransaction{" + "transactionID=" + transactionID + ", orderID=" + orderID + ", refundRequestID="
                + refundRequestID + ", transactionType=" + transactionType + ", provider=" + provider
                + ", providerTransactionID=" + providerTransactionID + ", bankAccountInfo=" + bankAccountInfo
                + ", responseData=" + responseData + ", requestData=" + requestData + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + ", userName=" + userName + ", courseName=" + courseName + '}';
    }
}