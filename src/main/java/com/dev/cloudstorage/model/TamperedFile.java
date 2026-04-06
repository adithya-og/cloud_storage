package com.dev.cloudstorage.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tampered_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TamperedFile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "file_id", nullable = false)
    private Long fileId;
    
    @Column(name = "detected_at")
    private LocalDateTime detectedAt;
    
    @Column(name = "original_hash", nullable = false)
    private String originalHash;
    
    @Column(name = "current_hash", nullable = false)
    private String currentHash;
    
    @Column(name = "detected_by_user_id")
    private Long detectedByUserId;
    
    @Column(name = "file_backup_path")
    private String fileBackupPath;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TamperStatus status = TamperStatus.DETECTED;
    
    @Lob
    @Column
    private String notes;
    
    @PrePersist
    protected void onCreate() {
        detectedAt = LocalDateTime.now();
    }
    
    public enum TamperStatus {
        DETECTED, RECOVERED, IRRECOVERABLE
    }
}