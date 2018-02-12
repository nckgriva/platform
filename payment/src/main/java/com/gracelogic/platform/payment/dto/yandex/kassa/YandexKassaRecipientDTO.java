package com.gracelogic.platform.payment.dto.yandex.kassa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YandexKassaRecipientDTO {
    private String gateway_id;

    public String getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    @Override
    public String toString() {
        return "YandexKassaRecipientDTO{" +
                "gateway_id='" + gateway_id + '\'' +
                '}';
    }
}
