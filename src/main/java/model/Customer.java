/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 * Represents a customer in the system.
 *
 * @author DangPH - CE180896
 */
public class Customer {

    private int customerID;
    private String username;
    private String password;
    private String email;
    private boolean isActive;
    private String fullName;
    private String phone;
    private String address;
    private String avatar;
    private String token;
    private Timestamp tokenExpires;
    private Timestamp lastTokenRequest;
    private String authProvider; // 'local', 'google', 'facebook'
    private String authProviderId; // Provider's unique user ID

    public Customer() {
        this.authProvider = "local"; // Default to local authentication
        this.isActive = true; // Default to active user
    }

    public Customer(int customerID, String username, String password, String email, boolean isActive) {
        this.customerID = customerID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.isActive = isActive;
        this.authProvider = "local";
    }

    public Customer(int customerID, String username, String password, String email,
            boolean isActive, String fullName, String phone, String address) {
        this.customerID = customerID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.isActive = isActive;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.authProvider = "local";
    }

    public Customer(int customerID, String username, String password, String email,
            boolean isActive, String fullName, String phone, String address,
            String authProvider, String authProviderId) {
        this.customerID = customerID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.isActive = isActive;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.authProvider = authProvider;
        this.authProviderId = authProviderId;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    // Alias for getCustomerID for consistency in both naming conventions
    public int getCustomerId() {
        return customerID;
    }

    public void setCustomerId(int customerID) {
        this.customerID = customerID;
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

    public java.sql.Timestamp getTokenExpires() {
        return tokenExpires;
    }

    public void setTokenExpires(java.sql.Timestamp tokenExpires) {
        this.tokenExpires = tokenExpires;
    }

    public java.sql.Timestamp getLastTokenRequest() {
        return lastTokenRequest;
    }

    public void setLastTokenRequest(java.sql.Timestamp lastTokenRequest) {
        this.lastTokenRequest = lastTokenRequest;
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
     * Check if the customer is authenticated through social login.
     *
     * @return true if authenticated via social login, false if local
     *         authentication
     */
    public boolean isSocialLogin() {
        return !"local".equalsIgnoreCase(this.authProvider);
    }

    /**
     * Check if the token is expired
     * 
     * @return true if token is expired or null, false otherwise
     */
    public boolean isTokenExpired() {
        if (this.tokenExpires == null) {
            return true;
        }
        return new java.util.Date().after(this.tokenExpires);
    }

    /**
     * Check if user can request a new token (60 seconds between requests)
     * 
     * @return true if user can request a new token, false otherwise
     */
    public boolean canRequestNewToken() {
        if (this.lastTokenRequest == null) {
            return true;
        }

        long currentTime = System.currentTimeMillis();
        long lastRequestTime = this.lastTokenRequest.getTime();

        // 60 seconds (60000 milliseconds) between requests
        return (currentTime - lastRequestTime) >= 60000;
    }

    @Override
    public String toString() {
        return "Customer{"
                + "customerID=" + customerID
                + ", username='" + username + '\''
                + ", email='" + email + '\''
                + ", isActive=" + isActive
                + ", authProvider='" + authProvider + '\''
                + '}';
    }
}