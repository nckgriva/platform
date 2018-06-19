package com.gracelogic.platform.survey.dto.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PageAnswersDTO {
    // question id, answer
    private HashMap<UUID, AnswerDTO> answers;

    public boolean containsStoredFiles() {
        for (Map.Entry<UUID, AnswerDTO> entry : answers.entrySet()) {
            if (entry.getValue().getStoredFileId() != null) return true;
        }
        return false;
    }

    public boolean containsNonTextAnswers() {
        for (Map.Entry<UUID, AnswerDTO> entry : answers.entrySet()) {
            if (entry.getValue().getAnswerId() != null) return true;
        }
        return false;
    }

    public String getAnswerIdsSeparatedByCommas() {
        String str = "";
        Collection<AnswerDTO> collection = answers.values();
        int i = 0;
        for (AnswerDTO answerDTO : collection) {
            str += "'" + answerDTO.getAnswerId() + "'";
            i++;
            if (i != collection.size()-1) str += ", ";
        }
        return str;
    }

    public HashMap<UUID, AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(HashMap<UUID, AnswerDTO> answers) {
        this.answers = answers;
    }
}
