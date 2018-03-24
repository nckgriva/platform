package com.gracelogic.platform.payment.dto.paypal;

public class PayPalShippingAddressDTO extends PayPalAddressDTO {
    private String recipient_name;
    private String default_address;

    public String getRecipient_name() {
        return recipient_name;
    }

    public void setRecipient_name(String recipient_name) {
        this.recipient_name = recipient_name;
    }

    public String getDefault_address() {
        return default_address;
    }

    public void setDefault_address(String default_address) {
        this.default_address = default_address;
    }
}
