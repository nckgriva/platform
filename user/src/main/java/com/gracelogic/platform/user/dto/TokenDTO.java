package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;

import java.io.Serializable;
import java.util.UUID;

public class TokenDTO extends IdObjectDTO implements Serializable {

    private UUID token;

    public TokenDTO() {
    }

    public TokenDTO(UUID token) {
        this.token = token;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }
}
