package com.dev.cloudstorage.service;

import com.dev.cloudstorage.model.ActivityLog;
import java.util.List;

public interface ActivityLogService {
    ActivityLog logActivity(Long userId, Long fileId, String action, String details);
    List<ActivityLog> getUserActivities(Long userId);
    List<ActivityLog> getFileActivities(Long fileId);
    List<ActivityLog> getAllActivities();
    List<ActivityLog> getActivitiesByAction(String action);
}