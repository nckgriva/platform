package com.gracelogic.platform.user.dto;

public class ChangePasswordViaVerificationCodeRequestDTO extends VerifyIdentifierRequestDTO {
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "ChangePasswordViaVerificationCodeRequestDTO{" +
                "newPassword=[not logged]" + '\'' +
                ", identifierTypeId=" + identifierTypeId +
                ", identifierValue='" + identifierValue + '\'' +
                '}';
    }
}
