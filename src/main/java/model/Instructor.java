/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 * Instructor model class
 *
 * @author DangPH - CE180896
 */
public class Instructor {

    private int instructorID;
    private int userID;
    private String biography;
    private String specialization;
    private Date approvalDate;

    // Statistical fields (not stored in database)
    private int totalCourses;
    private int totalStudents;

    // Associated user
    private User user;

    // Additional fields from User
    private String name;
    private String email;

    // Default constructor
    public Instructor() {
    }

    // Constructor with parameters
    public Instructor(int instructorId, int userId, String biography, String specialization,
            Date approvalDate) {
        this.instructorID = instructorId;
        this.userID = userId;
        this.biography = biography;
        this.specialization = specialization;
        this.approvalDate = approvalDate;
    }

    // Getters and setters
    public int getInstructorID() {
        return instructorID;
    }

    public void setInstructorID(int instructorID) {
        this.instructorID = instructorID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    // Statistical getters and setters
    public int getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(int totalCourses) {
        this.totalCourses = totalCourses;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    // User association
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Instructor{" + "instructorID=" + instructorID + ", userID=" + userID + ", biography=" + biography + ", specialization=" + specialization + ", approvalDate=" + approvalDate + ", totalCourses=" + totalCourses + ", totalStudents=" + totalStudents + ", user=" + user + '}';
    }

}
