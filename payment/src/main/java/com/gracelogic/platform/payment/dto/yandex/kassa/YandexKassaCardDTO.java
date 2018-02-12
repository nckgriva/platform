package com.gracelogic.platform.payment.dto.yandex.kassa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YandexKassaCardDTO {
    private String last4;
    private String expiry_month;
    private String expiry_year;
    private String card_type;

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getExpiry_month() {
        return expiry_month;
    }

    public void setExpiry_month(String expiry_month) {
        this.expiry_month = expiry_month;
    }

    public String getExpiry_year() {
        return expiry_year;
    }

    public void setExpiry_year(String expiry_year) {
        this.expiry_year = expiry_year;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    @Override
    public String toString() {
        return "YandexKassaCardDTO{" +
                "last4='" + last4 + '\'' +
                ", expiry_month='" + expiry_month + '\'' +
                ", expiry_year='" + expiry_year + '\'' +
                ", card_type='" + card_type + '\'' +
                '}';
    }
}
