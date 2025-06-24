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
    private int customerID; // Changed from userID to customerID
    private Timestamp requestDate;
    private String status; // 'pending', 'approved', 'rejected'
    private double refundAmount;
    private String reason; // Required reason from user
    private Timestamp processedDate;
    private int superUserID; // Changed from processedBy to superUserID (Admin who processed the request)
    private String adminMessage; // Required message from admin when processing
    private int refundPercentage; // Default 80%

    // Additional information for display purposes
    private String customerName; // Changed from userName to customerName
    private double originalAmount;
    private String adminName;
    private String courseName; // For display purposes, showing affected course(s)

    /**
     * Default constructor
     */
    public RefundRequest() {
    }

    /**
     * Constructor with essential fields
     *
     * @param orderID      The order ID
     * @param customerID   The customer ID
     * @param reason       The reason for refund request (required)
     * @param refundAmount The amount to be refunded
     */
    public RefundRequest(int orderID, int customerID, String reason, double refundAmount) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.reason = reason;
        this.refundAmount = refundAmount;
        this.requestDate = new Timestamp(System.currentTimeMillis());
        this.status = "pending";
        this.refundPercentage = 80; // Default 80%
    }

    /**
     * Legacy constructor for backward compatibility
     *
     * @param orderID      The order ID
     * @param userID       The user ID (now treated as customer ID)
     * @param reason       The reason for refund request (required)
     * @param refundAmount The amount to be refunded
     * @param legacy       Flag to indicate legacy constructor
     */
    public RefundRequest(int orderID, int userID, String reason, double refundAmount, boolean legacy) {
        this.orderID = orderID;
        this.customerID = userID; // Use userID as customerID
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

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    // Legacy methods for backward compatibility
    public int getUserID() {
        return customerID;
    }

    public void setUserID(int userID) {
        this.customerID = userID;
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

    public int getSuperUserID() {
        return superUserID;
    }

    public void setSuperUserID(int superUserID) {
        this.superUserID = superUserID;
    }

    // Legacy methods for backward compatibility
    public Integer getProcessedBy() {
        return superUserID;
    }

    public void setProcessedBy(Integer processedBy) {
        this.superUserID = processedBy;
    }

    public String getAdminMessage() {
        return adminMessage;
    }

    public void setAdminMessage(String adminMessage) {
        this.adminMessage = adminMessage;
    }

    public int getRefundPercentage() {
        return refundPercentage;
    }

    public void setRefundPercentage(int refundPercentage) {
        this.refundPercentage = refundPercentage;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // Legacy methods for backward compatibility
    public String getUserName() {
        return customerName;
    }

    public void setUserName(String userName) {
        this.customerName = userName;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getCourseName() {
        return courseName;
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

    @Override
    public String toString() {
        return "RefundRequest{" + "refundID=" + refundID + ", orderID=" + orderID + ", customerID=" + customerID
                + ", requestDate=" + requestDate + ", status=" + status + ", refundAmount=" + refundAmount + ", reason="
                + reason + ", processedDate=" + processedDate + ", superUserID=" + superUserID + ", adminMessage="
                + adminMessage + ", refundPercentage=" + refundPercentage
                + ", customerName=" + customerName + ", originalAmount=" + originalAmount
                + ", adminName=" + adminName + ", courseName=" + courseName + '}';
    }
}
