package com.dev.cloudstorage.controller;

import com.dev.cloudstorage.model.ActivityLog;
import com.dev.cloudstorage.service.ActivityLogService;
import com.dev.cloudstorage.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping("/user")
    public ResponseEntity<List<ActivityLog>> getUserActivities() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<ActivityLog> activities = activityLogService.getUserActivities(userId);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<List<ActivityLog>> getFileActivities(@PathVariable Long fileId) {
        List<ActivityLog> activities = activityLogService.getFileActivities(fileId);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<ActivityLog>> getActivitiesByAction(@PathVariable String action) {
        List<ActivityLog> activities = activityLogService.getActivitiesByAction(action);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ActivityLog>> getAllActivities() {
        List<ActivityLog> activities = activityLogService.getAllActivities();
        return ResponseEntity.ok(activities);
    }
}