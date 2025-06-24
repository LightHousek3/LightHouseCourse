/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 * Represents a student's progress in a specific lesson
 *
 * @author DangPH - CE180896
 */
public class LessonProgress {
    private int progressID;
    private int customerID;
    private int lessonID;
    private boolean isCompleted;
    private Timestamp completionDate;
    private Timestamp lastAccessDate;

    // Additional fields for relationship
    private Customer customer;
    private Lesson lesson;

    // Constructors
    public LessonProgress() {
    }

    public LessonProgress(int customerID, int lessonID) {
        this.customerID = customerID;
        this.lessonID = lessonID;
        this.isCompleted = false;
        this.lastAccessDate = new Timestamp(System.currentTimeMillis());
    }

    public LessonProgress(int progressID, int customerID, int lessonID, boolean isCompleted,
            Timestamp completionDate, Timestamp lastAccessDate) {
        this.progressID = progressID;
        this.customerID = customerID;
        this.lessonID = lessonID;
        this.isCompleted = isCompleted;
        this.completionDate = completionDate;
        this.lastAccessDate = lastAccessDate;
    }

    // Getters and Setters
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

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }

    public boolean isIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Timestamp getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Timestamp completionDate) {
        this.completionDate = completionDate;
    }

    public Timestamp getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(Timestamp lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    @Override
    public String toString() {
        return "LessonProgress{" + "progressID=" + progressID + ", customerID=" + customerID +
                ", lessonID=" + lessonID + ", isCompleted=" + isCompleted +
                ", completionDate=" + completionDate + ", lastAccessDate=" + lastAccessDate + '}';
    }
}