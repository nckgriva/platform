package com.gracelogic.platform.db.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gracelogic.platform.db.model.IdObject;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 18.12.14
 * Time: 12:33
 */
//@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
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

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public Date getChanged() {
        return changed;
    }

    public void setChanged(Date changed) {
        this.changed = changed;
    }

    @Deprecated
    public String idAsString() {
        if (id == null) {
            return null;
        }
        return id.toString();
    }

    @Deprecated
    public String getIdAsString() {
        return idAsString();
    }

    @Deprecated
    public void setIdAsString(String value) {
        if (!StringUtils.isEmpty(value)) {
            id = UUID.fromString(value);
        }
    }

    public static IdObjectDTO prepare(IdObjectDTO dto, IdObject<UUID> idObject) {
        dto.setId(idObject.getId());
        dto.setCreated(idObject.getCreated());
        dto.setChanged(idObject.getChanged());

        return dto;
    }
}
