package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveySession;

import java.util.Date;
import java.util.UUID;

public class SurveySessionDTO extends IdObjectDTO {
    private UUID userId;
    private String lastVisitIP;
    private UUID surveyId;
    private Date started;
    private Date ended;
    private Date expirationDate;
    private Integer[] pageVisitHistory;

    public static SurveySessionDTO prepare(SurveySession model) {
        SurveySessionDTO dto = new SurveySessionDTO();
        IdObjectDTO.prepare(dto, model);
        if (model.getUser() != null) {
            dto.setUserId(model.getUser().getId());
        }
        dto.setLastVisitIP(model.getLastVisitIP());
        if (model.getSurvey() != null) {
            dto.setSurveyId(model.getSurvey().getId());
        }
        dto.setStarted(model.getStarted());
        dto.setEnded(model.getEnded());
        dto.setPageVisitHistory(model.getPageVisitHistory());
        dto.setExpirationDate(model.getExpirationDate());
        return dto;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getLastVisitIP() {
        return lastVisitIP;
    }

    public void setLastVisitIP(String lastVisitIP) {
        this.lastVisitIP = lastVisitIP;
    }

    public UUID getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(UUID surveyId) {
        this.surveyId = surveyId;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getEnded() {
        return ended;
    }

    public void setEnded(Date ended) {
        this.ended = ended;
    }


    public Integer[] getPageVisitHistory() {
        return pageVisitHistory;
    }

    public void setPageVisitHistory(Integer[] pageVisitHistory) {
        this.pageVisitHistory = pageVisitHistory;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
