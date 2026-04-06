package com.dev.cloudstorage.util;

import org.springframework.stereotype.Component;

@Component
public class FileIntegrityUtil {
    
    /**
     * Verifies that a file has not been tampered with by comparing its current hash
     * with a stored hash
     */
    public static boolean verifyFileIntegrity(byte[] fileContent, String expectedHash) {
        String currentHash = calculateSHA256(fileContent);
        return expectedHash.equals(currentHash);
    }
    
    /**
     * Calculates the SHA-256 hash of the provided data
     */
    public static String calculateSHA256(byte[] data) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating SHA-256 hash", e);
        }
    }
}