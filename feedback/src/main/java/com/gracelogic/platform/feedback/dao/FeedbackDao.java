package com.gracelogic.platform.feedback.dao;

import com.gracelogic.platform.feedback.model.Feedback;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface FeedbackDao {
    Integer getFeedbacksCount(UUID feedbackTypeId, Date startDate, Date endDate, Map<String, String> fields);

    List<Feedback> getFeedbacks(UUID feedbackTypeId, Date startDate, Date endDate, Map<String, String> fields, String sortField, String sortDir, Integer startRecord, Integer recordsOnPage);
}
