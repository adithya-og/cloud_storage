package com.dev.cloudstorage.repository;

import com.dev.cloudstorage.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByOwnerId(Long ownerId);
    List<File> findByOwnerIdAndIsDeletedFalse(Long ownerId);
    List<File> findByIsDeletedFalse();
}