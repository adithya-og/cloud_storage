package com.dev.cloudstorage.controller;

import com.dev.cloudstorage.model.*;
import com.dev.cloudstorage.service.AdminService;
import com.dev.cloudstorage.service.AdminStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // User management endpoints
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        try {
            User user = adminService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            adminService.deleteUser(userId);
            return ResponseEntity.ok().body("User deactivated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long userId, @RequestParam String role) {
        try {
            User updatedUser = adminService.updateUserRole(userId, role);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // File management endpoints
    @GetMapping("/files")
    public ResponseEntity<List<File>> getAllFiles() {
        List<File> files = adminService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<?> deleteFilePermanently(@PathVariable Long fileId) {
        try {
            adminService.deleteFilePermanently(fileId);
            return ResponseEntity.ok().body("File deleted permanently");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Activity logs
    @GetMapping("/activities")
    public ResponseEntity<List<ActivityLog>> getAllActivities() {
        List<ActivityLog> activities = adminService.getAllActivities();
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/activities/user/{userId}")
    public ResponseEntity<List<ActivityLog>> getActivitiesByUser(@PathVariable Long userId) {
        List<ActivityLog> activities = adminService.getActivitiesByUser(userId);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/activities/action/{action}")
    public ResponseEntity<List<ActivityLog>> getActivitiesByAction(@PathVariable String action) {
        List<ActivityLog> activities = adminService.getActivitiesByAction(action);
        return ResponseEntity.ok(activities);
    }

    // Feedback management
    @GetMapping("/feedback")
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        List<Feedback> feedback = adminService.getAllFeedback();
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/feedback/unresolved")
    public ResponseEntity<List<Feedback>> getUnresolvedFeedback() {
        List<Feedback> feedback = adminService.getUnresolvedFeedback();
        return ResponseEntity.ok(feedback);
    }

    @PutMapping("/feedback/{feedbackId}/resolve")
    public ResponseEntity<Feedback> resolveFeedback(@PathVariable Long feedbackId) {
        try {
            Feedback resolvedFeedback = adminService.resolveFeedback(feedbackId);
            return ResponseEntity.ok(resolvedFeedback);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Tampered files
    @GetMapping("/tampered")
    public ResponseEntity<List<TamperedFile>> getAllTamperedFiles() {
        List<TamperedFile> tamperedFiles = adminService.getAllTamperedFiles();
        return ResponseEntity.ok(tamperedFiles);
    }

    @GetMapping("/tampered/status/{status}")
    public ResponseEntity<List<TamperedFile>> getTamperedFilesByStatus(@PathVariable String status) {
        List<TamperedFile> tamperedFiles = adminService.getTamperedFilesByStatus(status);
        return ResponseEntity.ok(tamperedFiles);
    }

    @PutMapping("/tampered/{tamperedFileId}/resolve")
    public ResponseEntity<?> resolveTamperedFile(@PathVariable Long tamperedFileId) {
        try {
            adminService.resolveTamperedFile(tamperedFileId);
            return ResponseEntity.ok().body("Tampered file marked as recovered");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Stats
    @GetMapping("/stats")
    public ResponseEntity<AdminStats> getAdminStats() {
        AdminStats stats = adminService.getAdminStats();
        return ResponseEntity.ok(stats);
    }
}