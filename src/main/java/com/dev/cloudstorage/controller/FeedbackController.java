package com.dev.cloudstorage.controller;

import com.dev.cloudstorage.dto.LoginResponse;
import com.dev.cloudstorage.model.Feedback;
import com.dev.cloudstorage.service.FeedbackService;
import com.dev.cloudstorage.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Feedback> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Feedback feedback = feedbackService.submitFeedback(userId, request.getSubject(),
                                                          request.getMessage(), request.getRating());
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Feedback>> getUserFeedback() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Feedback> feedback = feedbackService.getUserFeedback(userId);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        List<Feedback> feedback = feedbackService.getAllFeedback();
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/resolved")
    public ResponseEntity<List<Feedback>> getResolvedFeedback() {
        List<Feedback> feedback = feedbackService.getResolvedFeedback();
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/unresolved")
    public ResponseEntity<List<Feedback>> getUnresolvedFeedback() {
        List<Feedback> feedback = feedbackService.getUnresolvedFeedback();
        return ResponseEntity.ok(feedback);
    }

    @PutMapping("/{feedbackId}/resolve")
    public ResponseEntity<Feedback> resolveFeedback(@PathVariable Long feedbackId) {
        try {
            Feedback resolvedFeedback = feedbackService.resolveFeedback(feedbackId);
            return ResponseEntity.ok(resolvedFeedback);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Inner class for feedback request
    public static class FeedbackRequest {
        private String subject;
        private String message;
        private Integer rating;

        // Getters and setters
        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }
    }
}