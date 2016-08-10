package com.gracelogic.platform.property.service;

import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.property.dto.PropertyModel;
import com.gracelogic.platform.property.model.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Igor Parkhomenko
 * Date: 27.02.2015
 * Time: 14:09
 */
@Service
public class PropertyServiceImpl implements PropertyService {
    @Autowired
    private IdObjectService idObjectService;

    private Map<String, PropertyModel> properties = new HashMap<String, PropertyModel>();

    @PostConstruct
    public void init() {
        List<Property> propertyList = idObjectService.getList(Property.class);
        for (Property property : propertyList) {
            properties.put(property.getName(), PropertyModel.prepare(property));
        }
    }

    @Override
    public String getPropertyValue(String propertyName) {
        if (properties.containsKey(propertyName)) {
            PropertyModel propertyModel = properties.get(propertyName);
            if (propertyModel.getLifetime() == null) {
                return propertyModel.getValue();
            }
            else {
                if (propertyModel.getBuildTime() == null || System.currentTimeMillis() - propertyModel.getBuildTime() > propertyModel.getLifetime()) {
                    List<Property> props = idObjectService.getList(Property.class, "name", propertyName);
                    Property property = null;
                    if (props != null && !props.isEmpty()) {
                        property = props.iterator().next();
                    }
                    if (property != null) {
                        propertyModel = PropertyModel.prepare(property);
                        properties.put(property.getName(), propertyModel);
                    }
                }
                return propertyModel.getValue();
            }
        } else {
            List<Property> props = idObjectService.getList(Property.class, "name", propertyName);
            Property property = null;
            PropertyModel propertyModel = null;
            if (props != null && !props.isEmpty()) {
                property = props.iterator().next();
            }
            if (property != null) {
                propertyModel = PropertyModel.prepare(property);
                properties.put(property.getName(), propertyModel);
            }

            return propertyModel != null ? propertyModel.getValue() : null;
        }
    }

    @Override
    public Integer getPropertyValueAsInteger(String propertyName) {
        return Integer.parseInt(getPropertyValue(propertyName));
    }

    @Override
    public Long getPropertyValueAsLong(String propertyName) {
        return Long.parseLong(getPropertyValue(propertyName));
    }

    @Override
    public Boolean getPropertyValueAsBoolean(String propertyName) {
        return Boolean.parseBoolean(getPropertyValue(propertyName));
    }

    @Override
    public Double getPropertyValueAsDouble(String propertyName) {
        return Double.parseDouble(getPropertyValue(propertyName));
    }

}
