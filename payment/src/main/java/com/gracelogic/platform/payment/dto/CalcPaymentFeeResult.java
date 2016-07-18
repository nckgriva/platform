package com.gracelogic.platform.payment.dto;

/**
 * Author: Igor Parkhomenko
 * Date: 06.04.2015
 * Time: 14:41
 */
public class CalcPaymentFeeResult {
    private Double amount;
    private Double totalAmount;
    private Double fee;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return "CalcPaymentFeeResponse{" +
                "amount=" + amount +
                ", totalAmount=" + totalAmount +
                ", fee=" + fee +
                '}';
    }
}
