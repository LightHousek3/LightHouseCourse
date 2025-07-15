package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Represents a customer's progress in a course.
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
    private String customerName;

    public CourseProgress() {
        this.completionPercentage = BigDecimal.ZERO;
    }

    public CourseProgress(int customerID, int courseID) {
        this.customerID = customerID;
        this.courseID = courseID;
        this.completionPercentage = BigDecimal.ZERO;
    }

    public CourseProgress(int progressID, int customerID, int courseID, BigDecimal completionPercentage,
            Timestamp lastAccessDate, boolean isCompleted) {
        this.progressID = progressID;
        this.customerID = customerID;
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
        isCompleted = completed;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String toString() {
        return "CourseProgress{" +
                "progressID=" + progressID +
                ", customerID=" + customerID +
                ", courseID=" + courseID +
                ", completionPercentage=" + completionPercentage +
                ", lastAccessDate=" + lastAccessDate +
                ", isCompleted=" + isCompleted +
                '}';
    }
}
