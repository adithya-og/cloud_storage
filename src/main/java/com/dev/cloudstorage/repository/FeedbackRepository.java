package com.dev.cloudstorage.repository;

import com.dev.cloudstorage.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserId(Long userId);
    List<Feedback> findByIsResolved(Boolean isResolved);
    List<Feedback> findByUserIdAndIsResolved(Long userId, Boolean isResolved);
}