package com.gracelogic.platform.user.dto;

/**
 * Author: Igor Parkhomenko
 * Date: 14.05.14
 * Time: 15:48
 */
public class AuthRequest {
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

    public AuthRequest(String login) {
        this.login = login;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public AuthRequest() {
    }

    @Override
    public String toString() {
        return "AuthRequest{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", loginType='" + loginType + '\'' +
                '}';
    }
}
