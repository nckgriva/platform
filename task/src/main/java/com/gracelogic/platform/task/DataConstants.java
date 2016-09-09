package com.gracelogic.platform.task;

import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 18.07.2016
 * Time: 14:52
 */
public class DataConstants {

    public enum TaskExecutionMethods {
        MANUAL(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        CRON(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72"));

        private UUID value;

        TaskExecutionMethods(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum TaskExecutionStates {
        CREATED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        IN_PROGRESS(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        COMPLETED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73")),
        FAIL(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e74"));

        private UUID value;

        TaskExecutionStates(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

}
