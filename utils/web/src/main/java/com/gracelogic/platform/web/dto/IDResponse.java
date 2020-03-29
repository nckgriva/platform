package com.gracelogic.platform.web.dto;

import java.util.UUID;

public class IDResponse extends PlatformResponse {
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
