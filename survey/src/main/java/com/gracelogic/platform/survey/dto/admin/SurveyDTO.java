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
    private Integer maximumRespondents;
    private Long timeLimit;
    private UUID participationType;
    private UUID owner;
    private Integer maxAttempts;

    public static SurveyDTO prepare(Survey survey) {
        SurveyDTO model = new SurveyDTO();
        IdObjectDTO.prepare(model, survey);
        model.setName(survey.getName());
        model.setExpirationDate(survey.getExpirationDate());
        model.setShowProgress(survey.getShowProgress());
        model.setShowQuestionNumber(survey.getShowQuestionNumber());
        model.setAllowReturn(survey.getAllowReturn());
        model.setIntroduction(survey.getIntroduction());
        model.setConclusion(survey.getConclusion());
        model.setMaximumRespondents(survey.getMaximumRespondents());
        model.setTimeLimit(survey.getTimeLimit());
        model.setParticipationType(survey.getParticipationType());
        model.setOwner(survey.getOwner().getId());
        model.setMaxAttempts(survey.getMaxAttempts());
        return model;
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

    public Integer getMaximumRespondents() {
        return maximumRespondents;
    }

    public void setMaximumRespondents(Integer maximumRespondents) {
        this.maximumRespondents = maximumRespondents;
    }

    public Long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public UUID getParticipationType() {
        return participationType;
    }

    public void setParticipationType(UUID participationType) {
        this.participationType = participationType;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
