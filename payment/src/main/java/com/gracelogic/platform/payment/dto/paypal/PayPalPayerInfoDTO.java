package com.gracelogic.platform.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayPalPayerInfoDTO {
    private String email;
    private String first_name;
    private String last_name;
    private String payer_id;
    private PayPalAddressDTO shipping_address;
    private PayPalAddressDTO billing_address;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPayer_id() {
        return payer_id;
    }

    public void setPayer_id(String payer_id) {
        this.payer_id = payer_id;
    }

    public PayPalAddressDTO getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(PayPalAddressDTO shipping_address) {
        this.shipping_address = shipping_address;
    }

    public PayPalAddressDTO getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(PayPalAddressDTO billing_address) {
        this.billing_address = billing_address;
    }
}
