package com.gracelogic.platform.payment.dto.alfabank;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;

public class AlfaBankRegisterOrderDTO {
    private String userName;
    private String password;
    private String token;
    private String orderNumber;
    private Long amount;
    private Integer currency;
    private String returnUrl;
    private String failUrl;
    private String description;
    private String ip;
    private String language;
    private String pageView;
    private String clientId;
    private String merchantLogin;
    private String email;
    private String postAddress;
    private String jsonParams;
    private String additionalOfdParams;
    private Integer sessionTimeoutSecs;
    private String expirationDate;
    private String autocompletionDate;
    private String bindingId;
    private String orderBundle;
    private String features;
    private String prepaymentMdOrder;
    private String dynamicCallbackUrl;
    private String feeInput;

    public List<NameValuePair> getNameValuePairs() {
        List<NameValuePair> list = new LinkedList<>();
        if (userName != null) list.add(new BasicNameValuePair("userName", userName));
        if (password != null) list.add(new BasicNameValuePair("password", password));
        if (token != null) list.add(new BasicNameValuePair("token", token));
        if (orderNumber != null) list.add(new BasicNameValuePair("orderNumber", orderNumber));
        if (amount != null) list.add(new BasicNameValuePair("amount", amount.toString()));
        if (currency != null) list.add(new BasicNameValuePair("currency", currency.toString()));
        if (returnUrl != null) list.add(new BasicNameValuePair("returnUrl", returnUrl));
        if (failUrl != null) list.add(new BasicNameValuePair("failUrl", failUrl));
        if (description != null) list.add(new BasicNameValuePair("description", description));
        if (ip != null) list.add(new BasicNameValuePair("ip", ip));
        if (language != null) list.add(new BasicNameValuePair("language", language));
        if (pageView != null) list.add(new BasicNameValuePair("pageView", pageView));
        if (clientId != null) list.add(new BasicNameValuePair("clientId", clientId));
        if (merchantLogin != null) list.add(new BasicNameValuePair("merchantLogin", merchantLogin));
        if (email != null) list.add(new BasicNameValuePair("email", email));
        if (postAddress != null) list.add(new BasicNameValuePair("postAddress", postAddress));
        if (jsonParams != null) list.add(new BasicNameValuePair("jsonParams", jsonParams));
        if (additionalOfdParams != null) list.add(new BasicNameValuePair("additionalOfdParams", additionalOfdParams));
        if (sessionTimeoutSecs != null) list.add(new BasicNameValuePair("sessionTimeoutSecs", sessionTimeoutSecs.toString()));
        if (expirationDate != null) list.add(new BasicNameValuePair("expirationDate", expirationDate));
        if (autocompletionDate != null) list.add(new BasicNameValuePair("autocompletionDate", autocompletionDate));
        if (bindingId != null) list.add(new BasicNameValuePair("bindingId", bindingId));
        if (orderBundle != null) list.add(new BasicNameValuePair("orderBundle", orderBundle));
        if (features != null) list.add(new BasicNameValuePair("features", features));
        if (prepaymentMdOrder != null) list.add(new BasicNameValuePair("prepaymentMdOrder", prepaymentMdOrder));
        if (dynamicCallbackUrl != null) list.add(new BasicNameValuePair("dynamicCallbackUrl", dynamicCallbackUrl));
        if (feeInput != null) list.add(new BasicNameValuePair("feeInput", feeInput));

        return list;
    }

    /**
     * Логин магазина, полученный при подключении.
     * Если вместо аутентификации по логину и паролю используется открытый токен (параметр token), параметр userName передавать не нужно.
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Пароль магазина, полученный при подключении.
     * Если для аутентификации при регистрации вместо логина и пароля используется открытый токен (параметр token), параметр password передавать не нужно.
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Открытый ключ, который можно использовать для регистрации заказа.
     * Если для аутентификации при регистрации заказа используются логин и пароль, параметр token передавать не нужно.
     * @return
     */
    public String getToken() {
        return token;
    }

    /**
     * REQUIRED. Номер (идентификатор) заказа в системе магазина, уникален для каждого магазина в пределах системы.
     * @return
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * REQUIRED. Сумма платежа в копейках (или центах)
     * @return
     */
    public Long getAmount() {
        return amount;
    }

    /**
     * Код валюты платежа ISO 4217. Если не указан, считается равным коду валюты по умолчанию.
     * @return
     */
    public Integer getCurrency() {
        return currency;
    }

