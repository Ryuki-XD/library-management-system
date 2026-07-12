package com.librarysystem.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for password hashing using SHA-256.
 */
public final class PasswordUtils {

    private PasswordUtils() {
        // Utility class — prevent instantiation
    }

    /**
     * Hashes a plain-text password using SHA-256.
     *
     * @param password the plain-text password
     * @return the hex-encoded hash string
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies a plain-text password against a stored hash.
     *
     * @param password the plain-text password to verify
     * @param storedHash the previously stored hash
     * @return true if the password matches
     */
    public static boolean verifyPassword(String password, String storedHash) {
        String hashedInput = hashPassword(password);
        return hashedInput.equals(storedHash);
    }

    /**
     * Converts a byte array to a lowercase hexadecimal string.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
