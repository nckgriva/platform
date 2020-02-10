package com.gracelogic.platform.user.service;

import java.util.UUID;

public class DataConstants {
    public enum PassphraseEncryptors {
        OPEN(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        SHA1_WITH_SALT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72"));
        private UUID value;

        PassphraseEncryptors(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum PassphraseTypes {
        USER_PASSWORD(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        VERIFICATION_CODE(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72"));
        private UUID value;

        PassphraseTypes(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum PassphraseStates {
        ACTUAL(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        ARCHIVE(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72"));
        private UUID value;

        PassphraseStates(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum IdentifierTypes {
        USER_ID(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        EMAIL(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        PHONE(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73")),
        LOGIN(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e74"));
        private UUID value;

        IdentifierTypes(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum UserApproveMethods {
        AUTO("AUTO"),
        MANUAL("MANUAL");

        private String value;

        UserApproveMethods(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
