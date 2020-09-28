package com.gracelogic.platform.dictionary.dto;

import com.gracelogic.platform.dictionary.model.Dictionary;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.localization.service.StringConverter;

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

    public String getNameLocalized() {
        return StringConverter.getInstance().process(name, LocaleHolder.getLocale());
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
