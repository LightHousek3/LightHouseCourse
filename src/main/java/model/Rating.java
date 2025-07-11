/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 * Represents a customer's rating for a course.
 *
 * @author DangPH - CE180896
 */
public class Rating {

    private int ratingID;
    private int courseID;
    private int customerID;
    private int stars;
    private String comment;
    private Date createdAt;
    private Date updatedAt;

    // Non-DB fields for display purposes
    private String username;
    private String courseName;

    public Rating() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Rating(int courseID, int customerID, int stars, String comment) {
        this.courseID = courseID;
        this.customerID = customerID;
        this.stars = stars;
        this.comment = comment;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Legacy constructor for backward compatibility
    public Rating(int courseID, int userID, int stars, String comment, boolean legacy) {
        this.courseID = courseID;
        this.customerID = userID; // Use the userID as customerID
        this.stars = stars;
        this.comment = comment;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Rating(int ratingID, int courseID, int customerID, int stars, String comment, Date createdAt,
            Date updatedAt) {
        this.ratingID = ratingID;
        this.courseID = courseID;
        this.customerID = customerID;
        this.stars = stars;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getRatingID() {
        return ratingID;
    }

    public void setRatingID(int ratingID) {
        this.ratingID = ratingID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
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

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    @Override
    public String toString() {
        return "Rating{"
                + "ratingID=" + ratingID
                + ", courseID=" + courseID
                + ", customerID=" + customerID
                + ", stars=" + stars
                + ", comment='" + comment + '\''
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
