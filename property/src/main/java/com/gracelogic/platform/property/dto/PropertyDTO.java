package com.gracelogic.platform.property.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.property.model.Property;

import java.util.Date;
import java.util.UUID;

public class PropertyDTO extends IdObjectDTO {

    private UUID id;

    private Date created;

    private Date changed;

    private String name;

    private String value;

    private Boolean visible;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public Date getChanged() {
        return changed;
    }

    @Override
    public void setChanged(Date changed) {
        this.changed = changed;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getVisible() {return visible;}

    public void setVisible(Boolean visible) {this.visible = visible;}

    public static PropertyDTO prepare(Property property) {
        PropertyDTO propertyDTO = new PropertyDTO();
        IdObjectDTO.prepare(propertyDTO, property);

        propertyDTO.setName(property.getName());
        propertyDTO.setValue(property.getValue());
        propertyDTO.setVisible(property.getVisible());

        return propertyDTO;
    }
}
