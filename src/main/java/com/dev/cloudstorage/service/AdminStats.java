package com.dev.cloudstorage.service;

public class AdminStats {
    private long totalUsers;
    private long totalFiles;
    private long totalActivities;
    private long unresolvedFeedback;
    private long tamperedFiles;

    // Constructors
    public AdminStats() {}

    public AdminStats(long totalUsers, long totalFiles, long totalActivities, long unresolvedFeedback, long tamperedFiles) {
        this.totalUsers = totalUsers;
        this.totalFiles = totalFiles;
        this.totalActivities = totalActivities;
        this.unresolvedFeedback = unresolvedFeedback;
        this.tamperedFiles = tamperedFiles;
    }

    // Getters and setters
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(long totalFiles) {
        this.totalFiles = totalFiles;
    }

    public long getTotalActivities() {
        return totalActivities;
    }

    public void setTotalActivities(long totalActivities) {
        this.totalActivities = totalActivities;
    }

    public long getUnresolvedFeedback() {
        return unresolvedFeedback;
    }

    public void setUnresolvedFeedback(long unresolvedFeedback) {
        this.unresolvedFeedback = unresolvedFeedback;
    }

    public long getTamperedFiles() {
        return tamperedFiles;
    }

    public void setTamperedFiles(long tamperedFiles) {
        this.tamperedFiles = tamperedFiles;
    }
}