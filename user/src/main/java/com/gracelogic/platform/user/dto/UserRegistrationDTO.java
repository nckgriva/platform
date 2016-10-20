package com.gracelogic.platform.user.dto;

public class UserRegistrationDTO extends UserDTO {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
