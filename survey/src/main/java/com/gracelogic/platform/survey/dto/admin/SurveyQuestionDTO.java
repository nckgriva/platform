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
    private Boolean isRequired;
    private Boolean isHidden;
    private Long scaleMinValue;
    private Long scaleMaxValue;
    private String attachmentExtensions;

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
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
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
