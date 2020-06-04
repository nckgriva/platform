package com.gracelogic.platform.notification.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.notification.model.Template;

import java.util.UUID;

public class TemplateDTO extends IdObjectDTO {
    private String name;
    private String title;
    private String body;
    private UUID templateTypeId;
    private String templateTypeName;
    private String locale;

    public UUID getTemplateTypeId() {
        return templateTypeId;
    }

    public void setTemplateTypeId(UUID templateTypeId) {
        this.templateTypeId = templateTypeId;
    }

    public String getTemplateTypeName() {
        return templateTypeName;
    }

    public void setTemplateTypeName(String templateTypeName) {
        this.templateTypeName = templateTypeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public static TemplateDTO prepare(Template model, boolean enrich) {
        TemplateDTO dto = new TemplateDTO();
        IdObjectDTO.prepare(dto, model);
        dto.setName(model.getName());
        dto.setTitle(model.getTitle());
        dto.setBody(model.getBody());
        dto.setLocale(model.getLocale());
        if (model.getTemplateType() != null) {
            dto.setTemplateTypeId(model.getTemplateType().getId());
            if (enrich) {
                dto.setTemplateTypeName(model.getTemplateType().getName());
            }
        }
        return dto;
    }
}
