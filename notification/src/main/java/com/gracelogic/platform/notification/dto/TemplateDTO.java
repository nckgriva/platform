package com.gracelogic.platform.notification.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.notification.model.Template;
import com.gracelogic.platform.notification.model.TemplateType;

public class TemplateDTO extends IdObjectDTO {
    private TemplateTypeDTO templateTypeDTO;

    private String name;
    private String content;

    private class TemplateTypeDTO extends IdObjectDTO {
        private String name;
        private Integer sortOrder;

        public TemplateTypeDTO prepare(TemplateType model) {
            TemplateTypeDTO dto = new TemplateTypeDTO();
            IdObjectDTO.prepare(dto, model);
            dto.setName(model.getName());
            dto.setSortOrder(model.getSortOrder());
            return dto;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }
    }

    public TemplateDTO prepare(Template model) {
        TemplateDTO dto = new TemplateDTO();
        IdObjectDTO.prepare(dto, model);
        dto.setName(model.getName());
        dto.setContent(model.getContent());
        return dto;
    }

    public TemplateTypeDTO getTemplateTypeDTO() {
        return templateTypeDTO;
    }

    public void setTemplateTypeDTO(TemplateTypeDTO templateTypeDTO) {
        this.templateTypeDTO = templateTypeDTO;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
