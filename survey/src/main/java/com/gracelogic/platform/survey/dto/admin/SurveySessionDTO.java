package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveySession;

import java.util.Date;
import java.util.UUID;

public class SurveySessionDTO extends IdObjectDTO {
    private UUID user;
    private String lastVisitIP;
    private UUID survey;
    private Date started;
    private Date ended;
    private Date expirationDate;
    private Integer lastVisitedPageIndex;

    public static SurveySessionDTO prepare(SurveySession surveySession) {
        SurveySessionDTO model = new SurveySessionDTO();
        IdObjectDTO.prepare(model, surveySession);
        model.setUser(surveySession.getUser().getId());
        model.setLastVisitIP(surveySession.getLastVisitIP());
        model.setSurvey(surveySession.getSurvey().getId());
        model.setStarted(surveySession.getStarted());
        model.setEnded(surveySession.getEnded());
        model.setLastVisitedPageIndex(surveySession.getLastVisitedPageIndex());
        model.setExpirationDate(surveySession.getExpirationDate());
        return model;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public String getLastVisitIP() {
        return lastVisitIP;
    }

    public void setLastVisitIP(String lastVisitIP) {
        this.lastVisitIP = lastVisitIP;
    }

    public UUID getSurvey() {
        return survey;
    }

    public void setSurvey(UUID survey) {
        this.survey = survey;
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

    public Integer getLastVisitedPageIndex() {
        return lastVisitedPageIndex;
    }

    public void setLastVisitedPageIndex(Integer lastVisitedPageIndex) {
        this.lastVisitedPageIndex = lastVisitedPageIndex;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
