package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyAnswerVariantCatalog;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SurveyAnswerVariantCatalogDTO extends IdObjectDTO {
    private String name;

    private List<SurveyAnswerVariantCatalogItemDTO> items;
    private Set<UUID> itemsToDelete;

    public static SurveyAnswerVariantCatalogDTO prepare(SurveyAnswerVariantCatalog model) {
        SurveyAnswerVariantCatalogDTO dto = new SurveyAnswerVariantCatalogDTO();
        IdObjectDTO.prepare(dto, model);
        dto.setName(model.getName());
        return dto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SurveyAnswerVariantCatalogItemDTO> getItems() {
        return items;
    }

    public void setItems(List<SurveyAnswerVariantCatalogItemDTO> items) {
        this.items = items;
    }

    public Set<UUID> getItemsToDelete() {
        return itemsToDelete;
    }

    public void setItemsToDelete(Set<UUID> itemsToDelete) {
        this.itemsToDelete = itemsToDelete;
    }
}
