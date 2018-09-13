package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyLogicTrigger;

import java.util.UUID;

public class SurveyLogicTriggerDTO extends IdObjectDTO {
    private UUID surveyPageId;
    private UUID surveyQuestionId;
    private UUID answerVariantId;
    private Boolean interactionRequired;
    private UUID targetQuestionId;
    private String newConclusion;
    private Integer pageIndex;
    private String newLink;
    private UUID logicActionTypeId;


    // this is used only in client -> server when creating LogicTrigger in SurveyQuestionDTO. Indicates target question index in arrived array
    private Integer targetQuestionIndex;

    public static SurveyLogicTriggerDTO prepare(SurveyLogicTrigger model) {
        SurveyLogicTriggerDTO dto = new SurveyLogicTriggerDTO();
        IdObjectDTO.prepare(dto, model);
        dto.setSurveyPageId(model.getSurveyPage().getId());
        if (model.getSurveyQuestion() != null) {
            dto.setSurveyQuestionId(model.getSurveyQuestion().getId());
        }
        if (model.getAnswerVariant() != null) {
            dto.setAnswerVariantId(model.getAnswerVariant().getId());
        }
        dto.setInteractionRequired(model.isInteractionRequired());
        if (model.getTargetQuestion() != null) {
            dto.setTargetQuestionId(model.getTargetQuestion().getId());
        }
        dto.setNewConclusion(model.getNewConclusion());
        dto.setPageIndex(model.getPageIndex());
        dto.setNewLink(model.getNewLink());
        dto.setLogicActionTypeId(model.getSurveyLogicActionType().getId());
        return dto;
    }

    public UUID getSurveyPageId() {
        return surveyPageId;
    }

    public void setSurveyPageId(UUID surveyPageId) {
        this.surveyPageId = surveyPageId;
    }

    public UUID getSurveyQuestionId() {
        return surveyQuestionId;
    }

    public void setSurveyQuestionId(UUID surveyQuestionId) {
        this.surveyQuestionId = surveyQuestionId;
    }

    public UUID getAnswerVariantId() {
        return answerVariantId;
    }

    public void setAnswerVariantId(UUID answerVariantId) {
        this.answerVariantId = answerVariantId;
    }

    public UUID getTargetQuestionId() {
        return targetQuestionId;
    }

    public void setTargetQuestionId(UUID targetQuestionId) {
        this.targetQuestionId = targetQuestionId;
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

    public UUID getLogicActionTypeId() {
        return logicActionTypeId;
    }

    public void setLogicActionTypeId(UUID logicActionTypeId) {
        this.logicActionTypeId = logicActionTypeId;
    }

    public Boolean getInteractionRequired() {
        return interactionRequired;
    }

    public void setInteractionRequired(Boolean interactionRequired) {
        this.interactionRequired = interactionRequired;
    }

    public Integer getTargetQuestionIndex() {
        return targetQuestionIndex;
    }

    public void setTargetQuestionIndex(Integer targetQuestionIndex) {
        this.targetQuestionIndex = targetQuestionIndex;
    }
}
