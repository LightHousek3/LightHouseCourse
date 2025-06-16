package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;

/**
 * Utility class for password encryption and verification.
 */
public class PasswordEncrypt {
    
    /**
     * Encrypt a password using SHA-256 algorithm.
     * 
     * @param password The plain text password
     * @return The encrypted password
     */
    public static String encryptSHA256(String password) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Add password bytes to digest
            md.update(password.getBytes());
            
            // Get the hash's bytes
            byte[] bytes = md.digest();
            
            // Convert to hexadecimal format
            return Hex.encodeHexString(bytes);
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Verify a password against an encrypted password.
     * 
     * @param plainPassword The plain text password to check
     * @param encryptedPassword The encrypted password to check against
     * @return true if they match, false otherwise
     */
    public static boolean verifySHA256(String plainPassword, String encryptedPassword) {
        String encryptedInput = encryptSHA256(plainPassword);
        return encryptedInput != null && encryptedInput.equals(encryptedPassword);
    }
} 