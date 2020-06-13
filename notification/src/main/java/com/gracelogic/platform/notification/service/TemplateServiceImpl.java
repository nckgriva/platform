package com.gracelogic.platform.notification.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.notification.dto.Content;
import com.gracelogic.platform.notification.dto.TemplateDTO;
import com.gracelogic.platform.notification.model.Template;
import com.gracelogic.platform.notification.model.TemplateType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

@Service
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
        template.setTitle(dto.getTitle());
        template.setBody(dto.getBody());
        template.setLocale(dto.getLocale());
        template.setTemplateType(ds.get(TemplateType.class, dto.getTemplateTypeId()));

        return idObjectService.save(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(UUID id) {
        idObjectService.delete(Template.class, id);
    }

    @Override
    public TemplateDTO getTemplate(UUID id) throws ObjectNotFoundException {
        Template entity = idObjectService.getObjectById(Template.class, id);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }

        return TemplateDTO.prepare(entity, false);
    }

    @Override
    public EntityListResponse<TemplateDTO> getTemplatesPaged(String name, UUID templateTypeId, boolean enrich, boolean calculate,
                                                             Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = enrich ? " left join fetch el.templateType " : "";
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


        Integer totalCount = calculate ? idObjectService.getCount(Template.class, null, countFetches, cause, params) : null;

        EntityListResponse<TemplateDTO> entityListResponse = new EntityListResponse<>(totalCount, count, page, start);

        List<Template> items = idObjectService.getList(Template.class, fetches, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);
        for (Template e : items) {
            TemplateDTO el = TemplateDTO.prepare(e, enrich);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Override
    public Content buildFromTemplate(UUID templateTypeId, Locale locale, Map<String, String> params) {
        Map<String, Object> dbParams = new HashMap<>();
        dbParams.put("templateTypeId", templateTypeId);
        dbParams.put("locale", locale.toString());
        dbParams.put("defaultLocale", "*");

        List<Template> templates = idObjectService.getList(Template.class, null, "el.templateType.id=:templateTypeId and (el.locale=:locale or el.locale=:defaultLocale)", dbParams, "el.locale", "DESC", null, 1);
        String titleTemplate;
        String bodyTemplate;
        if (!templates.isEmpty()) {
            Template template = templates.iterator().next();
            titleTemplate = template.getTitle();
            bodyTemplate = template.getBody();
        }
        else {
            //Template not found - build raw params template
            titleTemplate = "Title";
            StringBuilder sb = new StringBuilder();
            for (String param : params.keySet()) {
                sb.append(param).append("=").append(params.get(param)).append("\n");
            }
            bodyTemplate = sb.toString();
        }


        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache bodyMustache = mf.compile(new StringReader(bodyTemplate), templateTypeId.toString() + locale.toString() + "body");
        Mustache titleMustache = mf.compile(new StringReader(titleTemplate), templateTypeId.toString() + locale.toString() + "title");

        Content content = new Content();
        content.setBody(bodyMustache.execute(new StringWriter(), params).toString());
        content.setTitle(titleMustache.execute(new StringWriter(), params).toString());

        return content;
    }
}
