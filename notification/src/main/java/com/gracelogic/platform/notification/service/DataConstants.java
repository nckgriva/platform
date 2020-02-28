package com.gracelogic.platform.notification.service;

import java.util.UUID;

public class DataConstants {

    public enum NotificationMethods {
        INTERNAL(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        EMAIL(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        SMS(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73")),
        PUSH(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e74"));

        private UUID value;

        NotificationMethods(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum NotificationStates {
        QUEUED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        SENT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        DELIVERED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73"));

        private UUID value;

        NotificationStates(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }
}
