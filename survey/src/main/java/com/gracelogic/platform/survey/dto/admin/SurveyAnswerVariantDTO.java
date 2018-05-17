package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyAnswerVariant;

import java.util.UUID;

public class SurveyAnswerVariantDTO extends IdObjectDTO {
    private UUID surveyQuestion;
    private String text;
    private Integer sortOrder;
    private Boolean defaultVariant; // Вариант выбран по умолчанию
    private Integer weight; // Вес ответа для автоматической обработки

    // TODO: add logic

    public static SurveyAnswerVariantDTO prepare(SurveyAnswerVariant surveyAnswerVariant) {
        SurveyAnswerVariantDTO model = new SurveyAnswerVariantDTO();
        IdObjectDTO.prepare(model, surveyAnswerVariant);
        model.setSurveyQuestion(surveyAnswerVariant.getSurveyQuestion().getId());
        model.setText(surveyAnswerVariant.getText());
        model.setSortOrder(surveyAnswerVariant.getSortOrder());
        model.setDefaultVariant(surveyAnswerVariant.getDefaultVariant());
        model.setWeight(surveyAnswerVariant.getWeight());
        return model;
    }

    public UUID getSurveyQuestion() {
        return surveyQuestion;
    }

    public void setSurveyQuestion(UUID surveyQuestion) {
        this.surveyQuestion = surveyQuestion;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getDefaultVariant() {
        return defaultVariant;
    }

    public void setDefaultVariant(Boolean defaultVariant) {
        this.defaultVariant = defaultVariant;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
