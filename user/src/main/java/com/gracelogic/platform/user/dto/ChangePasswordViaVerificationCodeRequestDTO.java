package com.gracelogic.platform.user.dto;

public class ChangePasswordViaVerificationCodeRequestDTO extends SendVerificationCodeForPasswordChangingRequestDTO {
    private String newPassword;
    private String verificationCode;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
