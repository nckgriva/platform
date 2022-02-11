package com.gracelogic.platform.payment.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.payment.model.PaymentSystem;
import com.gracelogic.platform.db.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class PaymentSystemDTO extends IdObjectDTO {
    private String name;
    private String description;
    private String allowedAddresses;
    private String paymentExecutorClass;
    private Boolean active;
    private Long fee;
    private Boolean feeIncluded;

    private Map<String, String> fields = new HashMap<>();

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

    public String getAllowedAddresses() {
        return allowedAddresses;
    }

    public void setAllowedAddresses(String allowedAddresses) {
        this.allowedAddresses = allowedAddresses;
    }

    public String getPaymentExecutorClass() {
        return paymentExecutorClass;
    }

    public void setPaymentExecutorClass(String paymentExecutorClass) {
        this.paymentExecutorClass = paymentExecutorClass;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Boolean getFeeIncluded() {
        return feeIncluded;
    }

    public void setFeeIncluded(Boolean feeIncluded) {
        this.feeIncluded = feeIncluded;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public static PaymentSystemDTO prepare(PaymentSystem model) {
        PaymentSystemDTO dto = new PaymentSystemDTO();
        IdObjectDTO.prepare(dto, model);

        dto.setName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setActive(model.getActive());
        dto.setAllowedAddresses(model.getAllowedAddresses());
        dto.setFee(model.getFee());
        dto.setFeeIncluded(model.getFeeIncluded());
        dto.setPaymentExecutorClass(model.getPaymentExecutorClass());

        if (!StringUtils.isEmpty(model.getFields())) {
            dto.setFields(JsonUtils.jsonToMap(model.getFields()));
        }

        return dto;
    }
}
