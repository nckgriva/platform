package com.gracelogic.platform.survey.dto.user;

import com.gracelogic.platform.survey.dto.admin.SurveyPageDTO;

import java.util.UUID;

public class SurveyInteractionDTO {
    private UUID surveySessionId;
    private SurveyPageDTO surveyPage;
    private SurveyConclusionDTO surveyConclusion;

    public UUID getSurveySessionId() {
        return surveySessionId;
    }

    public void setSurveySessionId(UUID surveySessionId) {
        this.surveySessionId = surveySessionId;
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
