package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyPage;

import java.util.UUID;

public class SurveyPageDTO extends IdObjectDTO {
    private UUID survey;
    private Integer pageIndex;
    private String description;
    private SurveyQuestionDTO[] questions;

    public static SurveyPageDTO prepare(SurveyPage surveyPage) {
        SurveyPageDTO model = new SurveyPageDTO();
        IdObjectDTO.prepare(model, surveyPage);
        model.setSurvey(surveyPage.getSurvey().getId());
        model.setPageIndex(surveyPage.getPageIndex());
        model.setDescription(surveyPage.getDescription());
        return model;
    }

    public UUID getSurvey() {
        return survey;
    }

    public void setSurvey(UUID survey) {
        this.survey = survey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }


    public SurveyQuestionDTO[] getQuestions() {
        return questions;
    }

    public void setQuestions(SurveyQuestionDTO[] questions) {
        this.questions = questions;
    }
}
