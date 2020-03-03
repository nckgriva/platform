package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.notification.dto.TemplateDTO;
import com.gracelogic.platform.notification.model.Template;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface TemplateService {
    @Transactional(rollbackFor = Exception.class)
    Template saveTemplate(TemplateDTO dto) throws ObjectNotFoundException;

    @Transactional(rollbackFor = Exception.class)
    void deleteTemplate(UUID id);

    public EntityListResponse<TemplateDTO> getTemplatesPaged(String name, String templateTypeId, boolean enrich,
                                                                 Integer count, Integer page, Integer start, String sortField, String sortDir);
}
