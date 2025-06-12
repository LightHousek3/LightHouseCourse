/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Represents an item in a user's shopping cart.
 *
 * @author DangPH - CE180896
 */
public class CartItem {

    private Course course;
    private double price;

    public CartItem() {
    }

    public CartItem(Course course, double price) {
        this.course = course;
        this.price = price;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "CartItem{"
                + "courseID=" + course.getCourseID()
                + ", courseName='" + course.getName() + '\''
                + ", price=" + price
                + '}';
    }
}
