package com.gracelogic.platform.web.dto;

import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 16.07.2016
 * Time: 22:38
 */
public class IDResponse extends PlatformRequest {
    private Object id;

    public Object getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "IDResponse{" +
                "id=" + id +
                '}';
    }

    public IDResponse(Object id) {
        this.id = id;
    }
}
