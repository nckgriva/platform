package com.gracelogic.platform.survey.dto.admin;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.survey.model.SurveyAnswerVariantCatalog;

public class SurveyAnswerVariantCatalogDTO extends IdObjectDTO {
    private String name;

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
}
