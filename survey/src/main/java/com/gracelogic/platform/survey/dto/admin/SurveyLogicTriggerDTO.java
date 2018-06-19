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
    private UUID logicActionType;

    public static SurveyLogicTriggerDTO prepare(SurveyLogicTrigger trigger) {
        SurveyLogicTriggerDTO model = new SurveyLogicTriggerDTO();
        IdObjectDTO.prepare(model, trigger);
        model.setSurveyPage(trigger.getSurveyPage().getId());
        if (trigger.getSurveyQuestion() != null) model.setSurveyQuestion(trigger.getSurveyQuestion().getId());
        if (trigger.getAnswerVariant() != null) model.setAnswerVariant(trigger.getAnswerVariant().getId());
        model.setInteractionRequired(trigger.isInteractionRequired());
        if (trigger.getTargetQuestion() != null) model.setTargetQuestion(trigger.getTargetQuestion().getId());
        model.setNewConclusion(trigger.getNewConclusion());
        model.setPageIndex(trigger.getPageIndex());
        model.setNewLink(trigger.getNewLink());
        model.setLogicActionType(trigger.getLogicActionType());
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

    public UUID getLogicActionType() {
        return logicActionType;
    }

    public void setLogicActionType(UUID logicActionType) {
        this.logicActionType = logicActionType;
    }

    public boolean isInteractionRequired() {
        return isInteractionRequired;
    }

    public void setInteractionRequired(boolean interactionRequired) {
        isInteractionRequired = interactionRequired;
    }
}
