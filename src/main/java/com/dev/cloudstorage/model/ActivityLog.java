package com.dev.cloudstorage.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "file_id")
    private Long fileId;
    
    @Column(nullable = false)
    private String action; // UPLOAD, DOWNLOAD, DELETE, SHARE, VIEW
    
    @Lob
    @Column(name = "action_details")
    private String actionDetails;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Lob
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}