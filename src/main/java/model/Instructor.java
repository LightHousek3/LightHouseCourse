/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 * Model class for Instructor based on database schema
 * 
 * @author DangPH - CE180896
 */
public class Instructor {
    private int instructorID;
    private int superUserID;
    private String biography;
    private String specialization;
    private Date approvalDate;

    // Additional fields not in database but used for statistics
    private int totalCourses;
    private int totalStudents;

    // Associated SuperUser
    private SuperUser superUser;

    // Convenience fields from SuperUser
    private String name;
    private String email;

    public Instructor() {
    }

    public Instructor(int instructorID, int superUserID, String biography, String specialization, Date approvalDate) {
        this.instructorID = instructorID;
        this.superUserID = superUserID;
        this.biography = biography;
        this.specialization = specialization;
        this.approvalDate = approvalDate;
    }

    // Getters and Setters
    public int getInstructorID() {
        return instructorID;
    }

    public void setInstructorID(int instructorID) {
        this.instructorID = instructorID;
    }

    public int getSuperUserID() {
        return superUserID;
    }

    public void setSuperUserID(int superUserID) {
        this.superUserID = superUserID;
    }

    // For backward compatibility
    public int getUserID() {
        return superUserID;
    }

    public void setUserID(int userID) {
        this.superUserID = userID;
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

    public SuperUser getSuperUser() {
        return superUser;
    }

    public void setSuperUser(SuperUser superUser) {
        this.superUser = superUser;
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
        return "Instructor{" + "instructorID=" + instructorID + ", superUserID=" + superUserID +
                ", biography=" + biography + ", specialization=" + specialization +
                ", approvalDate=" + approvalDate + ", totalCourses=" + totalCourses +
                ", totalStudents=" + totalStudents + '}';
    }
}
