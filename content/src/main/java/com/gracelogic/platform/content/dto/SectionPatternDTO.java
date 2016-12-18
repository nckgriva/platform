package com.gracelogic.platform.content.dto;

import com.gracelogic.platform.content.model.SectionPattern;
import com.gracelogic.platform.db.dto.IdObjectModel;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: Igor Parkhomenko
 * Date: 18.12.2016
 * Time: 23:11
 */
public class SectionPatternDTO extends IdObjectModel {
    private String name;
    private String description;
    private List<SectionPatternFieldDTO> fields = new LinkedList<>();

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

    public List<SectionPatternFieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<SectionPatternFieldDTO> fields) {
        this.fields = fields;
    }

    public static SectionPatternDTO prepare(SectionPattern pattern) {
        SectionPatternDTO dto = new SectionPatternDTO();
        IdObjectModel.prepare(dto, pattern);

        dto.setName(pattern.getName());
        dto.setDescription(pattern.getDescription());

        return dto;
    }
}
