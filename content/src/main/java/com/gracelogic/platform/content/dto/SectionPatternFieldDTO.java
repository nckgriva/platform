package com.gracelogic.platform.content.dto;

import com.gracelogic.platform.content.model.SectionPatternField;
import com.gracelogic.platform.db.dto.IdObjectDTO;

import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 18.12.2016
 * Time: 23:12
 */
public class SectionPatternFieldDTO extends IdObjectDTO {
    private String code;
    private String name;
    private Boolean nullable;
    private UUID sectionPatternId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public UUID getSectionPatternId() {
        return sectionPatternId;
    }

    public void setSectionPatternId(UUID sectionPatternId) {
        this.sectionPatternId = sectionPatternId;
    }

    public static SectionPatternFieldDTO prepare(SectionPatternField field) {
        SectionPatternFieldDTO dto = new SectionPatternFieldDTO();
        IdObjectDTO.prepare(dto, field);

        dto.setCode(field.getCode());
        dto.setName(field.getName());
        dto.setNullable(field.getNullable());
        if (field.getSectionPattern() != null) {
            dto.setSectionPatternId(field.getSectionPattern().getId());
        }

        return dto;
    }
}