    /**
     * REQUIRED. Адрес, на который требуется перенаправить пользователя в случае успешной оплаты.
     * Должен быть указан полностью, включая используемый протокол (например, https://test.ru вместо test.ru).
     * В противном случае пользователь будет перенаправлен по адресу следующего вида: http://<адресплатёжногошлюза>/<адрес_продавца>.
     * @return
     */
    public String getReturnUrl() {
        return returnUrl;
    }

    /**
     * Адрес, на который требуется перенаправить пользователя в случае неуспешной оплаты.
     * Должен быть указан полностью, включая используемый протокол (например, https://test.ru вместо test.ru).
     * В противном случае пользователь будет перенаправлен по адресу следующего вида: http://<адресплатёжногошлюза>/<адрес_продавца>.
     * @return
     */
    public String getFailUrl() {
        return failUrl;
    }

    /**
     * Описание заказа в свободной форме
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * IP-адрес покупателя.
     * @return
     */
    public String getIp() {
        return ip;
    }

    /**
     * Язык в кодировке ISO 639-1. Если не указан, будет использован язык, указанный в настройках магазина как язык по умолчанию (default language).
     * @return
     */
    public String getLanguage() {
        return language;
    }

    /**
     * По значению данного параметра определяется, какие страницы платёжного интерфейса должны загружаться для клиента.
     * Если параметр отсутствует, либо не соответствует формату, то по умолчанию считается pageView=DESKTOP.
     * Допустимые значения: DESKTOP/MOBILE
     * @return
     */
    public String getPageView() {
        return pageView;
    }

    /**
     * Номер (идентификатор) клиента в системе магазина.
     * Используется для реализации функционала связок. Может присутствовать, если магазину разрешено создание связок.
     * @return
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Чтобы зарегистрировать заказ от имени дочернего мерчанта, укажите его логин в этом параметре.
     * @return
     */
    public String getMerchantLogin() {
        return merchantLogin;
    }

    /**
     * Адрес электронной почты покупателя.
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Адрес доставки
     * @return
     */
    public String getPostAddress() {
        return postAddress;
    }

    /**
     * Блок для передачи дополнительных параметров мерчанта.
     * Формат вида: {"Имя1": "Значение1", "Имя2": "Значение2"}.
     * @return
     */
    public String getJsonParams() {
        return jsonParams;
    }

    /**
     * Блок дополнительных параметров для ОФД.
     * Пример:
     * {
     * "agent_info.type": "7",
     * "agent_info.paying.operation": "Наименование операции ма",
     * "agent_info.paying.phones": "+71111111111",
     * "agent_info.paymentsOperator.phones": "+72222222222",
     * "agent_info.MTOperator.address": "Адрес оператора перевода",
     * "agent_info.MTOperator.inn": "169910020020",
     * "agent_info.MTOperator.name": "Наименование оператора перевода",
     * "agent_info.MTOperator.phones": "+73333333333",
     * "supplier_info.phones": "+74444444444",
     * "cashier": “ФИО кассира”
     * "additional_check_props": "09090909",
     * "additional_user_props.name": "Наименование дополнительного реквизита пользователя",
     * "additional_user_props.value": "Значение дополнительного реквизита пользователя",
     * }
     * @return
     */
    public String getAdditionalOfdParams() {
        return additionalOfdParams;
    }

    /**
     * Продолжительность жизни заказа в секундах.
     * В случае если параметр не задан, будет использовано значение, указанное в настройках мерчанта или время по умолчанию (1200 секунд = 20 минут).
     * Если в запросе присутствует параметр expirationDate, то значение параметра sessionTimeoutSecs не учитывается.
     * @return
     */
    public Integer getSessionTimeoutSecs() {
        return sessionTimeoutSecs;
    }

    /**
     * Дата и время окончания жизни заказа. Формат: yyyy-MM-dd’T’HH:mm:ss.
     * Если этот параметр не передаётся в запросе, то для определения времени окончания жизни заказа используется sessionTimeoutSecs.
     * @return
     */
    public String getExpirationDate() {
        return expirationDate;
    }

    /**
     * Время автозавершения заказа. Если заказ не был завершен ко времени указанному в autocompletionDate, то он завершится автоматически.
     * Тип: ANS. Формат параметра: yyyy-MM-dd’T’HH:mm:ss. Пример: “2017-12-29T13:02:51”
     * @return
     */
    public String getAutocompletionDate() {
        return autocompletionDate;
    }

