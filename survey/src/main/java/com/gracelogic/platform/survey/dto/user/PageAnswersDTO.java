package com.gracelogic.platform.survey.dto.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class PageAnswersDTO {
    // question id, answer
    private HashMap<UUID, AnswerDTO> answers;

    public String getQuestionIdsSeparatedByCommas() {
        String str = "";
        Set<UUID> ids = answers.keySet();
        int i = 0;
        for (UUID uuid : ids) {
            str += "'" + uuid + "'";
            i++;
            if (i != ids.size()-1) str += ", ";
        }
        return str;
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
