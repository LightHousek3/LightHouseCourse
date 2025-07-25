package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {

    private int orderID;
    private int customerID;
    private Timestamp orderDate;
    private double totalAmount;
    private String status; // 'pending', 'completed', 'refunded', 'refund_pending'
    private String paymentMethod; // 'VNPAY' hoặc 'MOMO'
    private String paymentTransactionID;
    private String paymentData;

    private Customer customer;

    private String userName;

    private List<OrderDetail> orderDetails = new ArrayList<>();

    private Map<String, Object> attributes;

    public Order() {
        this.orderDetails = new ArrayList<>();
        this.attributes = new HashMap<>();
    }

    public Order(int customerID, double totalAmount, String paymentMethod) {
        this.customerID = customerID;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.orderDate = new Timestamp(System.currentTimeMillis());
        this.status = "pending";
        this.orderDetails = new ArrayList<>();
        this.attributes = new HashMap<>();
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

    // Backward compatibility
    public int getUserID() {
        return customerID;
    }

    public void setUserID(int userID) {
        this.customerID = userID;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentTransactionID() {
        return paymentTransactionID;
    }

    public void setPaymentTransactionID(String paymentTransactionID) {
        this.paymentTransactionID = paymentTransactionID;
    }

    public String getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(String paymentData) {
        this.paymentData = paymentData;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        if (this.orderDetails == null) {
            this.orderDetails = new ArrayList<>();
        }
        this.orderDetails.add(orderDetail);
    }

    public void setAttribute(String name, Object value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes != null ? attributes.get(name) : null;
    }

    public boolean getBooleanAttribute(String name, boolean defaultValue) {
        Object value = getAttribute(name);
        return (value instanceof Boolean) ? (Boolean) value : defaultValue;
    }

    public boolean isAttributeTrue(String name) {
        return getBooleanAttribute(name, false);
    }

    // Helper
    public boolean isPending() {
        return "pending".equals(status);
    }

    public boolean isCompleted() {
        return "completed".equals(status);
    }

    public boolean isCancelled() {
        return "cancelled".equals(status);
    }

    public boolean isRefunded() {
        return "refunded".equals(status);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", customerID=" + customerID +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentTransactionID='" + paymentTransactionID + '\'' +
                ", paymentData='" + paymentData + '\'' +
                ", customer=" + customer +
                ", userName='" + userName + '\'' +
                ", orderDetails=" + orderDetails +
                '}';
    }
}