    /**
     * Идентификатор связки, созданной ранее. Может использоваться, только если у магазина есть разрешение на работу со связками.
     * Если этот параметр передаётся в данном запросе, то это означает:
     * 1. Данный заказ может быть оплачен только с помощью связки;
     * 2. Плательщик будет перенаправлен на платёжную страницу, где требуется только ввод CVC.
     * @return
     */
    public String getBindingId() {
        return bindingId;
    }

    /**
     * Корзина товаров заказа
     *
     * Пример:
     * "{"cartItems":
     * {"items":
     * [{"positionId":"1",
     * "name":"TEST",
     * "quantity":{"value":1.0,"measure":"psc"},
     * "itemAmount":500000,
     * "itemCode":"code1",
     * "itemPrice":"500000",
     * "itemAttributes":
     * {"attributes":
     * [{"name":"agent_info.paying.operation","value":"Test operation"},
     * {"name":"supplier_info.phones","value":"+79161234567"},
     * {"name":"agent_info.MTOperator.name","value":"Test MT Operator"},
     * {"name":"agent_info.paymentsOperator.phones","value":"+79161234567,"},
     * {"name":"nomenclature","value":"dGVzdCBkZXBvc2l0"},
     * {"name":"agent_info.MTOperator.address","value":"Moscow"},
     * {"name":"supplier_info.name","value":"Test Supplier"},
     * {"name":"paymentMethod","value":"1"},
     * {"name":"paymentObject","value":"3"},
     * {"name":"agent_info.MTOperator.phones","value":"+79161234567"},
     * {"name":"agent_info.MTOperator.inn","value":"9715225506"},
     * {"name":"supplier_info.inn","value":"9715225506"},
     * {"name":"agent_info.type","value":"7"},
     * {"name":"agent_info.paying.phones","value":"+79161234567"}]}}]},
     * "agent":
     * {"agentType":1,
     * "payingOperation":"Test agent operation",
     * "payingPhones":"+79161234567,",
     * "paymentsOperatorPhones":"+79161234567",
     * "mtOperatorPhones":"+79169876543",
     * "mtOperatorName":"Agent MT Operator",
     * "mtOperatorAddress":"New York",
     * "mtOperatorInn":"9715225506"}}]"
     * @return
     */
    public String getOrderBundle() {
        return orderBundle;
    }

    /**
     * Возможно использование следующих значений:
     * AUTO_PAYMENT - Если запрос на регистрацию заказа инициирует проведение автоплатежей.
     * VERIFY - Если указать это значение после запроса на регистрацию заказа произойдёт верификация держателя карты без списания средств с его счёта, поэтому в запросе можно передавать нулевую сумму.
     * @return
     */
    public String getFeatures() {
        return features;
    }

    /**
     * mdOrder заказа с типом расчета Предоплата или Аванс
     * @return
     */
    public String getPrepaymentMdOrder() {
        return prepaymentMdOrder;
    }

    /**
     * Адрес для callback вызовов (необходима дополнительная настройка мерчанта)
     * @return
     */
    public String getDynamicCallbackUrl() {
        return dynamicCallbackUrl;
    }

    public String getFeeInput() {
        return feeInput;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public void setFailUrl(String failUrl) {
        this.failUrl = failUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setPageView(String pageView) {
        this.pageView = pageView;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setMerchantLogin(String merchantLogin) {
        this.merchantLogin = merchantLogin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public void setJsonParams(String jsonParams) {
        this.jsonParams = jsonParams;
    }

    public void setAdditionalOfdParams(String additionalOfdParams) {
        this.additionalOfdParams = additionalOfdParams;
    }

    public void setSessionTimeoutSecs(Integer sessionTimeoutSecs) {
        this.sessionTimeoutSecs = sessionTimeoutSecs;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setAutocompletionDate(String autocompletionDate) {
        this.autocompletionDate = autocompletionDate;
    }

    public void setBindingId(String bindingId) {
        this.bindingId = bindingId;
    }

    public void setOrderBundle(String orderBundle) {
        this.orderBundle = orderBundle;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public void setPrepaymentMdOrder(String prepaymentMdOrder) {
        this.prepaymentMdOrder = prepaymentMdOrder;
    }

    public void setDynamicCallbackUrl(String dynamicCallbackUrl) {
        this.dynamicCallbackUrl = dynamicCallbackUrl;
    }

    public void setFeeInput(String feeInput) {
        this.feeInput = feeInput;
    }
}
