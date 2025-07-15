package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the details of an order in the system.
 */
public class OrderDetail implements Serializable {
    private int orderDetailID;
    private int orderID;
    private int courseID;
    private double price;

    // Store additional custom attributes
    private Map<String, Object> attributes;

    // Additional field to store course info
    private Course course;

    // Temporary field to store order date for refund purposes
    private Timestamp orderDate;

    public OrderDetail() {
        attributes = new HashMap<>();
    }

    public OrderDetail(int orderDetailID, int orderID, int courseID, double price) {
        this.orderDetailID = orderDetailID;
        this.orderID = orderID;
        this.courseID = courseID;
        this.price = price;
        attributes = new HashMap<>();
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

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Set a custom attribute for this order detail
     * 
     * @param name  The attribute name
     * @param value The attribute value
     */
    public void setAttribute(String name, Object value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(name, value);
    }

    /**
     * Get a custom attribute for this order detail
     * 
     * @param name The attribute name
     * @return The attribute value, or null if not found
     */
    public Object getAttribute(String name) {
        return attributes != null ? attributes.get(name) : null;
    }

    /**
     * Get a boolean attribute with a default value
     * 
     * @param name         The attribute name
     * @param defaultValue The default value if the attribute is not found
     * @return The attribute value, or the default value if not found
     */
    public boolean getBooleanAttribute(String name, boolean defaultValue) {
        Object value = getAttribute(name);
        return (value instanceof Boolean) ? (Boolean) value : defaultValue;
    }

    /**
     * Check if a boolean attribute is true
     * 
     * @param name The attribute name
     * @return true if the attribute exists and is true, false otherwise
     */
    public boolean isAttributeTrue(String name) {
        return getBooleanAttribute(name, false);
    }

    /**
     * Get a double attribute with a default value
     * 
     * @param name         The attribute name
     * @param defaultValue The default value if the attribute is not found
     * @return The attribute value, or the default value if not found
     */
    public double getDoubleAttribute(String name, double defaultValue) {
        Object value = getAttribute(name);
        return (value instanceof Double) ? (Double) value : defaultValue;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderDetailID=" + orderDetailID +
                ", orderID=" + orderID +
                ", courseID=" + courseID +
                ", price=" + price +
                '}';
    }
}