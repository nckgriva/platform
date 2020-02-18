package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.user.model.Identifier;

import java.io.Serializable;
import java.util.UUID;

public class IdentifierDTO extends IdObjectDTO implements Serializable {
    private String value;
    private Boolean verified;
    private Boolean primary;
    private UUID identifierTypeId;
    private UUID userId;
    private String identifierTypeName;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public UUID getIdentifierTypeId() {
        return identifierTypeId;
    }

    public void setIdentifierTypeId(UUID identifierTypeId) {
        this.identifierTypeId = identifierTypeId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getIdentifierTypeName() {
        return identifierTypeName;
    }

    public void setIdentifierTypeName(String identifierTypeName) {
        this.identifierTypeName = identifierTypeName;
    }

    public static IdentifierDTO prepare(Identifier model, boolean enrich) {
        IdentifierDTO dto = new IdentifierDTO();
        IdObjectDTO.prepare(dto, model);
        dto.setValue(model.getValue());
        dto.setPrimary(model.getPrimary());
        dto.setVerified(model.getVerified());
        if (model.getIdentifierType() != null) {
            dto.setIdentifierTypeId(model.getIdentifierType().getId());
        }
        if (enrich) {
            dto.setIdentifierTypeName(model.getIdentifierType().getName());
        }
        if (model.getUser() != null) {
            dto.setUserId(model.getUser().getId());
        }
        return dto;
    }
}
