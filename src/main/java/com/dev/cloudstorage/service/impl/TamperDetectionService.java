package com.dev.cloudstorage.service.impl;

import com.dev.cloudstorage.model.File;
import com.dev.cloudstorage.model.TamperedFile;
import com.dev.cloudstorage.repository.FileRepository;
import com.dev.cloudstorage.repository.TamperedFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class TamperDetectionService {
    
    @Autowired
    private FileRepository fileRepository;
    
    @Autowired
    private TamperedFileRepository tamperedFileRepository;

    public boolean checkFileIntegrity(Long fileId) throws IOException {
        // Get the file from database
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        
        // Calculate the current hash of the file on disk
        String currentHash = calculateFileHash(file.getFilePath());
        
        // Compare with the stored hash
        boolean isTampered = !file.getFileHash().equals(currentHash);
        
        if (isTampered) {
            // Log tampering
            TamperedFile tamperedFile = new TamperedFile();
            tamperedFile.setFileId(fileId);
            tamperedFile.setOriginalHash(file.getFileHash());
            tamperedFile.setCurrentHash(currentHash);
            tamperedFile.setStatus(TamperedFile.TamperStatus.DETECTED);
            tamperedFile.setNotes("File hash mismatch detected - potential tampering");
            
            tamperedFileRepository.save(tamperedFile);
        }
        
        return isTampered;
    }
    
    private String calculateFileHash(String filePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        return calculateSHA256(fileBytes);
    }
    
    private String calculateSHA256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating SHA-256 hash", e);
        }
    }
}