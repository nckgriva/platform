package com.gracelogic.platform.content.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gracelogic.platform.content.model.Element;
import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.db.dto.JsonDateDeserializer;
import com.gracelogic.platform.db.dto.JsonDateSerializer;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.localization.service.StringConverter;
import com.gracelogic.platform.user.service.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElementDTO extends IdObjectDTO {
    private String name;
    private Integer sortOrder;
    private UUID sectionId;
    private Boolean active;
    private Date startDt;
    private Date endDt;
    private Date elementDt;
    private Map<String, String> fields = new HashMap<>();

    private SectionPatternDTO sectionPattern;

    public String getName() {
        return name;
    }

    public String getNameLocalized() {return StringConverter.getInstance().process(name, LocaleHolder.getLocale());}

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public UUID getSectionId() {
        return sectionId;
    }

    public void setSectionId(UUID sectionId) {
        this.sectionId = sectionId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public Date getStartDt() {
        return startDt;
    }

    public void setStartDt(Date startDt) {
        this.startDt = startDt;
    }

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public Date getEndDt() {
        return endDt;
    }

    public void setEndDt(Date endDt) {
        this.endDt = endDt;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public Date getElementDt() {
        return elementDt;
    }

    public void setElementDt(Date elementDt) {
        this.elementDt = elementDt;
    }

    public SectionPatternDTO getSectionPattern() {
        return sectionPattern;
    }

    public void setSectionPattern(SectionPatternDTO sectionPattern) {
        this.sectionPattern = sectionPattern;
    }

    public Map<String, String> getFieldsLocalized() {
        Map<String, String> fieldsLocalized = null;
        if (fields != null) {
            fieldsLocalized = new HashMap<>();
            for (String key : fields.keySet()) {
                fieldsLocalized.put(key, StringConverter.getInstance().process(fields.get(key), LocaleHolder.getLocale()));
            }
        }

        return fieldsLocalized;
    }

    public static ElementDTO prepare(Element element) {
        ElementDTO dto = new ElementDTO();
        IdObjectDTO.prepare(dto, element);

        dto.setName(element.getName());
        dto.setSortOrder(element.getSortOrder());
        dto.setActive(element.getActive());
        dto.setStartDt(element.getStartDt());
        dto.setEndDt(element.getEndDt());
        dto.setElementDt(element.getElementDt());

        if (element.getSection() != null) {
            dto.setSectionId(element.getSection().getId());
        }

        if (!StringUtils.isEmpty(element.getFields())) {
            dto.setFields(JsonUtils.jsonToMap(element.getFields()));
        }

        return dto;
    }
}
