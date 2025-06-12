/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Represents the details of an order in the system.
 *
 * @author DangPH - CE180896
 */
public class OrderDetail {

    private int orderDetailID;
    private int orderID;
    private int courseID;
    private double price;

    // Additional field to store course info
    private Course course;

    public OrderDetail() {
    }

    public OrderDetail(int orderDetailID, int orderID, int courseID, double price) {
        this.orderDetailID = orderDetailID;
        this.orderID = orderID;
        this.courseID = courseID;
        this.price = price;
    }

    public int getOrderDetailID() {
        return orderDetailID;
    }

    public void setOrderDetailID(int orderDetailID) {
        this.orderDetailID = orderDetailID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "OrderDetail{" + "orderDetailID=" + orderDetailID + ", orderID=" + orderID + ", courseID=" + courseID + ", price=" + price + ", course=" + course + '}';
    }

}
