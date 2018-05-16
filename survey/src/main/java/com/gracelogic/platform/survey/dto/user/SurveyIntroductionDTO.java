package com.gracelogic.platform.survey.dto.user;

public class SurveyIntroductionDTO {
    private String introduction;
    private Long timeLimit;
    private Integer totalQuestions;

    public SurveyIntroductionDTO(String introduction, Long timeLimit, Integer totalQuestions) {
        this.introduction = introduction;
        this.timeLimit = timeLimit;
        this.totalQuestions = totalQuestions;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
}
