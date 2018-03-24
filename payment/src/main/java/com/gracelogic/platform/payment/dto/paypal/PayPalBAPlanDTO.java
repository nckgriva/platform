package com.gracelogic.platform.payment.dto.paypal;

import java.util.LinkedList;
import java.util.List;

public class PayPalBAPlanDTO {
    private String id;
    private String name;
    private String description;
    private String type;
    private String state;
    private String create_time;
    private String update_time;
    private List<PayPalPaymentDefinitionDTO> payment_definitions = new LinkedList<>();
    private List<PayPalTermsDTO> terms = new LinkedList<>();
    private PayPalMerchantPreferencesDTO merchant_preferences;
    private PayPalCurrencyCodeDTO currency_code;
    private List<PayPalLinkDescriptionDTO> links = new LinkedList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public List<PayPalPaymentDefinitionDTO> getPayment_definitions() {
        return payment_definitions;
    }

    public void setPayment_definitions(List<PayPalPaymentDefinitionDTO> payment_definitions) {
        this.payment_definitions = payment_definitions;
    }

    public List<PayPalTermsDTO> getTerms() {
        return terms;
    }

    public void setTerms(List<PayPalTermsDTO> terms) {
        this.terms = terms;
    }
}
