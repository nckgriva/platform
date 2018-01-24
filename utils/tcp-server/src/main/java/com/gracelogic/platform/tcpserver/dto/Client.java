package com.gracelogic.platform.tcpserver.dto;

import java.util.UUID;

public class Client {
    private UUID id = UUID.randomUUID();

    private Object credentials = null;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Object getCredentials() {
        return credentials;
    }

    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }
}
