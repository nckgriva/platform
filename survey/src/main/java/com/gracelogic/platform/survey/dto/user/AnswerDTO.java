package com.gracelogic.platform.survey.dto.user;

import java.util.UUID;

public class AnswerDTO {
    private UUID questionId;
    private UUID answerId;

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public UUID getAnswerId() {
        return answerId;
    }

    public void setAnswerId(UUID answerId) {
        this.answerId = answerId;
    }
}
