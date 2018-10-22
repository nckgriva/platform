package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyAnswerVariantCatalog;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SurveyAnswerVariantCatalogDTO extends IdObjectDTO {
    private String name;
    private Boolean external;
    private String suggestionProcessorName;

    private List<SurveyAnswerVariantCatalogItemDTO> items;
    private Set<UUID> itemsToDelete;

    public static SurveyAnswerVariantCatalogDTO prepare(SurveyAnswerVariantCatalog model) {
        SurveyAnswerVariantCatalogDTO dto = new SurveyAnswerVariantCatalogDTO();
        IdObjectDTO.prepare(dto, model);
        dto.setName(model.getName());
        dto.setExternal(model.isExternal());
        dto.setSuggestionProcessorName(model.getSuggestionProcessorName());
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

    private void setItemsToDelete(Set<UUID> itemsToDelete) {
        this.itemsToDelete = itemsToDelete;
    }

    public Boolean isExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    public String getSuggestionProcessorName() {
        return suggestionProcessorName;
    }

    public void setSuggestionProcessorName(String suggestionProcessorName) {
        this.suggestionProcessorName = suggestionProcessorName;
    }
}
