package com.gracelogic.platform.suggestion.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.db.model.IdObject;

import java.util.Map;

public class SuggestedVariant extends IdObjectDTO {
    private String name;
    private String description;
    private Map<String, String> tags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public static SuggestedVariant prepare(IdObject entity, String name, String description) {
        SuggestedVariant variant = new SuggestedVariant();
        IdObjectDTO.prepare(variant, entity);

        variant.setName(name);
        variant.setDescription(description);

        return variant;
    }
}
