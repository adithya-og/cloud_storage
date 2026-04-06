package com.dev.cloudstorage.service.impl;

import com.dev.cloudstorage.model.Feedback;
import com.dev.cloudstorage.repository.FeedbackRepository;
import com.dev.cloudstorage.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public Feedback submitFeedback(Long userId, String subject, String message, Integer rating) {
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setSubject(subject);
        feedback.setMessage(message);
        feedback.setRating(rating);
        feedback.setIsResolved(false);
        
        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> getUserFeedback(Long userId) {
        return feedbackRepository.findByUserId(userId);
    }

    @Override
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    @Override
    public List<Feedback> getResolvedFeedback() {
        return feedbackRepository.findByIsResolved(true);
    }

    @Override
    public List<Feedback> getUnresolvedFeedback() {
        return feedbackRepository.findByIsResolved(false);
    }

    @Override
    public Feedback resolveFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));
        
        feedback.setIsResolved(true);
        return feedbackRepository.save(feedback);
    }

    @Override
    public Feedback getFeedbackById(Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));
    }
}