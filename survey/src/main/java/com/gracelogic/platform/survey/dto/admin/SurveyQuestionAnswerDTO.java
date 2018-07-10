package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyQuestionAnswer;

import java.util.UUID;

public class SurveyQuestionAnswerDTO extends IdObjectDTO {
    private UUID surveySessionId;
    private UUID questionId;
    private UUID answerVariantId;
    private String text;
    private UUID storedFile;

    public static SurveyQuestionAnswerDTO prepare(SurveyQuestionAnswer model) {
        SurveyQuestionAnswerDTO dto = new SurveyQuestionAnswerDTO();
        IdObjectDTO.prepare(dto, model);
        dto.setSurveySessionId(model.getSurveySession().getId());
        dto.setQuestionId(model.getQuestion().getId());
        if (model.getAnswerVariant() != null) {
            dto.setAnswerVariantId(model.getAnswerVariant().getId());
        }
        dto.setText(model.getText());
        if (model.getStoredFile() != null) {
            dto.setStoredFile(model.getStoredFile().getId());
        }

        return dto;
    }

    public UUID getSurveySessionId() {
        return surveySessionId;
    }

    public void setSurveySessionId(UUID surveySessionId) {
        this.surveySessionId = surveySessionId;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public UUID getAnswerVariantId() {
        return answerVariantId;
    }

    public void setAnswerVariantId(UUID answerVariantId) {
        this.answerVariantId = answerVariantId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UUID getStoredFile() {
        return storedFile;
    }

    public void setStoredFile(UUID storedFile) {
        this.storedFile = storedFile;
    }
}
