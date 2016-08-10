package com.gracelogic.platform.property.service;

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
}
