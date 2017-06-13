package com.gracelogic.platform.property.service;

import com.gracelogic.platform.property.dto.PropertyDTO;
import com.gracelogic.platform.property.model.Property;
import com.gracelogic.platform.db.dto.EntityListResponse;

import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 27.02.2015
 * Time: 14:09
 */
public interface PropertyService {
    String getPropertyValue(String propertyName);

    Integer getPropertyValueAsInteger(String propertyName);

    Long getPropertyValueAsLong(String propertyName);

    Boolean getPropertyValueAsBoolean(String propertyName);

    Double getPropertyValueAsDouble(String propertyName);

    UUID getPropertyValueAsUUID(String propertyName);

    Property saveProperty(PropertyDTO dto);

    PropertyDTO getProperty(UUID id);

    EntityListResponse<PropertyDTO> getPropertiesPaged(String name, boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir);
}
