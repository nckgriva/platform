package com.gracelogic.platform.payment.dto.paypal;

import java.util.LinkedList;
import java.util.List;

public class PayPalBillingAgreementDTO {
    private String id;
    private String state;
    private String name;
    private String description;
    private String start_date;
    private PayPalAgreementDetailsDTO agreement_details;
    private PayPalPayerDTO payer;
    private PayPalShippingAddressDTO shipping_address;
    private PayPalMerchantPreferencesDTO override_merchant_preferences;
    private List<PayPalOverrideChargeModelDTO> override_charge_models = new LinkedList<>();
    private PayPalBAPlanDTO plan;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public PayPalAgreementDetailsDTO getAgreement_details() {
        return agreement_details;
    }

    public void setAgreement_details(PayPalAgreementDetailsDTO agreement_details) {
        this.agreement_details = agreement_details;
    }

    public PayPalPayerDTO getPayer() {
        return payer;
    }

    public void setPayer(PayPalPayerDTO payer) {
        this.payer = payer;
    }

    public PayPalShippingAddressDTO getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(PayPalShippingAddressDTO shipping_address) {
        this.shipping_address = shipping_address;
    }

    public PayPalMerchantPreferencesDTO getOverride_merchant_preferences() {
        return override_merchant_preferences;
    }

    public void setOverride_merchant_preferences(PayPalMerchantPreferencesDTO override_merchant_preferences) {
        this.override_merchant_preferences = override_merchant_preferences;
    }

    public List<PayPalOverrideChargeModelDTO> getOverride_charge_models() {
        return override_charge_models;
    }

    public void setOverride_charge_models(List<PayPalOverrideChargeModelDTO> override_charge_models) {
        this.override_charge_models = override_charge_models;
    }

    public PayPalBAPlanDTO getPlan() {
        return plan;
    }

    public void setPlan(PayPalBAPlanDTO plan) {
        this.plan = plan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
