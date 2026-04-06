package com.dev.cloudstorage.service.impl;

import com.dev.cloudstorage.model.*;
import com.dev.cloudstorage.repository.*;
import com.dev.cloudstorage.service.BlockchainService;
import com.dev.cloudstorage.service.FileService;
import com.dev.cloudstorage.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileAccessRepository fileAccessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private BlockchainService blockchainService;

    @Value("${file.upload.dir:C:/uploads}")
    private String fileUploadDir;

    @Override
    public File uploadFile(MultipartFile file, Long userId) throws IOException {
        // Check if user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(fileUploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Encrypt file before saving
        byte[] fileBytes = file.getBytes();
        String encryptionKey = EncryptionUtil.generateSecureKey();
        byte[] encryptedFileBytes;
        try {
            encryptedFileBytes = EncryptionUtil.encrypt(fileBytes, encryptionKey);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting file: " + e.getMessage(), e);
        }

        // Save encrypted file to disk
        Files.write(filePath, encryptedFileBytes);

        // Calculate hash from original data (before encryption) for integrity check
        String fileHash = calculateSHA256(fileBytes);

        // Create file entity
        File fileInfo = new File();
        fileInfo.setName(uniqueFilename);
        fileInfo.setOriginalName(originalFilename);
        fileInfo.setFilePath(filePath.toString());
        fileInfo.setFileSize((long) encryptedFileBytes.length);
        fileInfo.setContentType(file.getContentType());
        fileInfo.setOwnerId(userId);
        fileInfo.setFileHash(fileHash);
        fileInfo.setEncryptionKey(encryptionKey);

        // Save to database
        File savedFile = fileRepository.save(fileInfo);

        // Add to blockchain
        blockchainService.addFileOperationToBlockchain(savedFile, "UPLOAD", userId);

        // Log the activity
        logActivity(userId, savedFile.getId(), "UPLOAD", "File uploaded and encrypted successfully");

        return savedFile;
    }

    @Override
    public byte[] downloadFile(Long fileId, Long userId) throws IOException {
        // Check if file exists and user has permission
        File file = getFileWithPermissionCheck(fileId, userId, FileAccess.PermissionType.READ);

        // Verify file integrity
        if (blockchainService.detectFileTampering(file)) {
            throw new SecurityException("File integrity compromised - tampering detected");
        }

        // Read encrypted file from disk
        Path filePath = Paths.get(file.getFilePath());
        byte[] encryptedFileBytes = Files.readAllBytes(filePath);

        // Decrypt file
        byte[] decryptedFileBytes;
        try {
            decryptedFileBytes = EncryptionUtil.decrypt(encryptedFileBytes, file.getEncryptionKey());
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting file: " + e.getMessage(), e);
        }

        // Log the activity
        logActivity(userId, fileId, "DOWNLOAD", "File downloaded and decrypted successfully");

        return decryptedFileBytes;
    }

    @Override
    public boolean deleteFile(Long fileId, Long userId) {
        // Check if file exists and user has permission
        File file = getFileWithPermissionCheck(fileId, userId, FileAccess.PermissionType.DELETE);

        // Mark as deleted in database
        file.setIsDeleted(true);
        fileRepository.save(file);

        // Add to blockchain
        blockchainService.addFileOperationToBlockchain(file, "DELETE", userId);

        // Log the activity
        logActivity(userId, fileId, "DELETE", "File marked as deleted");

        return true;
    }

    @Override
    public List<File> getUserFiles(Long userId) {
        return fileRepository.findByOwnerIdAndIsDeletedFalse(userId);
    }

    @Override
    public List<File> getAllFiles() {
        return fileRepository.findByIsDeletedFalse();
    }

    @Override
    public File getFileById(Long fileId) {
        return fileRepository.findById(fileId)
                .filter(file -> !file.getIsDeleted())
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
    }

    @Override
    public File getFileByIdAndOwner(Long fileId, Long ownerId) {
        return fileRepository.findById(fileId)
                .filter(file -> file.getOwnerId().equals(ownerId) && !file.getIsDeleted())
                .orElseThrow(() -> new IllegalArgumentException("File not found or access denied"));
    }

    @Override
    public boolean shareFile(Long fileId, Long currentUserId, Long targetUserId, String permissionType) {
        // Check if file exists and user has permission to share
        File file = getFileWithPermissionCheck(fileId, currentUserId, FileAccess.PermissionType.SHARE);

        // Check if target user exists
        userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

        // Create or update file access record
        Optional<FileAccess> existingAccess = fileAccessRepository.findByFileIdAndUserId(fileId, targetUserId);

        FileAccess fileAccess;
        if (existingAccess.isPresent()) {
            // Update existing access
            fileAccess = existingAccess.get();
            fileAccess.setPermissionType(FileAccess.PermissionType.valueOf(permissionType.toUpperCase()));
            fileAccess.setIsActive(true);
            fileAccess.setRevokedAt(null);
        } else {
            // Create new access record
            fileAccess = new FileAccess();
            fileAccess.setFileId(fileId);
            fileAccess.setUserId(targetUserId);
            fileAccess.setPermissionType(FileAccess.PermissionType.valueOf(permissionType.toUpperCase()));
            fileAccess.setGrantedBy(currentUserId);
        }

        fileAccessRepository.save(fileAccess);

        // Add to blockchain
        blockchainService.addFileOperationToBlockchain(file, "SHARE", currentUserId);

        // Log the activity
        logActivity(currentUserId, fileId, "SHARE", "File shared with user ID: " + targetUserId);

        return true;
    }

    @Override
    public boolean revokeShare(Long fileId, Long currentUserId, Long targetUserId) {
        // Check if file exists and user has permission to share
        File file = getFileWithPermissionCheck(fileId, currentUserId, FileAccess.PermissionType.SHARE);

        // Find and deactivate file access record
        Optional<FileAccess> fileAccessOpt = fileAccessRepository.findByFileIdAndUserId(fileId, targetUserId);

        if (fileAccessOpt.isPresent()) {
            FileAccess fileAccess = fileAccessOpt.get();
            fileAccess.setIsActive(false);
            fileAccess.setRevokedAt(java.time.LocalDateTime.now());
            fileAccessRepository.save(fileAccess);

            // Add to blockchain
            blockchainService.addFileOperationToBlockchain(file, "REVOKE_SHARE", currentUserId);

            // Log the activity
            logActivity(currentUserId, fileId, "REVOKE_SHARE", "File share revoked for user ID: " + targetUserId);

            return true;
        }

        return false;
    }

    @Override
    public List<File> getSharedFiles(Long userId) {
        // Get all active file accesses for this user
        List<FileAccess> fileAccesses = fileAccessRepository.findByUserIdAndIsActiveTrue(userId);

        // Get the actual files
        return fileAccesses.stream()
                .map(access -> fileRepository.findById(access.getFileId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(file -> !file.getIsDeleted())
                .toList();
    }

    private File getFileWithPermissionCheck(Long fileId, Long userId, FileAccess.PermissionType requiredPermission) {
        File file = fileRepository.findById(fileId)
                .filter(f -> !f.getIsDeleted())
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        // Check if user is the owner
        if (file.getOwnerId().equals(userId)) {
            return file;
        }

        // Check if user has required permission
        FileAccess fileAccess = fileAccessRepository.findByFileIdAndUserId(fileId, userId)
                .filter(access -> access.getIsActive())
                .orElseThrow(() -> new AccessDeniedException("Access denied"));

        if (!hasRequiredPermission(fileAccess.getPermissionType(), requiredPermission)) {
            throw new AccessDeniedException("Insufficient permissions");
        }

        return file;
    }

    private boolean hasRequiredPermission(FileAccess.PermissionType userPermission, FileAccess.PermissionType requiredPermission) {
        // Define permission hierarchy
        switch (requiredPermission) {
            case READ:
                return userPermission == FileAccess.PermissionType.READ ||
                       userPermission == FileAccess.PermissionType.WRITE ||
                       userPermission == FileAccess.PermissionType.SHARE ||
                       userPermission == FileAccess.PermissionType.DELETE;
            case WRITE:
            case SHARE:
                return userPermission == FileAccess.PermissionType.WRITE ||
                       userPermission == FileAccess.PermissionType.SHARE ||
                       userPermission == FileAccess.PermissionType.DELETE;
            case DELETE:
                return userPermission == FileAccess.PermissionType.DELETE;
            default:
                return false;
        }
    }

    private void logActivity(Long userId, Long fileId, String action, String details) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setFileId(fileId);
        log.setAction(action);
        log.setActionDetails(details);
        activityLogRepository.save(log);
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