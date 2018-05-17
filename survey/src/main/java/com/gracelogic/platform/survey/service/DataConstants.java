package com.gracelogic.platform.survey.service;

import java.util.UUID;

public class DataConstants {

    public enum LogicRelationType {
        /**
         * Hides a question when specified answer is selected
         */
        HIDES_QUESTION(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        /**
         * Shows a question when specified answer is selected
         */
        SHOWS_QUESTION(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72"));

        private UUID value;

        LogicRelationType(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum AnswerSavingType {
        /**
         * All answers from single page received in PageAnswersDTO
         */
        DEFAULT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        /**
         * Sends single answer in AnswerDTO when it was selected by user
         */
        INSTANT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72"));

        private UUID value;

        AnswerSavingType(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum ParticipationType {
        /**
         * All users can participate in survey as many times as they like
         */
        UNLIMITED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        /**
         * Participation is limited by cookies
         */
        COOKIE_LIMITED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        /**
         * Participation is limited by user ip-address
         */
        IP_LIMITED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73")),
        /**
         * Participation is limited by cookies and by ip-address
         */
        COOKIE_IP_LIMITED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e74")),
        /**
         * Authorization is required to participate in survey
         */
        AUTHORIZATION_REQUIRED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e75"));

        private UUID value;

        ParticipationType(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum QuestionType {
        RADIOBUTTON(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        CHECKBOX(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        TEXT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73")),
        COMBOBOX(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e74")),
        LISTBOX(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e75")),
        RATING_SCALE(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e76")),
        ATTACHMENT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e77"));

        private UUID value;

        QuestionType(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }
}
