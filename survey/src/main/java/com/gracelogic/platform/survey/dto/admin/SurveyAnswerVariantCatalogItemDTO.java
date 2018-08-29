package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyAnswerVariantCatalogItem;

import java.util.UUID;

public class SurveyAnswerVariantCatalogItemDTO extends IdObjectDTO {
    private UUID catalogId;
    private String text;

    public static SurveyAnswerVariantCatalogItemDTO prepare(SurveyAnswerVariantCatalogItem model) {
        SurveyAnswerVariantCatalogItemDTO dto = new SurveyAnswerVariantCatalogItemDTO();
        IdObjectDTO.prepare(dto, model);
        dto.setText(model.getText());
        dto.setCatalogId(model.getCatalog().getId());
        return dto;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public UUID getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(UUID catalogId) {
        this.catalogId = catalogId;
    }
}
