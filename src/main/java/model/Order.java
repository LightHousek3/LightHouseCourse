/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Order model class
 *
 * @author DangPH - CE180896
 */
public class Order {

    private int orderID;
    private int customerID;
    private Timestamp orderDate;
    private double totalAmount;
    private String status; // 'pending', 'completed', 'refunded'

    // Additional information
    private List<OrderDetail> orderDetails = new ArrayList<>();
    private Customer customer;

    // Constructors
    public Order() {
        this.orderDetails = new ArrayList<>();
    }

    public Order(int customerID, double totalAmount) {
        this.customerID = customerID;
        this.totalAmount = totalAmount;
        this.orderDate = new Timestamp(System.currentTimeMillis());
        this.status = "pending";
        this.orderDetails = new ArrayList<>();
    }

    // Getters and Setters
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

    // For backward compatibility
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // Helper methods
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
        return "Order{" + "orderID=" + orderID + ", customerID=" + customerID + ", orderDate=" + orderDate
                + ", totalAmount="
                + totalAmount + ", status=" + status + ", orderDetails=" + orderDetails + ", customer=" + customer
                + '}';
    }
}
