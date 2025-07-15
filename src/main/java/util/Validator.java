package util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 */
public class Validator {

    // Regular expressions for validation
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; //email format (e.g. user@example.com)
    private static final String USERNAME_REGEX = "^[A-Za-z0-9_]{3,20}$"; //3-20 characters, letters, numbers, underscores only
    private static final String FULLNAME_REGEX = "^[A-Za-zÀ-ỹà-ỹ\\s'\\.\\-]{2,50}$"; // 2-50 characters, letters (incl. Vietnamese), spaces, ', ., -
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
     * Validate a fullname with advanced checks: - Not null or empty - 2-50
     * characters - Only letters, spaces, apostrophes, dots, hyphens, Vietnamese
     * accents - No numbers, emoji, consecutive spaces - At least 2 words - Not
     * all uppercase or all lowercase
     *
     * @param fullname The fullname to validate
     * @return null if valid, or error message string if invalid
     */
    public static boolean isValidFullname(String fullname) {
        // Check Not null or empty
        if (fullname == null || fullname.trim().isEmpty()) {
            return false;
        }

        String trimmed = fullname.trim();

        // Check length 2-50 characters
        if (trimmed.length() < 2 || trimmed.length() > 50) {
            return false;
        }

        // Check allowed characters
        if (!trimmed.matches(FULLNAME_REGEX)) {
            return false;
        }

        // No numbers allowed
        if (trimmed.matches(".*\\d.*")) {
            return false;
        }

        // No emoji or symbols (Unicode category So = Symbol Other)
        if (trimmed.matches(".*\\p{So}.*")) {
            return false;
        }

        // No consecutive spaces
        if (trimmed.matches(".*\\s{2,}.*")) {
            return false;
        }

        // At least two words
        if (trimmed.split("\\s+").length < 2) {
            return false;
        }

        // Must not be all uppercase or all lowercase
        if (trimmed.equals(trimmed.toUpperCase()) || trimmed.equals(trimmed.toLowerCase())) {
            return false;
        }
        
        return true;
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
