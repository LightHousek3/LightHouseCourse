/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Represents a SuperUser in the system (Admin or Instructor).
 *
 * @author DangPH - CE180896
 */
public class SuperUser {

    private int superUserID;
    private String username;
    private String password;
    private String email;
    private String role; // 'admin' or 'instructor'
    private boolean isActive;
    private String fullName;
    private String phone;
    private String address;
    private String avatar;

    public SuperUser() {
        this.isActive = true; // Default to active user
    }

    public SuperUser(int superUserID, String username, String password, String email, String role, boolean isActive) {
        this.superUserID = superUserID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
    }

    public SuperUser(int superUserID, String username, String password, String email, String role,
            boolean isActive, String fullName, String phone, String address) {
        this.superUserID = superUserID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }

    public SuperUser(int superUserID, String username, String password, String email, String role,
            boolean isActive, String fullName, String phone, String address, String avatar) {
        this.superUserID = superUserID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.avatar = avatar;
    }

    public int getSuperUserID() {
        return superUserID;
    }

    public void setSuperUserID(int superUserID) {
        this.superUserID = superUserID;
    }

    // Alias for getSuperUserID for consistency in both naming conventions
    public int getSuperUserId() {
        return superUserID;
    }

    public void setSuperUserId(int superUserID) {
        this.superUserID = superUserID;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Check if the superuser has admin role.
     *
     * @return true if superuser is an admin, false otherwise
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.role);
    }

    /**
     * Check if the superuser has instructor role.
     *
     * @return true if superuser is an instructor, false otherwise
     */
    public boolean isInstructor() {
        return "instructor".equalsIgnoreCase(this.role);
    }

    @Override
    public String toString() {
        return "SuperUser{"
                + "superUserID=" + superUserID
                + ", username='" + username + '\''
                + ", email='" + email + '\''
                + ", role='" + role + '\''
                + ", isActive=" + isActive
                + '}';
    }
}