package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyQuestionAnswer;

import java.util.UUID;

public class SurveyQuestionAnswerDTO extends IdObjectDTO {
    private UUID surveySession;
    private UUID question;
    private UUID answerVariant;
    private String textAnswer;
    private UUID storedFile;

    public static SurveyQuestionAnswerDTO prepare(SurveyQuestionAnswer answer) {
        SurveyQuestionAnswerDTO model = new SurveyQuestionAnswerDTO();
        IdObjectDTO.prepare(model, answer);
        model.setSurveySession(answer.getSurveySession().getId());
        model.setQuestion(answer.getQuestion().getId());
        if (answer.getAnswerVariant() != null) model.setAnswerVariant(answer.getAnswerVariant().getId());
        model.setTextAnswer(answer.getTextAnswer());
        if (answer.getStoredFile() != null) model.setStoredFile(answer.getStoredFile().getId());

        return model;
    }

    public UUID getSurveySession() {
        return surveySession;
    }

    public void setSurveySession(UUID surveySession) {
        this.surveySession = surveySession;
    }

    public UUID getQuestion() {
        return question;
    }

    public void setQuestion(UUID question) {
        this.question = question;
    }

    public UUID getAnswerVariant() {
        return answerVariant;
    }

    public void setAnswerVariant(UUID answerVariant) {
        this.answerVariant = answerVariant;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public UUID getStoredFile() {
        return storedFile;
    }

    public void setStoredFile(UUID storedFile) {
        this.storedFile = storedFile;
    }
}
