package com.gracelogic.platform.web.dto;

import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 16.07.2016
 * Time: 22:38
 */
public class IDResponse extends PlatformRequest {
    private UUID id;

    public UUID getId() {
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

    public IDResponse(UUID id) {
        this.id = id;
    }
}
