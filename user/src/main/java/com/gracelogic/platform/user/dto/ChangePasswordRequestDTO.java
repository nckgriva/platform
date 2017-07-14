package com.gracelogic.platform.user.dto;

public class ChangePasswordRequestDTO extends RepairCodeRequestDTO {
    private String newPassword;
    private String code;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
