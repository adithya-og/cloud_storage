package com.dev.cloudstorage.repository;

import com.dev.cloudstorage.model.FileAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileAccessRepository extends JpaRepository<FileAccess, Long> {
    List<FileAccess> findByFileId(Long fileId);
    List<FileAccess> findByUserId(Long userId);
    Optional<FileAccess> findByFileIdAndUserId(Long fileId, Long userId);
    List<FileAccess> findByFileIdAndIsActiveTrue(Long fileId);
    List<FileAccess> findByUserIdAndIsActiveTrue(Long userId);
}