package com.gracelogic.platform.survey.service;

import java.util.UUID;

public class DataConstants {

    public enum RelationType {
        HIDES_QUESTION(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        SHOWS_QUESTION(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72"));

        private UUID value;

        RelationType(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum ParticipationType {
        UNLIMITED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        COOKIE_LIMITED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        IP_LIMITED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73")),
        COOKIE_IP_LIMITED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e74")),
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
