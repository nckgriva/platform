package com.gracelogic.platform.survey.dto.user;

import java.util.*;

public class PageAnswersDTO {
    // question id, answer
    private HashMap<UUID, List<AnswerDTO>> answers;

    public boolean containsStoredFiles() {
        for (Map.Entry<UUID, List<AnswerDTO>> entry : answers.entrySet()) {
            for (AnswerDTO answerDTO: entry.getValue())
                if (answerDTO.getStoredFileId() != null) return true;
        }
        return false;
    }

    public boolean containsNonTextAnswers() {
        for (Map.Entry<UUID, List<AnswerDTO>> entry : answers.entrySet()) {
            for (AnswerDTO answerDTO: entry.getValue())
                if (answerDTO.getAnswerId() != null) return true;
        }
        return false;
    }

    public HashMap<UUID, List<AnswerDTO>> getAnswers() {
        return answers;
    }

    public void setAnswers(HashMap<UUID, List<AnswerDTO>> answers) {
        this.answers = answers;
    }
}
