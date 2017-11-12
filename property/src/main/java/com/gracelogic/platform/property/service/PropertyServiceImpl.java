package com.gracelogic.platform.property.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.property.dto.PropertyDTO;
import com.gracelogic.platform.property.dto.PropertyModel;
import com.gracelogic.platform.property.model.Property;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                    propertyModel = reloadProperty(propertyName);
                }
                return propertyModel.getValue();
            }
        } else {
            PropertyModel propertyModel = reloadProperty(propertyName);
            return propertyModel != null ? propertyModel.getValue() : null;
        }
    }

    public PropertyModel reloadProperty(String propertyName) {
        PropertyModel propertyModel = null;

        Map<String, Object> params = new HashMap<>();
        params.put("propertyName", propertyName);
        List<Property> props = idObjectService.getList(Property.class, null, "el.name=:propertyName", params, null, null, null, 1);

        Property property = null;
        if (props != null && !props.isEmpty()) {
            property = props.iterator().next();
        }
        if (property != null) {
            propertyModel = PropertyModel.prepare(property);
            properties.put(property.getName(), propertyModel);
        }

        return propertyModel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Property saveProperty(PropertyDTO dto) throws ObjectNotFoundException {
        Property entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(Property.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new Property();
        }

        entity.setName(dto.getName());
        entity.setValue(dto.getValue());
        entity.setLifetime(dto.getLifetime());
        entity.setVisible(dto.getVisible());

        Property property = idObjectService.save(entity);
        reloadProperty(property.getName());
        return property;
    }

    @Override
    public EntityListResponse<PropertyDTO> getPropertiesPaged(String name, Boolean visible, boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = "";
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(name)) {
            params.put("name", "%%" + StringUtils.lowerCase(name) + "%%");
            cause += " and lower(el.name) like :name";
        }

        if (visible != null) {
            cause += " and el.visible = :visible";
            params.put("visible", visible);
        }

        int totalCount = idObjectService.getCount(Property.class, null, countFetches, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<PropertyDTO> entityListResponse = new EntityListResponse<PropertyDTO>();
        entityListResponse.setEntity("property");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<Property> items = idObjectService.getList(Property.class, fetches, cause, params, sortField, sortDir, startRecord, count);
        entityListResponse.setPartCount(items.size());
        for (Property e : items) {
            PropertyDTO el = PropertyDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Override
    public PropertyDTO getProperty(UUID id) throws ObjectNotFoundException {
        Property entity = idObjectService.getObjectById(Property.class, id);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }
        PropertyDTO dto = PropertyDTO.prepare(entity);
        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteProperty(UUID id) {
        idObjectService.delete(Property.class, id);
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

    @Override
    public UUID getPropertyValueAsUUID(String propertyName) {
        return UUID.fromString(getPropertyValue(propertyName));
    }

}
