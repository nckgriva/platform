package com.gracelogic.platform.payment.dto.yandex.kassa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YandexKassaPaymentMethodDTO {
    private String id;
    private String type;
    private YandexKassaCardDTO card;
    private Boolean saved;
    private String title;
    private String phone;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public YandexKassaCardDTO getCard() {
        return card;
    }

    public void setCard(YandexKassaCardDTO card) {
        this.card = card;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getSaved() {
        return saved;
    }

    public void setSaved(Boolean saved) {
        this.saved = saved;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "YandexKassaPaymentMethodDTO{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", card='" + card + '\'' +
                ", saved=" + saved +
                ", title='" + title + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}