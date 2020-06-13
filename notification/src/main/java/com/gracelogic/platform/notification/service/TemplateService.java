package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.notification.dto.Content;
import com.gracelogic.platform.notification.dto.TemplateDTO;
import com.gracelogic.platform.notification.model.Template;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public interface TemplateService {
    Template saveTemplate(TemplateDTO dto) throws ObjectNotFoundException;

    void deleteTemplate(UUID id);

    TemplateDTO getTemplate(UUID id) throws ObjectNotFoundException;

    EntityListResponse<TemplateDTO> getTemplatesPaged(String name, UUID templateTypeId, boolean enrich, boolean calculate,
                                                                 Integer count, Integer page, Integer start, String sortField, String sortDir);

    Content buildFromTemplate(UUID templateTypeId, Locale locale, Map<String, String> params);
}
