package com.gracelogic.platform.property.dto;

import com.gracelogic.platform.property.model.Property;

/**
 * Author: Igor Parkhomenko
 * Date: 27.08.2015
 * Time: 16:26
 */
public class PropertyModel {
    private String value;
    private Long lifetime;
    private Long buildTime;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getLifetime() {
        return lifetime;
    }

    public void setLifetime(Long lifetime) {
        this.lifetime = lifetime;
    }

    public Long getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(Long buildTime) {
        this.buildTime = buildTime;
    }

    public static PropertyModel prepare(Property property) {
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setValue(property.getValue());
        propertyModel.setLifetime(property.getLifetime());
        propertyModel.setBuildTime(System.currentTimeMillis());
        return propertyModel;
    }

    @Override
    public String toString() {
        return "PropertyModel{" +
                "value='" + value + '\'' +
                ", lifetime=" + lifetime +
                ", buildTime=" + buildTime +
                '}';
    }
}
