package com.gracelogic.platform.account.dto;

import com.gracelogic.platform.finance.FinanceUtils;

import java.util.UUID;

public class CalculateExchangeRequestDTO {
    private UUID sourceCurrencyId;
    private UUID destinationCurrencyId;
    private Long value; //in source currency

    public UUID getSourceCurrencyId() {
        return sourceCurrencyId;
    }

    public void setSourceCurrencyId(UUID sourceCurrencyId) {
        this.sourceCurrencyId = sourceCurrencyId;
    }

    public UUID getDestinationCurrencyId() {
        return destinationCurrencyId;
    }

    public void setDestinationCurrencyId(UUID destinationCurrencyId) {
        this.destinationCurrencyId = destinationCurrencyId;
    }

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

}
