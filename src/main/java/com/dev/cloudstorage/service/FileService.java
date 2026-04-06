package com.dev.cloudstorage.service;

import com.dev.cloudstorage.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    File uploadFile(MultipartFile file, Long userId) throws IOException;
    byte[] downloadFile(Long fileId, Long userId) throws IOException;
    boolean deleteFile(Long fileId, Long userId);
    List<File> getUserFiles(Long userId);
    List<File> getAllFiles();
    File getFileById(Long fileId);
    File getFileByIdAndOwner(Long fileId, Long ownerId);
    boolean shareFile(Long fileId, Long currentUserId, Long targetUserId, String permissionType);
    boolean revokeShare(Long fileId, Long currentUserId, Long targetUserId);
    List<File> getSharedFiles(Long userId);
}