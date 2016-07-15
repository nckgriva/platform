package com.gracelogic.platform.dao.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gracelogic.platform.dao.model.IdObject;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 18.12.14
 * Time: 12:33
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class IdObjectModel implements Serializable {
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

    public String idAsString() {
        if (id == null) {
            return null;
        }
        return id.toString();
    }

    public String getIdAsString() {
        return idAsString();
    }

    public void setIdAsString(String value) {
        if (!StringUtils.isEmpty(value)) {
            id = UUID.fromString(value);
        }
    }

    public static IdObjectModel prepare(IdObjectModel model, IdObject<UUID> idObject) {
        model.setId(idObject.getId());
        model.setCreated(idObject.getCreated());
        model.setChanged(idObject.getChanged());

        return model;
    }
}
