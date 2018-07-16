package com.gracelogic.platform.survey.service;

import java.util.UUID;

public class DataConstants {

    public enum LogicActionTypes {
        /**
         * Hides a question on client machine
         */
        HIDE_QUESTION(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        /**
         * Shows a question on client machine
         */
        SHOW_QUESTION(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),

        /**
         * Ends survey
         */
        END_SURVEY(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73")),

        /**
         * Changes conclusion of passing survey
         */
        CHANGE_CONCLUSION(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e74")),
        /**
         * Change url when survey ends
         */
        CHANGE_LINK(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e75")),
        /**
         * Go to specified page
         */
        GO_TO_PAGE(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e76"));

        private UUID value;

        LogicActionTypes(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum ParticipationTypes {
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

        ParticipationTypes(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum QuestionTypes {
        RADIOBUTTON(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        CHECKBOX(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        TEXT_SINGLE_LINE(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73")),
        COMBOBOX(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e74")),
        LISTBOX(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e75")),
        RATING_SCALE(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e76")),
        ATTACHMENT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e77")),
        TEXT_MULTILINE(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e78")),
        MATRIX_RADIOBUTTON(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e79")),
        MATRIX_CHECKBOX(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e80"));

        private UUID value;

        QuestionTypes(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }
}
