package com.gracelogic.platform.user.dto;


public class VerifyIdentifierRequestDTO extends IdentifierRequestDTO {
    private String verificationCode;

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    @Override
    public String toString() {
        return "VerifyIdentifierRequestDTO{" +
                "verificationCode='" + verificationCode + '\'' +
                ", identifierTypeId=" + identifierTypeId +
                ", identifierValue='" + identifierValue + '\'' +
                '}';
    }
}
