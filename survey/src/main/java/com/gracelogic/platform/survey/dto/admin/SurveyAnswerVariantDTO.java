package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyAnswerVariant;

import java.util.UUID;

public class SurveyAnswerVariantDTO extends IdObjectDTO {
    private UUID surveyQuestionId;
    private String text;
    private Integer sortOrder;
    private Boolean isDefaultVariant; // Вариант выбран по умолчанию
    private Boolean isCustomVariant;
    private Integer weight; // Вес ответа для автоматической обработки

    public static SurveyAnswerVariantDTO prepare(SurveyAnswerVariant model) {
        SurveyAnswerVariantDTO dto = new SurveyAnswerVariantDTO();
        IdObjectDTO.prepare(dto, model);
        if (model.getSurveyQuestion() != null) {
            dto.setSurveyQuestionId(model.getSurveyQuestion().getId());
        }
        dto.setText(model.getText());
        dto.setSortOrder(model.getSortOrder());
        dto.setDefaultVariant(model.getDefaultVariant());
        dto.setCustomVariant(model.getCustomVariant());
        dto.setWeight(model.getWeight());
        return dto;
    }

    public UUID getSurveyQuestionId() {
        return surveyQuestionId;
    }

    public void setSurveyQuestionId(UUID surveyQuestionId) {
        this.surveyQuestionId = surveyQuestionId;
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
        return isDefaultVariant;
    }

    public void setDefaultVariant(Boolean defaultVariant) {
        isDefaultVariant = defaultVariant;
    }

    public Boolean getCustomVariant() {
        return isCustomVariant;
    }

    public void setCustomVariant(Boolean customVariant) {
        isCustomVariant = customVariant;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
