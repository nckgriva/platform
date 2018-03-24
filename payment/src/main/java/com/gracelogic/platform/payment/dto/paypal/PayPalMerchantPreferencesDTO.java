package com.gracelogic.platform.payment.dto.paypal;

public class PayPalMerchantPreferencesDTO {
    private String id;
    private PayPalCurrencyDTO setup_fee;
    private String cancel_url;
    private String return_url;
    private String max_fail_attempts;
    private String auto_bill_amount;
    private String initial_fail_amount_action;
    private String accepted_payment_type;
    private String char_set;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PayPalCurrencyDTO getSetup_fee() {
        return setup_fee;
    }

    public void setSetup_fee(PayPalCurrencyDTO setup_fee) {
        this.setup_fee = setup_fee;
    }

    public String getCancel_url() {
        return cancel_url;
    }

    public void setCancel_url(String cancel_url) {
        this.cancel_url = cancel_url;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getMax_fail_attempts() {
        return max_fail_attempts;
    }

    public void setMax_fail_attempts(String max_fail_attempts) {
        this.max_fail_attempts = max_fail_attempts;
    }

    public String getAuto_bill_amount() {
        return auto_bill_amount;
    }

    public void setAuto_bill_amount(String auto_bill_amount) {
        this.auto_bill_amount = auto_bill_amount;
    }

    public String getInitial_fail_amount_action() {
        return initial_fail_amount_action;
    }

    public void setInitial_fail_amount_action(String initial_fail_amount_action) {
        this.initial_fail_amount_action = initial_fail_amount_action;
    }

    public String getAccepted_payment_type() {
        return accepted_payment_type;
    }

    public void setAccepted_payment_type(String accepted_payment_type) {
        this.accepted_payment_type = accepted_payment_type;
    }

    public String getChar_set() {
        return char_set;
    }

    public void setChar_set(String char_set) {
        this.char_set = char_set;
    }
}
