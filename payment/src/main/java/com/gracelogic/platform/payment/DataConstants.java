package com.gracelogic.platform.payment;

import java.util.UUID;

public class DataConstants {

    public enum PaymentSystems {
        MANUAL(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e61")),
        BANK_IMPORT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e62")),
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
        OUTGOING_PAYMENT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        CANCEL_PAYMENT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e99")),
        MARKET_BUY(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e13")),
        MARKET_SELL(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e14")),
        MARKET_BUY_CANCEL(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e15")),
        MARKET_SELL_CANCEL(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e16"));

        private UUID value;

        TransactionTypes(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }
}
