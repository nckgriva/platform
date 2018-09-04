package com.gracelogic.platform.content.service;

import java.util.UUID;

public class DataConstants {

    public enum ElementFieldTypes {
        SINGLE_LINE_TEXT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        MULTI_LINE_TEXT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        CHECK_BOX(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73")),
        COMBO_BOX(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e74")),
        STORED_FILE(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e75"));

        private UUID value;

        ElementFieldTypes(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }
}
