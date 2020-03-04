package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.notification.dto.TemplateDTO;
import com.gracelogic.platform.notification.model.Template;
import com.gracelogic.platform.notification.model.TemplateType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TemplateServiceImpl implements TemplateService {
    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private DictionaryService ds;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Template saveTemplate(TemplateDTO dto) throws ObjectNotFoundException {
        Template template;
        if (dto.getId() != null) {
            template = idObjectService.getObjectById(Template.class, dto.getId());
            if (template == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            template = new Template();
        }

        template.setName(dto.getName());
        template.setContent(dto.getContent());
        template.setTemplateType(ds.get(TemplateType.class, dto.getTemplateTypeId()));

        return idObjectService.save(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(UUID id) {
        idObjectService.delete(Template.class, id);
    }

    @Override
    public EntityListResponse<TemplateDTO> getTemplatesPaged(String name, UUID templateTypeId, boolean enrich,
                                                             Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = "";
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (!StringUtils.isEmpty(name)) {
            params.put("name", "%%" + StringUtils.lowerCase(name) + "%%");
            cause += " and lower(el.name) like :name";
        }

        if (templateTypeId != null) {
            params.put("templateTypeId", templateTypeId);
            cause += " and el.templateType.id=:templateTypeId";
        }


        int totalCount = idObjectService.getCount(Template.class, null, countFetches, cause, params);

        EntityListResponse<TemplateDTO> entityListResponse = new EntityListResponse<>(totalCount, count, page, start);

        List<Template> items = idObjectService.getList(Template.class, fetches, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);
        for (Template e : items) {
            TemplateDTO el = TemplateDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }
}
