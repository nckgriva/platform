package com.gracelogic.platform.oauth.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.oauth.model.AuthProvider;

public class AuthProviderDTO extends IdObjectDTO {
    private String name;
    private String url;
    private Integer sortOrder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public static AuthProviderDTO prepare(AuthProvider model) {
        AuthProviderDTO dto = new AuthProviderDTO();
        IdObjectDTO.prepare(dto, model);

        dto.setName(model.getName());
        dto.setSortOrder(model.getSortOrder());

        return dto;
    }
}
