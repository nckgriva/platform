package com.gracelogic.platform.payment;

import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 18.07.2016
 * Time: 14:52
 */
public class DataConstants {

    public enum PaymentSystems {
        MANUAL(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e61")),
        YANDEX_MONEY(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        SBRF(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        ELECSNET(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73"));
        private UUID value;

        PaymentSystems(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum PaymentStates {
        CREATED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        ACTIVATED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        CANCELLED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73"));
        private UUID value;

        PaymentStates(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum TransactionTypes {
        INCOMING_PAYMENT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        OUTGOING_PAYMENT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72"));

        private UUID value;

        TransactionTypes(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }
}
