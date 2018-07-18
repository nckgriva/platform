package com.gracelogic.platform.survey.dto.user;

import com.gracelogic.platform.survey.dto.admin.SurveyPageDTO;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SurveyInteractionDTO {
    private UUID surveySessionId;
    private SurveyPageDTO surveyPage;
    private SurveyConclusionDTO surveyConclusion;
    private HashMap<UUID, List<AnswerDTO>> pageAnswers;

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

    public HashMap<UUID, List<AnswerDTO>> getPageAnswers() {
        return pageAnswers;
    }

    public void setPageAnswers(HashMap<UUID, List<AnswerDTO>> pageAnswers) {
        this.pageAnswers = pageAnswers;
    }
}
