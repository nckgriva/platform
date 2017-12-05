package com.gracelogic.platform.content.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gracelogic.platform.content.model.Section;
import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.localization.service.StringConverter;

import java.util.LinkedList;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SectionDTO extends IdObjectDTO {
    private String name;
    private String description;
    private Boolean active;
    private String path;
    private UUID parentId;
    private UUID sectionPatternId;
    private Integer sortOrder;

    private LinkedList<SectionDTO> children = new LinkedList<SectionDTO>();

    public String getName() {
        return name;
    }

    public String getNameLocalized() {return StringConverter.getInstance().process(name, LocaleHolder.getLocale());}

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionLocalized() {return StringConverter.getInstance().process(description, LocaleHolder.getLocale());}

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public UUID getSectionPatternId() {
        return sectionPatternId;
    }

    public void setSectionPatternId(UUID sectionPatternId) {
        this.sectionPatternId = sectionPatternId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LinkedList<SectionDTO> getChildren() {
        return children;
    }

    public void setChildren(LinkedList<SectionDTO> children) {
        this.children = children;
    }

    public void addChild(SectionDTO sectionDTO) {
        children.add(sectionDTO);
    }

    public void removeChild(UUID id) {
        SectionDTO sectionToRemove = null;
        for (SectionDTO sectionDTO : children) {
            if (sectionDTO.getId().equals(id)) {
                sectionToRemove = sectionDTO;
                break;
            }
        }

        if (sectionToRemove != null) {
            children.remove(sectionToRemove);
        }
    }

    public static SectionDTO prepare(Section section) {
        SectionDTO dto = new SectionDTO();
        IdObjectDTO.prepare(dto, section);

        dto.setName(section.getName());
        dto.setDescription(section.getDescription());
        dto.setActive(section.getActive());
        dto.setSortOrder(section.getSortOrder());
        dto.setPath(section.getPath());
        if (section.getParent() != null) {
            dto.setParentId(section.getParent().getId());
        }
        if (section.getSectionPattern() != null) {
            dto.setSectionPatternId(section.getSectionPattern().getId());
        }

        return dto;
    }
}

