package com.gracelogic.platform.survey.dto.admin;

import java.util.Set;
import java.util.UUID;

public class GetSurveyQuestionsRequest {
    private UUID surveyId;
    private UUID surveyPageId;
    private Set<UUID> surveyQuestionTypes;
    private String text;
    private Boolean withVariants;
    private Integer start;
    private Integer count;
    private String sortField;
    private String sortDir;

    public UUID getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(UUID surveyId) {
        this.surveyId = surveyId;
    }

    public UUID getSurveyPageId() {
        return surveyPageId;
    }

    public void setSurveyPageId(UUID surveyPageId) {
        this.surveyPageId = surveyPageId;
    }

    public Set<UUID> getSurveyQuestionTypes() {
        return surveyQuestionTypes;
    }

    public void setSurveyQuestionTypes(Set<UUID> surveyQuestionTypes) {
        this.surveyQuestionTypes = surveyQuestionTypes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getWithVariants() {
        return withVariants;
    }

    public void setWithVariants(Boolean withVariants) {
        this.withVariants = withVariants;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }
}
