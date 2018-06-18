package com.gracelogic.platform.survey.dto.user;

import com.gracelogic.platform.survey.dto.admin.SurveyPageDTO;

import java.util.UUID;

public class SurveyPassingDTO {
    private UUID surveyPassingId;
    private SurveyPageDTO surveyPage;
    private SurveyConclusionDTO surveyConclusion;

    public UUID getSurveyPassingId() {
        return surveyPassingId;
    }

    public void setSurveyPassingId(UUID surveyPassingId) {
        this.surveyPassingId = surveyPassingId;
    }

    public SurveyPageDTO getSurveyPage() {
        return surveyPage;
    }

    public void setSurveyPage(SurveyPageDTO surveyPage) {
        this.surveyPage = surveyPage;
    }

    public SurveyConclusionDTO getSurveyConclusion() {
        return surveyConclusion;
    }

    public void setSurveyConclusion(SurveyConclusionDTO surveyConclusion) {
        this.surveyConclusion = surveyConclusion;
    }
}
