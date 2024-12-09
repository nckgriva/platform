package com.gracelogic.platform.notification.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.notification.Path;
import com.gracelogic.platform.notification.dto.TemplateDTO;
import com.gracelogic.platform.notification.model.Template;
import com.gracelogic.platform.notification.service.TemplateService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.IDResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_TEMPLATE)
public class TemplateApi extends AbstractAuthorizedController {
    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private TemplateService templateService;

    @PreAuthorize("hasAuthority('TEMPLATE:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getTemplates(@RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "templateTypeId", required = false) UUID templateTypeId,
                                       @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                       @RequestParam(value = "calculate", defaultValue = "false") Boolean calculate,
                                       @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                       @RequestParam(value = "page", required = false) Integer page,
                                       @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                       @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                       @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        EntityListResponse<TemplateDTO> templates = templateService.getTemplatesPaged(name, templateTypeId, enrich, calculate, count, page, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<TemplateDTO>>(templates, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('TEMPLATE:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getTemplate(@PathVariable(value = "id") UUID id) {
        try {
            TemplateDTO templateDTO = templateService.getTemplate(id);
            return new ResponseEntity<TemplateDTO >(templateDTO, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", messageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('TEMPLATE:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveTemplate(@RequestBody TemplateDTO templateDTO) {
        try {
            Template template = templateService.saveTemplate(templateDTO);
            return new ResponseEntity<IDResponse>(new IDResponse(template.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", messageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasAuthority('TEMPLATE:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteTemplate(@PathVariable(value = "id") UUID id) {
        try {
            templateService.deleteTemplate(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", messageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
