package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyQuestion;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SurveyQuestionDTO extends IdObjectDTO {
    private UUID surveyPage;
    private Integer sortOrder;
    private String text;
    private UUID type;
    private Boolean required;
    private Boolean hidden;
    private List<SurveyAnswerVariantDTO> answers;
    private SurveyVariantLogicDTO variantLogic;

    public static SurveyQuestionDTO prepare(SurveyQuestion surveyQuestion) {
        SurveyQuestionDTO model = new SurveyQuestionDTO();
        IdObjectDTO.prepare(model, surveyQuestion);
        model.setSurveyPage(surveyQuestion.getSurveyPage().getId());
        model.setSortOrder(surveyQuestion.getSortOrder());
        model.setText(surveyQuestion.getText());
        model.setType(surveyQuestion.getType());
        model.setRequired(surveyQuestion.getRequired());
        model.setHidden(surveyQuestion.getHidden());
        return model;
    }

    public UUID getSurveyPage() {
        return surveyPage;
    }

    public void setSurveyPage(UUID surveyPage) {
        this.surveyPage = surveyPage;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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

    public List<SurveyAnswerVariantDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<SurveyAnswerVariantDTO> answers) {
        this.answers = answers;
    }

    public SurveyVariantLogicDTO getVariantLogic() {
        return variantLogic;
    }

    public void setVariantLogic(SurveyVariantLogicDTO variantLogic) {
        this.variantLogic = variantLogic;
    }
}
