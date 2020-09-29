package com.gracelogic.platform.dictionary.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.dictionary.model.Dictionary;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.localization.service.StringConverter;

import java.util.HashMap;
import java.util.Map;

public class DictionaryDTO {
    private Object id;
    private String name;
    private String code;
    private Integer sortOrder;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Map<String, String> getNameLocalized() { //todo вроде, работает
        Map<String, String> nameLocalized = null;
        if (name != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                nameLocalized = mapper.readValue(name, Map.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return nameLocalized;
    }

    public static DictionaryDTO prepare(Dictionary dictionary) {
        DictionaryDTO dto = new DictionaryDTO();
        dto.setId(dictionary.getId());
        dto.setName(dictionary.getName());
        dto.setCode(dictionary.getCode());
        dto.setSortOrder(dictionary.getSortOrder());

        return dto;
    }
}
