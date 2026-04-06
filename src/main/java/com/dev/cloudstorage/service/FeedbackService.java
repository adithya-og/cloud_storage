package com.dev.cloudstorage.service;

import com.dev.cloudstorage.model.Feedback;
import java.util.List;

public interface FeedbackService {
    Feedback submitFeedback(Long userId, String subject, String message, Integer rating);
    List<Feedback> getUserFeedback(Long userId);
    List<Feedback> getAllFeedback();
    List<Feedback> getResolvedFeedback();
    List<Feedback> getUnresolvedFeedback();
    Feedback resolveFeedback(Long feedbackId);
    Feedback getFeedbackById(Long feedbackId);
}