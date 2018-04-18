package com.gracelogic.platform.feedback.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.feedback.dto.FeedbackDTO;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public interface FeedbackService {
    void saveFeedback(FeedbackDTO feedbackDTO);

    EntityListResponse<FeedbackDTO> getFeedbacksPaged(UUID feedbackTypeId, Date startDate, Date endDate, Map<String, String> fields, Integer count, Integer page, Integer start, String sortField, String sortDir);
}
