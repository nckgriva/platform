package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.Survey;

import java.util.Date;
import java.util.UUID;

public class SurveyDTO extends IdObjectDTO {
    private String name;
    private Date expirationDate;
    private Boolean showProgress;
    private Boolean showQuestionNumber;
    private Boolean allowReturn;
    private String introduction;
    private String conclusion;
    private Integer maxRespondents;
    private Long timeLimit;
    private UUID participationTypeId;
    private UUID ownerId;
    private Integer maxAttempts;

    public static SurveyDTO prepare(Survey model) {
        SurveyDTO dto = new SurveyDTO();
        IdObjectDTO.prepare(dto, model);
        dto.setName(model.getName());
        dto.setExpirationDate(model.getExpirationDate());
        dto.setShowProgress(model.getShowProgress());
        dto.setShowQuestionNumber(model.getShowQuestionNumber());
        dto.setAllowReturn(model.getAllowReturn());
        dto.setIntroduction(model.getIntroduction());
        dto.setConclusion(model.getConclusion());
        dto.setMaxRespondents(model.getMaxRespondents());
        dto.setTimeLimit(model.getTimeLimit());
        dto.setParticipationTypeId(model.getSurveyParticipationType().getId());
        dto.setOwnerId(model.getOwner().getId());
        dto.setMaxAttempts(model.getMaxAttempts());
        return dto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean getShowProgress() {
        return showProgress;
    }

    public void setShowProgress(Boolean showProgress) {
        this.showProgress = showProgress;
    }

    public Boolean getShowQuestionNumber() {
        return showQuestionNumber;
    }

    public void setShowQuestionNumber(Boolean showQuestionNumber) {
        this.showQuestionNumber = showQuestionNumber;
    }

    public Boolean getAllowReturn() {
        return allowReturn;
    }

    public void setAllowReturn(Boolean allowReturn) {
        this.allowReturn = allowReturn;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public Integer getMaxRespondents() {
        return maxRespondents;
    }

    public void setMaxRespondents(Integer maxRespondents) {
        this.maxRespondents = maxRespondents;
    }

    public Long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public UUID getParticipationTypeId() {
        return participationTypeId;
    }

    public void setParticipationTypeId(UUID participationTypeId) {
        this.participationTypeId = participationTypeId;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
