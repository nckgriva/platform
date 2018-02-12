package com.gracelogic.platform.payment.dto.yandex.kassa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YandexKassaNotificationDTO {
    private String type;
    private String event;
    private YandexKassaPaymentDTO object;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public YandexKassaPaymentDTO getObject() {
        return object;
    }

    public void setObject(YandexKassaPaymentDTO object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "YandexKassaNotificationDTO{" +
                "type='" + type + '\'' +
                ", event='" + event + '\'' +
                ", object=" + object +
                '}';
    }
}
