package com.gracelogic.platform.payment.dto.yandex.kassa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YandexKassaPaymentDTO {
    private String id;
    private String status;
    private Boolean paid;
    private String created_at;
    private String expires_at;
    private String captured_at;
    private YandexKassaAmountDTO amount;
    private YandexKassaConfirmationDTO confirmation;
    private YandexKassaPaymentMethodDTO payment_method;
    private YandexKassaRecipientDTO recipient;
    private Boolean test;
    private YandexKassaAmountDTO refunded_amount;
    private String receipt_registration;
    private String description;
    private Map<String, String> metadata;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(String expires_at) {
        this.expires_at = expires_at;
    }

    public String getCaptured_at() {
        return captured_at;
    }

    public void setCaptured_at(String captured_at) {
        this.captured_at = captured_at;
    }

    public YandexKassaAmountDTO getAmount() {
        return amount;
    }

    public void setAmount(YandexKassaAmountDTO amount) {
        this.amount = amount;
    }

    public YandexKassaConfirmationDTO getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(YandexKassaConfirmationDTO confirmation) {
        this.confirmation = confirmation;
    }

    public YandexKassaPaymentMethodDTO getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(YandexKassaPaymentMethodDTO payment_method) {
        this.payment_method = payment_method;
    }

    public YandexKassaRecipientDTO getRecipient() {
        return recipient;
    }

    public void setRecipient(YandexKassaRecipientDTO recipient) {
        this.recipient = recipient;
    }

    public Boolean getTest() {
        return test;
    }

    public void setTest(Boolean test) {
        this.test = test;
    }

    public YandexKassaAmountDTO getRefunded_amount() {
        return refunded_amount;
    }

    public void setRefunded_amount(YandexKassaAmountDTO refunded_amount) {
        this.refunded_amount = refunded_amount;
    }

    public String getReceipt_registration() {
        return receipt_registration;
    }

    public void setReceipt_registration(String receipt_registration) {
        this.receipt_registration = receipt_registration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "YandexKassaPaymentDTO{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", paid=" + paid +
                ", created_at='" + created_at + '\'' +
                ", expires_at='" + expires_at + '\'' +
                ", captured_at='" + captured_at + '\'' +
                ", amount=" + amount +
                ", confirmation=" + confirmation +
                ", payment_method=" + payment_method +
                ", recipient=" + recipient +
                ", test=" + test +
                ", refunded_amount=" + refunded_amount +
                ", receipt_registration='" + receipt_registration + '\'' +
                ", description='" + description + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
