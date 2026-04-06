package com.dev.cloudstorage.service;

import com.dev.cloudstorage.model.TamperedFile;
import com.dev.cloudstorage.model.User;
import com.dev.cloudstorage.model.ActivityLog;
import com.dev.cloudstorage.model.Feedback;

import java.util.List;

public interface AdminService {
    // User management
    List<User> getAllUsers();
    User getUserById(Long userId);
    void deleteUser(Long userId);
    User updateUserRole(Long userId, String newRole);
    
    // File management
    List<com.dev.cloudstorage.model.File> getAllFiles();
    com.dev.cloudstorage.model.File getFileById(Long fileId);
    void deleteFilePermanently(Long fileId);
    
    // Activity logs
    List<ActivityLog> getAllActivities();
    List<ActivityLog> getActivitiesByUser(Long userId);
    List<ActivityLog> getActivitiesByAction(String action);
    
    // Feedback management
    List<Feedback> getAllFeedback();
    List<Feedback> getUnresolvedFeedback();
    Feedback resolveFeedback(Long feedbackId);
    void deleteFeedback(Long feedbackId);
    
    // Tampered files
    List<TamperedFile> getAllTamperedFiles();
    List<TamperedFile> getTamperedFilesByStatus(String status);
    TamperedFile getTamperedFileById(Long tamperedFileId);
    void resolveTamperedFile(Long tamperedFileId);
    
    // Stats
    AdminStats getAdminStats();
}