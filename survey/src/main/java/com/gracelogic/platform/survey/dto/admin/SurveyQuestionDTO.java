package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyQuestion;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SurveyQuestionDTO extends IdObjectDTO {
    private UUID surveyPageId;
    private Integer questionIndex;
    private String text;
    private UUID surveyQuestionTypeId;
    private Boolean required;
    private Boolean hidden;
    private Long scaleMinValue;
    private Long scaleMaxValue;
    private String attachmentExtensions;

    private List<SurveyAnswerVariantDTO> answerVariants = new LinkedList<>();
    private Set<UUID> answersToDelete;


    public static SurveyQuestionDTO prepare(SurveyQuestion model) {
        SurveyQuestionDTO dto = new SurveyQuestionDTO();
        IdObjectDTO.prepare(dto, model);
        dto.setSurveyPageId(model.getSurveyPage().getId());
        dto.setQuestionIndex(model.getQuestionIndex());
        dto.setText(model.getText());
        if (model.getSurveyQuestionType() != null) {
            dto.setSurveyQuestionTypeId(model.getSurveyQuestionType().getId());
        }
        dto.setRequired(model.getRequired());
        dto.setHidden(model.getHidden());
        dto.setScaleMinValue(model.getScaleMinValue());
        dto.setScaleMaxValue(model.getScaleMaxValue());
        dto.setAttachmentExtensions(model.getAttachmentExtensions());
        return dto;
    }

    public UUID getSurveyPageId() {
        return surveyPageId;
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
}
