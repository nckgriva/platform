package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.web.dto.PlatformRequest;

/**
 * Author: Igor Parkhomenko
 * Date: 30.12.2015
 * Time: 0:18
 */
public class RepairCodeRequest extends PlatformRequest {
    private String login;
    private String loginType = "phone";

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
