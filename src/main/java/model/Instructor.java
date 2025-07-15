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
    private String username;
    private String password;
    private String fullName;
    private String phone;
    private String address;
    private String avatar;
    private boolean active;
    private String authProvider;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public void setBio(String bio) {
        this.biography = bio;
    }

    @Override
    public String toString() {
        return "Instructor{"
                + "instructorID=" + instructorID
                + ", superUserID=" + superUserID
                + ", biography='" + biography + '\''
                + ", specialization='" + specialization + '\''
                + ", approvalDate=" + approvalDate
                + ", totalCourses=" + totalCourses
                + ", totalStudents=" + totalStudents
                + ", username='" + username + '\''
                + ", fullName='" + fullName + '\''
                + ", email='" + email + '\''
                + ", phone='" + phone + '\''
                + ", address='" + address + '\''
                + ", avatar='" + avatar + '\''
                + '}';
    }

    public void setAuthProviderId(String authProviderId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    

}
