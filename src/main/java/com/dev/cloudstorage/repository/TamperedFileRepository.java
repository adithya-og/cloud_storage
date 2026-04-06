package com.dev.cloudstorage.repository;

import com.dev.cloudstorage.model.TamperedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TamperedFileRepository extends JpaRepository<TamperedFile, Long> {
    List<TamperedFile> findByFileId(Long fileId);
    List<TamperedFile> findByStatus(com.dev.cloudstorage.model.TamperedFile.TamperStatus status);
    List<TamperedFile> findByDetectedByUserId(Long userId);
}