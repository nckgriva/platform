package com.gracelogic.platform.oauth;

import java.util.UUID;

public class DataConstants {

    public enum OAuthProviders {
        VK(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        OK(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        INSTAGRAM(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73")),
        FACEBOOK(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e74")),
        TWITTER(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e75")),
        GOOGLE(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e76")),
        LINKEDIN(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e77"));

        private UUID value;

        OAuthProviders(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }
}
