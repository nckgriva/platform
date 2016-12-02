package com.gracelogic.platform.dictionary.dto;

import com.gracelogic.platform.dictionary.model.Dictionary;

import java.util.UUID;

public class DictionaryDTO {
    private UUID id;
    private String name;
    private String code;
    private Integer sortOrder;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public static DictionaryDTO prepare(Dictionary dictionary) {
        DictionaryDTO dto = new DictionaryDTO();
        dto.setId(dictionary.getId());
        dto.setName(dictionary.getName());
        dto.setCode(dictionary.getCode());
        dto.setSortOrder(dictionary.getSortOrder());

        return dto;
    }
}
