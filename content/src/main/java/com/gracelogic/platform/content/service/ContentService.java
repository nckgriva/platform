package com.gracelogic.platform.content.service;

import com.gracelogic.platform.content.dto.ElementDTO;
import com.gracelogic.platform.content.dto.SectionDTO;
import com.gracelogic.platform.db.dto.EntityListResponse;

import java.util.*;

public interface ContentService {
    List<SectionDTO> getSectionsHierarchically(UUID parentId, boolean onlyActive);

    EntityListResponse<ElementDTO> getElementsPaged(Collection<UUID> sectionIds, Boolean active, Date validOnDate, Map<String, String> fields, Integer count, Integer page, Integer start, String sortField, String sortDir);
}
