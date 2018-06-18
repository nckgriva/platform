package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyVariantLogic;

import java.util.UUID;

public class SurveyVariantLogicDTO extends IdObjectDTO {
    private UUID answerVariant;
    private UUID surveyQuestion;
    private boolean isSelectionRequired;
    private UUID targetQuestion;
    private String newConclusion;
    private Integer pageIndex;
    private String newLink;
    private UUID logicType;

    public static SurveyVariantLogicDTO prepare(SurveyVariantLogic variantLogic) {
        SurveyVariantLogicDTO model = new SurveyVariantLogicDTO();
        IdObjectDTO.prepare(model, variantLogic);
        model.setSurveyQuestion(variantLogic.getSurveyQuestion().getId());
        model.setAnswerVariant(variantLogic.getAnswerVariant().getId());
        model.setSelectionRequired(variantLogic.isSelectionRequired());
        model.setTargetQuestion(variantLogic.getTargetQuestion().getId());
        model.setNewConclusion(variantLogic.getNewConclusion());
        model.setPageIndex(variantLogic.getPageIndex());
        model.setNewLink(variantLogic.getNewLink());
        model.setLogicType(variantLogic.getLogicType());
        return model;
    }

    public UUID getSurveyQuestion() {
        return surveyQuestion;
    }

    public void setSurveyQuestion(UUID surveyQuestion) {
        this.surveyQuestion = surveyQuestion;
    }

    public UUID getAnswerVariant() {
        return answerVariant;
    }

    public void setAnswerVariant(UUID answerVariant) {
        this.answerVariant = answerVariant;
    }

    public boolean isSelectionRequired() {
        return isSelectionRequired;
    }

    public void setSelectionRequired(boolean selectionRequired) {
        isSelectionRequired = selectionRequired;
    }

    public UUID getTargetQuestion() {
        return targetQuestion;
    }

    public void setTargetQuestion(UUID targetQuestion) {
        this.targetQuestion = targetQuestion;
    }

    public String getNewConclusion() {
        return newConclusion;
    }

    public void setNewConclusion(String newConclusion) {
        this.newConclusion = newConclusion;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getNewLink() {
        return newLink;
    }

    public void setNewLink(String newLink) {
        this.newLink = newLink;
    }

    public UUID getLogicType() {
        return logicType;
    }

    public void setLogicType(UUID logicType) {
        this.logicType = logicType;
    }
}
