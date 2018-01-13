package com.gracelogic.platform.market;

import java.util.UUID;

public class DataConstants {
    public enum OrderStates {
        DRAFT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        PENDING(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        PAID(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73")),
        CANCELED(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e74"));

        private UUID value;

        OrderStates(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum DiscountTypes {
        FIX_AMOUNT_DISCOUNT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        FIX_PERCENT_DISCOUNT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        GIFT_PRODUCT(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73"));

        private UUID value;

        DiscountTypes(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum CashierVoucherTypes {
        INCOME(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        INCOME_RETURN(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        EXPENSE(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73")),
        EXPENSE_RETURN(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e74"));

        private UUID value;

        CashierVoucherTypes(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }

    public enum ProductOwnershipTypes {
        FULL(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e71")),
        LIMITED_IN_TIME(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e72")),
        SUBSCRIPTION(UUID.fromString("54480ce1-00eb-4179-a2b6-f74daa6b9e73"));

        private UUID value;

        ProductOwnershipTypes(UUID value) {
            this.value = value;
        }

        public UUID getValue() {
            return value;
        }
    }
}