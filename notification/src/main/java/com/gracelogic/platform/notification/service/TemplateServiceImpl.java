package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.notification.dto.TemplateDTO;
import com.gracelogic.platform.notification.model.Template;
import com.gracelogic.platform.notification.model.TemplateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class TemplateServiceImpl implements TemplateService {
    @Autowired
    private IdObjectService idObjectService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Template saveTemplate(TemplateDTO dto) throws ObjectNotFoundException {
        Template template;
        if (dto.getId() != null) {
            template = idObjectService.getObjectById(Template.class, dto.getId());
            if (template == null) {
                throw new ObjectNotFoundException();
            }
        }
        else {
            template = new Template();
        }

        template.setName(dto.getName());
        template.setContent(dto.getContent());
        template.setTemplateType(idObjectService.getObjectById(TemplateType.class, dto.getTemplateTypeId()));

        template = idObjectService.save(template);

        return template;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(UUID id) {
        idObjectService.delete(Template.class, id);
    }
}
