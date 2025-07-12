package util;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 */
public class Validator {

    // Regular expressions for validation
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String USERNAME_REGEX = "^[A-Za-z0-9_]{3,20}$";
    private static final String FULLNAME_REGEX = "^[A-Za-z_ ]{3,50}$";
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
     * Validate a fullname.
     *
     * @param fullname The username to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidFullname(String fullname) {
        if (fullname == null || fullname.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(FULLNAME_REGEX, fullname);
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

    /**
     * Checks if a given string is null or empty after trimming whitespace.
     *
     * @param value the input string
     * @return true if the string is null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Validates that a string is not null and does not exceed the specified
     * maximum length.
     *
     * @param value the input string
     * @param maxLength the maximum allowed length
     * @return true if the string is valid, false otherwise
     */
    public static boolean isValidText(String value, int maxLength) {
        return value != null && value.length() <= maxLength;
    }

    /**
     * Validates if a string represents a valid integer within a given range.
     *
     * @param value the input string
     * @param min the minimum acceptable value
     * @param max the maximum acceptable value
     * @return true if the string is a valid integer within the range, false
     * otherwise
     */
    public static boolean isValidInteger(String value, int min, int max) {
        try {
            int number = Integer.parseInt(value);
            return number >= min && number <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates if a string represents a valid double within a given range.
     *
     * @param value the input string
     * @param min the minimum acceptable value
     * @param max the maximum acceptable value
     * @return true if the string is a valid double within the range, false
     * otherwise
     */
    public static boolean isValidDouble(String value, double min, double max) {
        try {
            double number = Double.parseDouble(value);
            return number >= min && number <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses a string to a double. Returns a default value if parsing fails.
     *
     * @param value the input string
     * @param defaultValue the value to return if parsing fails
     * @return the parsed double, or defaultValue if the string is not a valid
     * double
     */
    public static double parseDoubleOrDefault(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Parses a string to an integer. Returns a default value if parsing fails.
     *
     * @param value the input string
     * @param defaultValue the value to return if parsing fails
     * @return the parsed integer, or defaultValue if the string is not a valid
     * integer
     */
    public static int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Checks whether a given string is a valid integer. Handles null and empty
     * values gracefully.
     *
     * @param value the input string to check
     * @return true if the string is non-null, non-empty, and a valid integer;
     * false otherwise
     */
    public static boolean isValidInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Converts a list of integers into a comma-separated string.
     * <p>
     * For example: [1, 2, 3, 4] → "1,2,3,4"
     *
     * @param list the list of integers to join
     * @return a string with integers separated by commas, or an empty string if
     * the list is null or empty
     */
    public static String joinIntegerList(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
