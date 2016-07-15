package com.gracelogic.platform.user.service;

import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 21.03.2015
 * Time: 16:39
 */
public class DataConstants {

    public enum AuthCodeTypes {
        ACTIVATION(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        LOAN_REQUEST_TERM_ACCEPTANCE(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        PASSWORD_REPAIR(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73")),
        EMAIL_VERIFY(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e74")),
        LOAN_TERM_ACCEPTANCE(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e75"));
        private UUID value;

        AuthCodeTypes(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum AuthCodeStates {
        NEW(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        DELETED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72"));
        private UUID value;

        AuthCodeStates(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum UserSettingKey {
        DAILY_RATE("DAILY_RATE");

        private String value;

        UserSettingKey(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
