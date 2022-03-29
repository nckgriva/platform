package com.gracelogic.platform.payment.dto.alfabank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlfaBankOrderStatusRequestDTO {
    private String userName;
    private String password;
    private String orderId;
    private String token;
    private String language;

    public List<NameValuePair> getNameValuePairs() {
        List<NameValuePair> list = new LinkedList<>();
        if (userName != null) list.add(new BasicNameValuePair("userName", userName));
        if (password != null) list.add(new BasicNameValuePair("password", password));
        if (token != null) list.add(new BasicNameValuePair("token", token));
        if (orderId != null) list.add(new BasicNameValuePair("orderId", orderId));
        if (language != null) list.add(new BasicNameValuePair("language", language));
        return list;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
