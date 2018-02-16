package com.gracelogic.platform.payment.dto.yandex.kassa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YandexKassaConfirmationDTO {
    private String type;
    private String return_url;
    private Boolean enforce;
    private String confirmation_url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public Boolean getEnforce() {
        return enforce;
    }

    public void setEnforce(Boolean enforce) {
        this.enforce = enforce;
    }

    public String getConfirmation_url() {
        return confirmation_url;
    }

    public void setConfirmation_url(String confirmation_url) {
        this.confirmation_url = confirmation_url;
    }

    public YandexKassaConfirmationDTO(String type, String return_url) {
        this.type = type;
        this.return_url = return_url;
    }

    public YandexKassaConfirmationDTO() {
    }

    @Override
    public String toString() {
        return "YandexKassaConfirmationDTO{" +
                "type='" + type + '\'' +
                ", return_url='" + return_url + '\'' +
                ", enforce=" + enforce +
                ", confirmation_url='" + confirmation_url + '\'' +
                '}';
    }
}
