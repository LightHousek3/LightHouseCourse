/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 * Represents a refund request in the system.
 *
 * @author DangPH - CE180896
 */
public class RefundRequest {

    private int refundID;
    private int orderID;
    private int userID;
    private Timestamp requestDate;
    private String status; // 'pending', 'approved', 'rejected'
    private double refundAmount;
    private String reason; // Required reason from user
    private Timestamp processedDate;
    private int processedBy; // Admin who processed the request
    private String adminMessage; // Required message from admin when processing
    private String refundTransactionID; // Transaction ID from payment provider
    private String refundData; // JSON response data from payment provider
    private int refundPercentage; // Default 80%
    private int courseID; // For course-specific refunds, null for full order refunds
    private String courseName;

    // Additional information for display purposes
    private String userName;
    private double originalAmount;
    private String paymentMethod;
    private Timestamp orderDate;
    private String adminName;

    /**
     * Default constructor
     */
    public RefundRequest() {
    }

    /**
     * Constructor with essential fields
     *
     * @param orderID The order ID
     * @param userID The user ID
     * @param reason The reason for refund request (required)
     * @param refundAmount The amount to be refunded
     */
    public RefundRequest(int orderID, int userID, String reason, double refundAmount) {
        this.orderID = orderID;
        this.userID = userID;
        this.reason = reason;
        this.refundAmount = refundAmount;
        this.requestDate = new Timestamp(System.currentTimeMillis());
        this.status = "pending";
        this.refundPercentage = 80; // Default 80%
    }

    // Getters and Setters
    public int getRefundID() {
        return refundID;
    }

    public void setRefundID(int refundID) {
        this.refundID = refundID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Timestamp processedDate) {
        this.processedDate = processedDate;
    }

    public Integer getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(Integer processedBy) {
        this.processedBy = processedBy;
    }

    public String getAdminMessage() {
        return adminMessage;
    }

    public void setAdminMessage(String adminMessage) {
        this.adminMessage = adminMessage;
    }

    public String getRefundTransactionID() {
        return refundTransactionID;
    }

    public void setRefundTransactionID(String refundTransactionID) {
        this.refundTransactionID = refundTransactionID;
    }

    public String getRefundData() {
        return refundData;
    }

    public void setRefundData(String refundData) {
        this.refundData = refundData;
    }

    public int getRefundPercentage() {
        return refundPercentage;
    }

    public void setRefundPercentage(int refundPercentage) {
        this.refundPercentage = refundPercentage;
    }

    public Integer getCourseID() {
        return courseID;
    }

    public void setCourseID(Integer courseID) {
        this.courseID = courseID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCourseName() {
        return courseName;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // Helper methods
    /**
     * Checks if the refund request is pending
     *
     * @return true if status is 'pending', false otherwise
     */
    public boolean isPending() {
        return "pending".equals(status);
    }

    /**
     * Checks if the refund request is approved
     *
     * @return true if status is 'approved', false otherwise
     */
    public boolean isApproved() {
        return "approved".equals(status);
    }

    /**
     * Checks if the refund request is rejected
     *
     * @return true if status is 'rejected', false otherwise
     */
    public boolean isRejected() {
        return "rejected".equals(status);
    }

    public void setProcessedBy(int processedBy) {
        this.processedBy = processedBy;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    @Override
    public String toString() {
        return "RefundRequest{" + "refundID=" + refundID + ", orderID=" + orderID + ", userID=" + userID + ", requestDate=" + requestDate + ", status=" + status + ", refundAmount=" + refundAmount + ", reason=" + reason + ", processedDate=" + processedDate + ", processedBy=" + processedBy + ", adminMessage=" + adminMessage + ", refundTransactionID=" + refundTransactionID + ", refundData=" + refundData + ", refundPercentage=" + refundPercentage + ", courseID=" + courseID + ", courseName=" + courseName + ", userName=" + userName + ", originalAmount=" + originalAmount + ", paymentMethod=" + paymentMethod + ", orderDate=" + orderDate + ", adminName=" + adminName + '}';
    }  

}
