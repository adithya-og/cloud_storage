package com.dev.cloudstorage.controller;

import com.dev.cloudstorage.service.FileService;
import com.dev.cloudstorage.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    @Autowired
    private FileService fileService;

    @PostMapping("/{fileId}/share/{targetUserId}")
    public ResponseEntity<?> shareFile(@PathVariable Long fileId, @PathVariable Long targetUserId,
                                      @RequestParam(defaultValue = "READ") String permission) {
        try {
            // Get current user ID
            Long currentUserId = SecurityUtils.getCurrentUserId();

            boolean success = fileService.shareFile(fileId, currentUserId, targetUserId, permission);

            if (success) {
                return ResponseEntity.ok().body("File shared successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to share file");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sharing file: " + e.getMessage());
        }
    }

    @DeleteMapping("/{fileId}/revoke/{targetUserId}")
    public ResponseEntity<?> revokeShare(@PathVariable Long fileId, @PathVariable Long targetUserId) {
        try {
            // Get current user ID
            Long currentUserId = SecurityUtils.getCurrentUserId();

            boolean success = fileService.revokeShare(fileId, currentUserId, targetUserId);

            if (success) {
                return ResponseEntity.ok().body("File share revoked successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to revoke file share");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error revoking file share: " + e.getMessage());
        }
    }
}