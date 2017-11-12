package com.gracelogic.platform.property.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.property.dto.PropertyDTO;
import com.gracelogic.platform.property.model.Property;

import java.util.UUID;

public interface PropertyService {
    String getPropertyValue(String propertyName);

    Integer getPropertyValueAsInteger(String propertyName);

    Long getPropertyValueAsLong(String propertyName);

    Boolean getPropertyValueAsBoolean(String propertyName);

    Double getPropertyValueAsDouble(String propertyName);

    UUID getPropertyValueAsUUID(String propertyName);

    Property saveProperty(PropertyDTO dto) throws ObjectNotFoundException;

    PropertyDTO getProperty(UUID id) throws ObjectNotFoundException;

    void deleteProperty(UUID id);

    EntityListResponse<PropertyDTO> getPropertiesPaged(String name, Boolean visible, boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir);
}
