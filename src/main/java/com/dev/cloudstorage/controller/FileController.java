package com.dev.cloudstorage.controller;

import com.dev.cloudstorage.model.File;
import com.dev.cloudstorage.service.FileService;
import com.dev.cloudstorage.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Get current user ID
            Long userId = SecurityUtils.getCurrentUserId();

            File uploadedFile = fileService.uploadFile(file, userId);
            return ResponseEntity.ok(uploadedFile);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            // Get current user ID
            Long userId = SecurityUtils.getCurrentUserId();

            byte[] fileBytes = fileService.downloadFile(fileId, userId);

            File fileInfo = fileService.getFileById(fileId);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.getOriginalName() + "\"")
                    .body(new ByteArrayResource(fileBytes));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        // Get current user ID
        Long userId = SecurityUtils.getCurrentUserId();

        boolean success = fileService.deleteFile(fileId, userId);

        if (success) {
            return ResponseEntity.ok().body("File deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to delete file");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<File>> getUserFiles() {
        // Get current user ID
        Long userId = SecurityUtils.getCurrentUserId();

        List<File> files = fileService.getUserFiles(userId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/shared")
    public ResponseEntity<List<File>> getSharedFiles() {
        // Get current user ID
        Long userId = SecurityUtils.getCurrentUserId();

        List<File> files = fileService.getSharedFiles(userId);
        return ResponseEntity.ok(files);
    }
}