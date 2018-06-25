package com.gracelogic.platform.content.service;

import com.gracelogic.platform.content.dto.ElementDTO;
import com.gracelogic.platform.content.dto.SectionDTO;
import com.gracelogic.platform.content.dto.SectionPatternDTO;
import com.gracelogic.platform.content.model.Element;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;

import java.util.*;

public interface ContentService {
    List<SectionDTO> getSectionsHierarchically(UUID parentId, boolean onlyActive);

    SectionDTO getSection(UUID id) throws ObjectNotFoundException;

    EntityListResponse<ElementDTO> getElementsPaged(String name, Collection<UUID> sectionIds, Boolean active, Date validOnDate, Map<String, String> fields, Integer count, Integer page, Integer start, String sortField, String sortDir);

    Element saveElement(ElementDTO dto) throws ObjectNotFoundException;

    void deleteElement(UUID id);

    SectionPatternDTO getSectionPattern(UUID sectionPatternId) throws ObjectNotFoundException;

    SectionPatternDTO getSectionPatternBySection(UUID sectionId) throws ObjectNotFoundException;

    ElementDTO getElement(UUID id, boolean includeSectionPattern) throws ObjectNotFoundException;
}
