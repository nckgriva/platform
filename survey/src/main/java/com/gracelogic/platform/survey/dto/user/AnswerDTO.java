package com.gracelogic.platform.survey.dto.user;

import com.gracelogic.platform.survey.model.SurveyQuestionAnswer;

import java.util.UUID;

public class AnswerDTO {
    private UUID answerId;
    private UUID storedFileId;
    private String text;
    private Integer selectedMatrixRow;
    private Integer selectedMatrixColumn;

    public static AnswerDTO fromModel(SurveyQuestionAnswer model) {
        AnswerDTO dto = new AnswerDTO();
        dto.setAnswerId(model.getAnswerVariant() != null ? model.getAnswerVariant().getId() : null);
        dto.setStoredFileId(model.getStoredFile() != null ? model.getStoredFile().getId() : null);
        dto.setText(model.getText());
        dto.setSelectedMatrixRow(model.getSelectedMatrixRow());
        dto.setSelectedMatrixColumn(model.getSelectedMatrixColumn());
        return dto;
    }

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getSelectedMatrixRow() {
        return selectedMatrixRow;
    }

    public void setSelectedMatrixRow(Integer selectedMatrixRow) {
        this.selectedMatrixRow = selectedMatrixRow;
    }

    public Integer getSelectedMatrixColumn() {
        return selectedMatrixColumn;
    }

    public void setSelectedMatrixColumn(Integer selectedMatrixColumn) {
        this.selectedMatrixColumn = selectedMatrixColumn;
    }
}
