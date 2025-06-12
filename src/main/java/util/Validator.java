package util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 */
public class Validator {
    // Regular expressions for validation
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String USERNAME_REGEX = "^[A-Za-z0-9_]{3,20}$";
    private static final String PASSWORD_REGEX = "^.{6,}$"; // At least 6 characters
    private static final String PHONE_REGEX = "^\\d{10,11}$"; // 10-11 digits
    
    /**
     * Validate an email address.
     * 
     * @param email The email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(EMAIL_REGEX, email);
    }
    
    /**
     * Validate a username.
     * 
     * @param username The username to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(USERNAME_REGEX, username);
    }
    
    /**
     * Validate a password.
     * 
     * @param password The password to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(PASSWORD_REGEX, password);
    }
    
    /**
     * Validate a phone number.
     * 
     * @param phone The phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(PHONE_REGEX, phone);
    }
    
    /**
     * Validate a numeric input.
     * 
     * @param input The input to validate
     * @return true if valid number, false otherwise
     */
    public static boolean isValidNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate that a string is not empty.
     * 
     * @param input The input to validate
     * @return true if not empty, false otherwise
     */
    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }
    
} 