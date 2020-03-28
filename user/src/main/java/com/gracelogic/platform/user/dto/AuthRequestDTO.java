package com.gracelogic.platform.user.dto;

public class AuthRequestDTO extends IdentifierRequestDTO {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AuthRequestDTO{" +
                "identifierValue='" + identifierValue + '\'' +
                ", identifierTypeId=" + identifierTypeId +
                ", password='" + password + '\'' +
                '}';
    }
}
