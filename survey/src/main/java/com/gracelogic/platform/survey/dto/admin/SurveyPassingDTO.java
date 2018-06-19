package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyPassing;

import java.util.Date;
import java.util.UUID;

public class SurveyPassingDTO extends IdObjectDTO {
    private UUID user;
    private String lastVisitIP;
    private UUID survey;
    private Date started;
    private Date ended;
    private Integer lastVisitedPageIndex;

    public static SurveyPassingDTO prepare(SurveyPassing surveyPassing) {
        SurveyPassingDTO model = new SurveyPassingDTO();
        IdObjectDTO.prepare(model, surveyPassing);
        model.setUser(surveyPassing.getUser().getId());
        model.setLastVisitIP(surveyPassing.getLastVisitIP());
        model.setSurvey(surveyPassing.getSurvey().getId());
        model.setStarted(surveyPassing.getStarted());
        model.setEnded(surveyPassing.getEnded());
        model.setLastVisitedPageIndex(surveyPassing.getLastVisitedPageIndex());
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
}
