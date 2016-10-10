package com.gracelogic.platform.web.dto;

/**
 * Author: Igor Parkhomenko
 * Date: 25.07.13
 * Time: 13:55
 */
public class SelectModel {
    private String id;
    private String name;

    public SelectModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
