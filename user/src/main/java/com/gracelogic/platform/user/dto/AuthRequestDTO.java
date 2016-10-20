package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.web.dto.PlatformRequest;

/**
 * Author: Igor Parkhomenko
 * Date: 14.05.14
 * Time: 15:48
 */
public class AuthRequestDTO extends PlatformRequest {
    private String login;
    private String password;
    private String loginType;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthRequestDTO(String login) {
        this.login = login;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public AuthRequestDTO() {
    }

    @Override
    public String toString() {
        return "AuthRequestDTO{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", loginType='" + loginType + '\'' +
                '}';
    }
}
