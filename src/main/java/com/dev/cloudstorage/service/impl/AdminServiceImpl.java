package com.dev.cloudstorage.service.impl;

import com.dev.cloudstorage.model.*;
import com.dev.cloudstorage.repository.*;
import com.dev.cloudstorage.service.AdminService;
import com.dev.cloudstorage.service.AdminStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FileRepository fileRepository;
    
    @Autowired
    private ActivityLogRepository activityLogRepository;
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private TamperedFileRepository tamperedFileRepository;
    

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(false); // Soft delete by deactivating
        userRepository.save(user);
    }

    @Override
    public User updateUserRole(Long userId, String newRole) {
        User user = getUserById(userId);
        user.setRole(User.Role.valueOf(newRole.toUpperCase()));
        return userRepository.save(user);
    }

    @Override
    public List<com.dev.cloudstorage.model.File> getAllFiles() {
        return fileRepository.findAll();
    }

    @Override
    public com.dev.cloudstorage.model.File getFileById(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
    }

    @Override
    public void deleteFilePermanently(Long fileId) {
        fileRepository.deleteById(fileId);
    }

    @Override
    public List<ActivityLog> getAllActivities() {
        return activityLogRepository.findAll();
    }

    @Override
    public List<ActivityLog> getActivitiesByUser(Long userId) {
        return activityLogRepository.findByUserId(userId);
    }

    @Override
    public List<ActivityLog> getActivitiesByAction(String action) {
        return activityLogRepository.findByAction(action);
    }

    @Override
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    @Override
    public List<Feedback> getUnresolvedFeedback() {
        return feedbackRepository.findByIsResolved(false);
    }

    @Override
    public Feedback resolveFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));
        feedback.setIsResolved(true);
        return feedbackRepository.save(feedback);
    }

    @Override
    public void deleteFeedback(Long feedbackId) {
        feedbackRepository.deleteById(feedbackId);
    }

    @Override
    public List<TamperedFile> getAllTamperedFiles() {
        return tamperedFileRepository.findAll();
    }

    @Override
    public List<TamperedFile> getTamperedFilesByStatus(String status) {
        return tamperedFileRepository.findByStatus(TamperedFile.TamperStatus.valueOf(status.toUpperCase()));
    }

    @Override
    public TamperedFile getTamperedFileById(Long tamperedFileId) {
        return tamperedFileRepository.findById(tamperedFileId)
                .orElseThrow(() -> new IllegalArgumentException("Tampered file record not found"));
    }

    @Override
    public void resolveTamperedFile(Long tamperedFileId) {
        TamperedFile tamperedFile = getTamperedFileById(tamperedFileId);
        tamperedFile.setStatus(TamperedFile.TamperStatus.RECOVERED);
        tamperedFileRepository.save(tamperedFile);
    }

    @Override
    public AdminStats getAdminStats() {
        long totalUsers = userRepository.count();
        long totalFiles = fileRepository.count();
        long totalActivities = activityLogRepository.count();
        long unresolvedFeedback = feedbackRepository.findByIsResolved(false).size();
        long tamperedFiles = tamperedFileRepository.findByStatus(TamperedFile.TamperStatus.DETECTED).size();
        
        return new AdminStats(totalUsers, totalFiles, totalActivities, unresolvedFeedback, tamperedFiles);
    }
}