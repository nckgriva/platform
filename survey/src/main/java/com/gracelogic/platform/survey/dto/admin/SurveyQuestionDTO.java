package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyQuestion;

import java.util.List;
import java.util.UUID;

public class SurveyQuestionDTO extends IdObjectDTO {
    private UUID surveyPage;
    private Integer questionIndex;
    private String text;
    private UUID type;
    private Boolean required;
    private Boolean hidden;
    private Long scaleMinValue;
    private Long scaleMaxValue;
    private String attachmentExtensions;

    private List<SurveyAnswerVariantDTO> answers;
    private List<SurveyLogicTriggerDTO> logicTriggers;

    public static SurveyQuestionDTO prepare(SurveyQuestion surveyQuestion) {
        SurveyQuestionDTO model = new SurveyQuestionDTO();
        IdObjectDTO.prepare(model, surveyQuestion);
        model.setSurveyPage(surveyQuestion.getSurveyPage().getId());
        model.setQuestionIndex(surveyQuestion.getQuestionIndex());
        model.setText(surveyQuestion.getText());
        model.setType(surveyQuestion.getType());
        model.setRequired(surveyQuestion.getRequired());
        model.setHidden(surveyQuestion.getHidden());
        model.setScaleMinValue(surveyQuestion.getScaleMinValue());
        model.setScaleMaxValue(surveyQuestion.getScaleMaxValue());
        model.setAttachmentExtensions(surveyQuestion.getAttachmentExtensions());
        return model;
    }

    public UUID getSurveyPage() {
        return surveyPage;
    }

    public void setSurveyPage(UUID surveyPage) {
        this.surveyPage = surveyPage;
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

    public UUID getType() {
        return type;
    }

    public void setType(UUID type) {
        this.type = type;
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

    public List<SurveyAnswerVariantDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<SurveyAnswerVariantDTO> answers) {
        this.answers = answers;
    }

    public List<SurveyLogicTriggerDTO> getLogicTriggers() {
        return logicTriggers;
    }

    public void setLogicTriggers(List<SurveyLogicTriggerDTO> logicTriggers) {
        this.logicTriggers = logicTriggers;
    }
}
