package com.dev.cloudstorage.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_access")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileAccess {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "file_id", nullable = false)
    private Long fileId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_type")
    private PermissionType permissionType = PermissionType.READ;
    
    @Column(name = "granted_by", nullable = false)
    private Long grantedBy;
    
    @Column(name = "granted_at")
    private LocalDateTime grantedAt;
    
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @PrePersist
    protected void onCreate() {
        grantedAt = LocalDateTime.now();
    }
    
    public enum PermissionType {
        READ, WRITE, SHARE, DELETE
    }
}