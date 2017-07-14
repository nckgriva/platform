package com.gracelogic.platform.filestorage.service;

import java.util.UUID;

public class DataConstants {

    public enum StoreModes {
        LOCAL(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        EXTERNAL_LINK(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72"));

        private UUID value;

        StoreModes(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }
}
