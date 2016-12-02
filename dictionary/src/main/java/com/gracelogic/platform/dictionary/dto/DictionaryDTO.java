package com.gracelogic.platform.dictionary.dto;

import com.gracelogic.platform.dictionary.model.Dictionary;

import java.util.UUID;

public class DictionaryDTO {
    private UUID id;
    private String name;

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

    public static DictionaryDTO prepare(Dictionary dictionary) {
        DictionaryDTO dto = new DictionaryDTO();
        dto.setId(dictionary.getId());
        dto.setName(dictionary.getName());

        return dto;
    }
}
