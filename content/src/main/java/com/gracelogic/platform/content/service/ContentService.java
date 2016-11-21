package com.gracelogic.platform.content.service;

import com.gracelogic.platform.content.dto.ElementDTO;
import com.gracelogic.platform.content.dto.SectionDTO;
import com.gracelogic.platform.db.dto.EntityListResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ContentService {
    List<SectionDTO> getSectionsHierarchically(UUID parentId, boolean onlyActive);

    EntityListResponse<ElementDTO> getElementsPaged(UUID sectionId, Boolean active, Date validOnDate, Map<String, String> fields, Integer count, Integer page, Integer start, String sortField, String sortDir);
}
