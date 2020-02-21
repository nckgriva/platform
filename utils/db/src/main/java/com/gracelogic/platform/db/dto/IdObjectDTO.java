package com.gracelogic.platform.db.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gracelogic.platform.db.model.IdObject;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

//@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
public class IdObjectDTO implements Serializable {
    private UUID id;
    private Date created;
    private Date changed;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.ALWAYS)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.ALWAYS)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public Date getChanged() {
        return changed;
    }

    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public static IdObjectDTO prepare(IdObjectDTO dto, IdObject<UUID> idObject) {
        dto.setId(idObject.getId());
        dto.setCreated(idObject.getCreated());
        dto.setChanged(idObject.getChanged());

        return dto;
    }
}
