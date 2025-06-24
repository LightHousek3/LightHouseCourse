/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 * Represents an item in a user's shopping cart.
 *
 * @author DangPH - CE180896
 */
public class CartItem {
    private int cartItemId;
    private int customerID;
    private int courseID;
    private double price;
    private Timestamp createdAt;

    // Additional fields for relationships
    private Course course;
    private Customer customer;

    public CartItem() {
    }

    public CartItem(int customerID, int courseID, double price) {
        this.customerID = customerID;
        this.courseID = courseID;
        this.price = price;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public CartItem(int cartItemId, int customerID, int courseID, double price, Timestamp createdAt) {
        this.cartItemId = cartItemId;
        this.customerID = customerID;
        this.courseID = courseID;
        this.price = price;
        this.createdAt = createdAt;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
        if (course != null) {
            this.courseID = course.getCourseID();
        }
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerID = customer.getCustomerID();
        }
    }

    @Override
    public String toString() {
        return "CartItem{"
                + "cartItemId=" + cartItemId
                + ", customerID=" + customerID
                + ", courseID=" + courseID
                + ", price=" + price
                + ", createdAt=" + createdAt
                + '}';
    }
}
