package com.gracelogic.platform.feedback.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.feedback.model.Feedback;
import com.gracelogic.platform.user.service.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FeedbackDTO extends IdObjectDTO implements Serializable {

    private UUID feedbackTypeId;
    private Map<String, String> fields = new HashMap<String, String>();

    public UUID getFeedbackTypeId() { return feedbackTypeId; }

    public void setFeedbackTypeId(UUID feedbackTypeId) { this.feedbackTypeId = feedbackTypeId; }

    public Map<String, String> getFields() { return fields; }

    public void setFields(Map<String, String> fields) { this.fields = fields; }

    public static FeedbackDTO prepare(Feedback feedback) {
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        IdObjectDTO.prepare(feedbackDTO, feedback);

        if (feedback.getFeedbackType() != null) {
            feedbackDTO.setFeedbackTypeId(feedback.getFeedbackType().getId());
        }

        if (!StringUtils.isEmpty(feedback.getFields())) {
            feedbackDTO.setFields(JsonUtils.jsonToMap(feedback.getFields()));
        }

        return feedbackDTO;
    }
}
