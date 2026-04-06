package com.dev.cloudstorage.service.impl;

import com.dev.cloudstorage.model.ActivityLog;
import com.dev.cloudstorage.repository.ActivityLogRepository;
import com.dev.cloudstorage.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Override
    public ActivityLog logActivity(Long userId, Long fileId, String action, String details) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setFileId(fileId);
        log.setAction(action);
        log.setActionDetails(details);
        
        return activityLogRepository.save(log);
    }

    @Override
    public List<ActivityLog> getUserActivities(Long userId) {
        return activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<ActivityLog> getFileActivities(Long fileId) {
        return activityLogRepository.findByFileId(fileId);
    }

    @Override
    public List<ActivityLog> getAllActivities() {
        return activityLogRepository.findAll();
    }

    @Override
    public List<ActivityLog> getActivitiesByAction(String action) {
        return activityLogRepository.findByAction(action);
    }
}