package com.gracelogic.platform.suggestion.dto;

import com.gracelogic.platform.db.dto.IdObjectModel;
import com.gracelogic.platform.db.model.IdObject;

public class SuggestedVariant extends IdObjectModel {
    private String name;
    private String description;

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

    public static SuggestedVariant prepare(IdObject entity, String name, String description) {
        SuggestedVariant variant = new SuggestedVariant();
        IdObjectModel.prepare(variant, entity);

        variant.setName(name);
        variant.setDescription(description);

        return variant;
    }
}
