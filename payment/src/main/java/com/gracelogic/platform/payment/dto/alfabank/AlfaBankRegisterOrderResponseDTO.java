package com.gracelogic.platform.payment.dto.alfabank;

public class AlfaBankRegisterOrderResponseDTO {
    private String errorCode;
    private String errorMessage;
    private String formUrl;
    private String orderId;

    /**
     * Код ошибки.
     * @return
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Описание ошибки на языке, переданном в параметре language в запросе.
     * @return
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * URL платёжной формы, на который надо перенаправить броузер клиента.
     * Не возвращается если регистрация заказа не удалась по причине ошибки, детализированной в errorCode.
     * @return
     */
    public String getFormUrl() {
        return formUrl;
    }

    /**
     * Номер заказа в платёжной системе. Уникален в пределах системы.
     * Отсутствует если регистрация заказа на удалась по причине ошибки, детализированной в errorCode.
     * @return
     */
    public String getOrderId() {
        return orderId;
    }
}
