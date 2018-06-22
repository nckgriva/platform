package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyPage;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SurveyPageDTO extends IdObjectDTO {
    private UUID surveyId;
    private Integer pageIndex;
    private String description;
    private List<SurveyQuestionDTO> questions = new LinkedList<>();

    public static SurveyPageDTO prepare(SurveyPage model) {
        SurveyPageDTO dto = new SurveyPageDTO();
        IdObjectDTO.prepare(dto, model);
        if (model.getSurvey() != null) {
            dto.setSurveyId(model.getSurvey().getId());
        }
        dto.setPageIndex(model.getPageIndex());
        dto.setDescription(model.getDescription());
        return dto;
    }

    public UUID getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(UUID surveyId) {
        this.surveyId = surveyId;
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

    public List<SurveyQuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestionDTO> questions) {
        this.questions = questions;
    }
}
