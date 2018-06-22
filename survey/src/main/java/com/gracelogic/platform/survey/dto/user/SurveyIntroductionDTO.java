package com.gracelogic.platform.survey.dto.user;

import com.gracelogic.platform.survey.model.Survey;

public class SurveyIntroductionDTO {
    private String name;
    private String introduction;
    private Long timeLimit;
    private Boolean showQuestionNumber;
    private Boolean allowReturn;
    private Boolean showProgress;

    public SurveyIntroductionDTO(Survey survey) {
        this.name = survey.getName();
        this.introduction = survey.getIntroduction();
        this.allowReturn = survey.getAllowReturn();
        this.showQuestionNumber = survey.getShowQuestionNumber();
        this.allowReturn = survey.getAllowReturn();
        this.showProgress = survey.getShowProgress();
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

    public boolean isShowQuestionNumber() {
        return showQuestionNumber;
    }

    public void setShowQuestionNumber(boolean showQuestionNumber) {
        this.showQuestionNumber = showQuestionNumber;
    }

    public boolean isAllowReturn() {
        return allowReturn;
    }

    public void setAllowReturn(boolean allowReturn) {
        this.allowReturn = allowReturn;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
