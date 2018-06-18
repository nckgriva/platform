package com.gracelogic.platform.survey.dto.user;

import java.util.UUID;

public class AnswerDTO {
    private UUID answerId;
    private UUID storedFileId;
    private String textAnswer;

    public UUID getStoredFileId() {
        return storedFileId;
    }

    public void setStoredFileId(UUID storedFileId) {
        this.storedFileId = storedFileId;
    }

    public UUID getAnswerId() {
        return answerId;
    }

    public void setAnswerId(UUID answerId) {
        this.answerId = answerId;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }
}
