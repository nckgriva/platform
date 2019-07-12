package com.gracelogic.platform.oauth.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.oauth.model.AuthProvider;

public class AuthProviderDTO extends IdObjectDTO {
    private String name;
    private String fullName;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static AuthProviderDTO prepare(AuthProvider model) {
        AuthProviderDTO dto = new AuthProviderDTO();
        IdObjectDTO.prepare(dto, model);

        dto.setName(model.getName());
        dto.setFullName(model.getFullName());

        return dto;
    }
}
