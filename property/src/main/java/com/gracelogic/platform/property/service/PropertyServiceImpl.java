package com.gracelogic.platform.property.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.property.dto.PropertyDTO;
import com.gracelogic.platform.property.model.Property;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PropertyServiceImpl implements PropertyService {
    @Autowired
    private IdObjectService idObjectService;

    private Map<String, String> cache = ExpiringMap.builder()
            .expiration(30, TimeUnit.SECONDS)
            .entryLoader(key -> getPropertyValueByName((String) key))
            .build();

    @Override
    public String getPropertyValue(String propertyName) {
        return cache.get(propertyName);
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
        entity.setVisible(dto.getVisible());

        return idObjectService.save(entity);
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

        EntityListResponse<PropertyDTO> entityListResponse = new EntityListResponse<PropertyDTO>(totalCount, count, page, start);

        List<Property> items = idObjectService.getList(Property.class, fetches, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);
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

    private String getPropertyValueByName(String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        List<Property> properties = idObjectService.getList(Property.class, null, "el.name=:name", params, null, null, 1);
        if (!properties.isEmpty()) {
            return properties.iterator().next().getValue();
        }
        return null;
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
