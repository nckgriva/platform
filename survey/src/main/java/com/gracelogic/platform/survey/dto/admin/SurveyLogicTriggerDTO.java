package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyLogicTrigger;

import java.util.UUID;

public class SurveyLogicTriggerDTO extends IdObjectDTO {
    private UUID surveyPage;
    private UUID surveyQuestion;
    private UUID answerVariant;
    private boolean isInteractionRequired;
    private UUID targetQuestion;
    private String newConclusion;
    private Integer pageIndex;
    private String newLink;
    private UUID logicType;

    public static SurveyLogicTriggerDTO prepare(SurveyLogicTrigger variantLogic) {
        SurveyLogicTriggerDTO model = new SurveyLogicTriggerDTO();
        IdObjectDTO.prepare(model, variantLogic);
        model.setSurveyPage(variantLogic.getSurveyPage().getId());
        model.setSurveyQuestion(variantLogic.getSurveyQuestion().getId());
        model.setAnswerVariant(variantLogic.getAnswerVariant().getId());
        model.setInteractionRequired(variantLogic.isInteractionRequired());
        model.setTargetQuestion(variantLogic.getTargetQuestion().getId());
        model.setNewConclusion(variantLogic.getNewConclusion());
        model.setPageIndex(variantLogic.getPageIndex());
        model.setNewLink(variantLogic.getNewLink());
        model.setLogicType(variantLogic.getLogicType());
        return model;
    }

    public UUID getSurveyPage() {
        return surveyPage;
    }

    public void setSurveyPage(UUID surveyPage) {
        this.surveyPage = surveyPage;
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

    public boolean isInteractionRequired() {
        return isInteractionRequired;
    }

    public void setInteractionRequired(boolean interactionRequired) {
        isInteractionRequired = interactionRequired;
    }
}
