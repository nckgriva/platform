package com.gracelogic.platform.feedback.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.feedback.dto.FeedbackDTO;
import com.gracelogic.platform.feedback.model.Feedback;
import com.gracelogic.platform.user.dto.AuthorizedUser;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public interface FeedbackService {
    Feedback saveFeedback(FeedbackDTO feedbackDTO);

    FeedbackDTO getFeedback(UUID id, AuthorizedUser user) throws ObjectNotFoundException;

    EntityListResponse<FeedbackDTO> getFeedbacksPaged(UUID feedbackTypeId, Date startDate, Date endDate, Map<String, String> fields, Integer count, Integer page, Integer start, String sortField, String sortDir);
}
