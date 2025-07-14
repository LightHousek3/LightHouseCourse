/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Represents a user's progress in a course.
 *
 * @author DangPH - CE180896
 */
public class CourseProgress {
    private int progressID;
    private int customerID;
    private int courseID;
    private BigDecimal completionPercentage;
    private Timestamp lastAccessDate;
    private boolean isCompleted;

    // Non-DB fields for display
    private String courseName;
    private String userName;

    public CourseProgress() {
        this.completionPercentage = BigDecimal.ZERO;
    }

    public CourseProgress(int userID, int courseID) {
        this.customerID = userID;
        this.courseID = courseID;
        this.completionPercentage = BigDecimal.ZERO;
    }

    public CourseProgress(int progressID, int userID, int courseID, BigDecimal completionPercentage,
            Timestamp lastAccessDate, boolean isCompleted) {
        this.progressID = progressID;
        this.customerID = userID;
        this.courseID = courseID;
        this.completionPercentage = completionPercentage;
        this.lastAccessDate = lastAccessDate;
        this.isCompleted = isCompleted;
    }

    public int getProgressID() {
        return progressID;
    }

    public void setProgressID(int progressID) {
        this.progressID = progressID;
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

    public BigDecimal getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(BigDecimal completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public Timestamp getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(Timestamp lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public boolean isIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    @Override
    public String toString() {
        return "CourseProgress{" +
                "progressID=" + progressID +
                ", userID=" + customerID +
                ", courseID=" + courseID +
                ", completionPercentage=" + completionPercentage +
                ", lastAccessDate=" + lastAccessDate +
                ", isCompleted=" + isCompleted +
                '}';
    }

}