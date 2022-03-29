package com.gracelogic.platform.payment.dto.alfabank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlfaBankOrderStatusResponseDTO {
    private Integer orderStatus;
    private String orderNumber;
    private String errorCode;
    private String errorMessage;
    private Integer actionCode;
    private Long amount;
    private String currency;
    private MerchantOrderParam[] merchantOrderParams;

    /**
     * По значению этого параметра определяется состояние заказа в платёжной системе. Список возможных значений приведён в списке ниже.
     * Отсутствует, если заказ не был найден.
     * 0 - Заказ зарегистрирован, но не оплачен;
     * 1 - Предавторизованная сумма захолдирована (для двухстадийных платежей);
     * 2 - Проведена полная авторизация суммы заказа;
     * 3 - Авторизация отменена;
     * 4 - По транзакции была проведена операция возврата;
     * 5 - Инициирована авторизация через ACS банка-эмитента;
     * 6 - Авторизация отклонена.
     * @return
     */
    public Integer getOrderStatus() {
        return orderStatus;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Integer getActionCode() {
        return actionCode;
    }

    /**
     * Сумма платежа в копейках (или центах)
     * @return
     */
    public Long getAmount() {
        return amount;
    }

    /**
     * Код валюты платежа ISO 4217. Если не указан, считается равным 810 (российские рубли).
     * @return
     */
    public String getCurrency() {
        return currency;
    }

    public MerchantOrderParam[] getMerchantOrderParams() {
        return merchantOrderParams;
    }

    @Override
    public String toString() {
        return "AlfaBankOrderStatusResponseDTO{" +
                "orderStatus=" + orderStatus +
                ", orderNumber='" + orderNumber + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", actionCode=" + actionCode +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }
}
