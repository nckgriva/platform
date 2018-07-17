package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyQuestion;

import java.util.*;

public class SurveyQuestionDTO extends IdObjectDTO {
    private UUID surveyPageId;
    private Integer questionIndex;
    private String text;
    private String description;
    private UUID surveyQuestionTypeId;
    private Boolean required;
    private Boolean hidden;
    private Long scaleMinValue;
    private Long scaleMaxValue;
    private String scaleMinValueLabel;
    private String scaleMaxValueLabel;
    private String attachmentExtensions;
    private String[] matrixColumns;
    private String[] matrixRows;

    private List<SurveyAnswerVariantDTO> answerVariants = new LinkedList<>();
    private Set<UUID> answersToDelete = new HashSet<>();


    public static SurveyQuestionDTO prepare(SurveyQuestion model) {
        SurveyQuestionDTO dto = new SurveyQuestionDTO();
        IdObjectDTO.prepare(dto, model);
        dto.setSurveyPageId(model.getSurveyPage().getId());
        dto.setQuestionIndex(model.getQuestionIndex());
        dto.setText(model.getText());
        dto.setDescription(model.getDescription());
        if (model.getSurveyQuestionType() != null) {
            dto.setSurveyQuestionTypeId(model.getSurveyQuestionType().getId());
        }
        dto.setRequired(model.getRequired());
        dto.setHidden(model.getHidden());
        dto.setScaleMinValue(model.getScaleMinValue());
        dto.setScaleMaxValue(model.getScaleMaxValue());
        dto.setAttachmentExtensions(model.getAttachmentExtensions());
        dto.setMatrixColumns(model.getMatrixColumns());
        dto.setMatrixRows(model.getMatrixRows());

        dto.setScaleMaxValueLabel(model.getScaleMaxValueLabel());
        dto.setScaleMinValueLabel(model.getScaleMinValueLabel());
        return dto;
    }

    public UUID getSurveyPageId() {
        return surveyPageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSurveyPageId(UUID surveyPageId) {
        this.surveyPageId = surveyPageId;
    }

    public Integer getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(Integer questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UUID getSurveyQuestionTypeId() {
        return surveyQuestionTypeId;
    }

    public void setSurveyQuestionTypeId(UUID surveyQuestionTypeId) {
        this.surveyQuestionTypeId = surveyQuestionTypeId;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Long getScaleMinValue() {
        return scaleMinValue;
    }

    public void setScaleMinValue(Long scaleMinValue) {
        this.scaleMinValue = scaleMinValue;
    }

    public Long getScaleMaxValue() {
        return scaleMaxValue;
    }

    public void setScaleMaxValue(Long scaleMaxValue) {
        this.scaleMaxValue = scaleMaxValue;
    }

    public String getAttachmentExtensions() {
        return attachmentExtensions;
    }

    public void setAttachmentExtensions(String attachmentExtensions) {
        this.attachmentExtensions = attachmentExtensions;
    }

    public List<SurveyAnswerVariantDTO> getAnswerVariants() {
        return answerVariants;
    }

    public void setAnswerVariants(List<SurveyAnswerVariantDTO> answerVariants) {
        this.answerVariants = answerVariants;
    }

    public Set<UUID> getAnswersToDelete() {
        return answersToDelete;
    }

    public void setAnswersToDelete(Set<UUID> answersToDelete) {
        this.answersToDelete = answersToDelete;
    }

    public String[] getMatrixColumns() {
        return matrixColumns;
    }

    public void setMatrixColumns(String[] matrixColumns) {
        this.matrixColumns = matrixColumns;
    }

    public String[] getMatrixRows() {
        return matrixRows;
    }

    public void setMatrixRows(String[] matrixRows) {
        this.matrixRows = matrixRows;
    }

    public String getScaleMinValueLabel() {
        return scaleMinValueLabel;
    }

    public void setScaleMinValueLabel(String scaleMinValueLabel) {
        this.scaleMinValueLabel = scaleMinValueLabel;
    }

    public String getScaleMaxValueLabel() {
        return scaleMaxValueLabel;
    }

    public void setScaleMaxValueLabel(String scaleMaxValueLabel) {
        this.scaleMaxValueLabel = scaleMaxValueLabel;
    }
}
