package com.gracelogic.platform.content.dto;

import com.gracelogic.platform.content.model.Element;
import com.gracelogic.platform.db.dto.IdObjectModel;
import com.gracelogic.platform.user.service.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElementDTO extends IdObjectModel {
    private String name;
    private Integer sortOrder;
    private UUID sectionId;
    private Boolean active;
    private Date startDt;
    private Date endDt;
    private Map<String, String> fields = new HashMap<>();

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

    public Date getStartDt() {
        return startDt;
    }

    public void setStartDt(Date startDt) {
        this.startDt = startDt;
    }

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

    public static ElementDTO prepare(Element element) {
        ElementDTO dto = new ElementDTO();
        IdObjectModel.prepare(dto, element);

        dto.setName(element.getName());
        dto.setSortOrder(element.getSortOrder());
        dto.setActive(element.getActive());
        dto.setStartDt(element.getStartDt());
        dto.setEndDt(element.getEndDt());

        if (element.getSection() != null) {
            dto.setSectionId(element.getSection().getId());
        }

        if (!StringUtils.isEmpty(element.getFields())) {
            dto.setFields(JsonUtils.jsonToMap(element.getFields()));
        }

        return dto;
    }
}
