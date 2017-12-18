package com.gracelogic.platform.account.dto;

import com.gracelogic.platform.finance.FinanceUtils;

public class CalculateExchangeResponseDTO {
    private Long value; //in source currency

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Double getValueAsFractional() {
        if (this.value != null) {
            return FinanceUtils.toFractional2Rounded(this.value);
        }
        else {
            return null;
        }
    }

    public void setValueAsFractional(String sValue) {
        this.value = FinanceUtils.stringToLong(sValue);
    }

    public CalculateExchangeResponseDTO(Long value) {
        this.value = value;
    }
}
