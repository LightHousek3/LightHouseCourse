/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Represents a user in the system.
 *
 * @author DangPH - CE180896
 */
public class User {

    private int userID;
    private String username;
    private String password;
    private String email;
    private String role; // 'admin', 'user', or 'instructor'
    private boolean isActive;
    private String fullName;
    private String phone; // Matches 'Phone' column in database
    private String address;
    private String avatar;
    private String token; // Added to match 'Token' column in database
    private String authProvider; // 'local', 'google', 'facebook'
    private String authProviderId; // Provider's unique user ID

    public User() {
        this.authProvider = "local"; // Default to local authentication
        this.isActive = true; // Default to active user
    }

    public User(int userID, String username, String password, String email, String role, boolean isActive) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.authProvider = "local";
    }

    public User(int userID, String username, String password, String email, String role,
            boolean isActive, String fullName, String phone, String address) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.authProvider = "local";
    }

    public User(int userID, String username, String password, String email, String role,
            boolean isActive, String fullName, String phone, String address,
            String authProvider, String authProviderId) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.authProvider = authProvider;
        this.authProviderId = authProviderId;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    // Alias for getUserID for consistency in both naming conventions
    public int getUserId() {
        return userID;
    }

    public void setUserId(int userID) {
        this.userID = userID;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public String getAuthProviderId() {
        return authProviderId;
    }

    public void setAuthProviderId(String authProviderId) {
        this.authProviderId = authProviderId;
    }

    /**
     * Check if the user has admin role.
     *
     * @return true if user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.role);
    }

    /**
     * Check if the user has instructor role.
     *
     * @return true if user is an instructor, false otherwise
     */
    public boolean isInstructor() {
        return "instructor".equalsIgnoreCase(this.role);
    }

    /**
     * Check if the user has regular user/student role.
     *
     * @return true if user is a regular user/student, false otherwise
     */
    public boolean isCustomer() {
        return "user".equalsIgnoreCase(this.role);
    }

    /**
     * Check if the user is authenticated through social login.
     *
     * @return true if authenticated via social login, false if local
     * authentication
     */
    public boolean isSocialLogin() {
        return !"local".equalsIgnoreCase(this.authProvider);
    }

    @Override
    public String toString() {
        return "User{"
                + "userID=" + userID
                + ", username='" + username + '\''
                + ", email='" + email + '\''
                + ", role='" + role + '\''
                + ", isActive=" + isActive
                + ", authProvider='" + authProvider + '\''
                + '}';
    }
}
