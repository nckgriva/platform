package com.gracelogic.platform.survey.dto.admin;

import java.util.UUID;

public class ImportCatalogItemsDTO {
    private UUID catalogId;
    private String[] items;

    public UUID getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(UUID catalogId) {
        this.catalogId = catalogId;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }
}
