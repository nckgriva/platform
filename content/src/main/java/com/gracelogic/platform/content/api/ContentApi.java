package com.gracelogic.platform.content.api;

import com.gracelogic.platform.content.Path;
import com.gracelogic.platform.content.dto.ElementDTO;
import com.gracelogic.platform.content.dto.SectionDTO;
import com.gracelogic.platform.content.dto.SectionPatternDTO;
import com.gracelogic.platform.content.model.Element;
import com.gracelogic.platform.content.service.ContentService;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.IDResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@RestController
@RequestMapping(value = Path.API_CONTENT)
public class ContentApi extends AbstractAuthorizedController {
    @Autowired
    private ContentService contentService;

    @Autowired
    @Qualifier("contentMessageSource")
    private ResourceBundleMessageSource messageSource;

    @RequestMapping(value = "/section", method = RequestMethod.GET)
    public ResponseEntity<List<SectionDTO>> getSections(
            @RequestParam(value = "parentId", required = false) UUID parentId,
            @RequestParam(value = "onlyActive", required = false, defaultValue = "true") Boolean onlyActive) {
        List<SectionDTO> sectionDTOs = contentService.getSectionsHierarchically(parentId, onlyActive);

        return ResponseEntity.ok(sectionDTOs);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/section/{id}")
    public ResponseEntity getSection(@PathVariable(value = "id") UUID id) {
        try {
            SectionDTO sectionDTO = contentService.getSection(id);
            return ResponseEntity.ok(sectionDTO);
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.ELEMENT_NOT_FOUND", messageSource.getMessage("content.ELEMENT_NOT_FOUND", null, LocaleHolder.getLocale())));
        }
    }

    @RequestMapping(value = "/element", method = RequestMethod.GET)
    public ResponseEntity<EntityListResponse<ElementDTO>> getElements(@RequestParam(value = "query", required = false) String query,
                                                                      @RequestParam(value = "queryFields", required = false) String sQueryFields,
                                                                      @RequestParam(value = "sectionIds", required = false) String sSectionIds,
                                                                      @RequestParam(value = "active", required = false) Boolean active,
                                                                      @RequestParam(value = "validOnDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date validOnDate,
                                                                      @RequestParam(value = "calculate", defaultValue = "false") Boolean calculate,
                                                                      @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                                                      @RequestParam(value = "page", required = false) Integer page,
                                                                      @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
                                                                      @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                                                      @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir,
                                                                      @RequestParam Map<String, String> allRequestParams) {

        Map<String, String> fields = new HashMap<>();
        if (allRequestParams != null) {
            for (String paramName : allRequestParams.keySet()) {
                if (StringUtils.startsWithIgnoreCase(paramName, "fields.")) {
                    String value = null;
                    try {
                        value = URLDecoder.decode(allRequestParams.get(paramName), "UTF-8");
                    } catch (UnsupportedEncodingException ignored) {
                    }

                    if (!StringUtils.isEmpty(value)) {
                        fields.put(paramName.substring("fields.".length()), value);
                    }
                }
            }
        }

        List<String> sectionIds = new LinkedList<>();
        if (!StringUtils.isEmpty(sSectionIds)) {
            sectionIds.addAll(Arrays.asList(sSectionIds.split(",")));
        }

        List<String> queryFields = Collections.emptyList();
        if (!StringUtils.isEmpty(sQueryFields)) {
            queryFields = Arrays.asList(sQueryFields.split(","));
        }

        EntityListResponse<ElementDTO> elements = contentService.getElementsPaged(query, queryFields, sectionIds, active, validOnDate,
                fields, calculate, length, page, start, sortField, sortDir);

        return ResponseEntity.ok(elements);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/element/{id}")
    public ResponseEntity getElement(@PathVariable(value = "id") UUID id,
                                     @RequestParam(value = "includeSectionPattern", required = false, defaultValue = "false") Boolean includeSectionPattern) {
        try {
            ElementDTO elementDTO = contentService.getElement(id, includeSectionPattern);
            return ResponseEntity.ok(elementDTO);
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.ELEMENT_NOT_FOUND", messageSource.getMessage("content.ELEMENT_NOT_FOUND", null, LocaleHolder.getLocale())));
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/element/by-external-id/{externalId}")
    public ResponseEntity getElementByExternalId(@PathVariable(value = "externalId") String externalId,
                                                 @RequestParam(value = "includeSectionPattern", required = false, defaultValue = "false") Boolean includeSectionPattern) {
        try {
            ElementDTO elementDTO = contentService.getElementByExternalId(externalId, includeSectionPattern);
            return ResponseEntity.ok(elementDTO);
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.ELEMENT_NOT_FOUND", messageSource.getMessage("content.ELEMENT_NOT_FOUND", null, LocaleHolder.getLocale())));
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/pattern/{id}")
    public ResponseEntity getSectionPattern(@PathVariable(value = "id") UUID id) {
        try {
            SectionPatternDTO sectionPatternDTO = contentService.getSectionPattern(id);
            return ResponseEntity.ok(sectionPatternDTO);
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.PATTERN_NOT_FOUND", messageSource.getMessage("content.PATTERN_NOT_FOUND", null, LocaleHolder.getLocale())));
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/section/{id}/pattern")
    public ResponseEntity getSectionPatternBySection(@PathVariable(value = "id") UUID id) {
        try {
            SectionPatternDTO sectionPatternDTO = contentService.getSectionPatternBySection(id);
            return ResponseEntity.ok(sectionPatternDTO);
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.PATTERN_NOT_FOUND", messageSource.getMessage("content.PATTERN_NOT_FOUND", null, LocaleHolder.getLocale())));
        }
    }

    @PreAuthorize("hasAuthority('ELEMENT:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/element/save")
    public ResponseEntity saveElement(@RequestBody ElementDTO elementDTO) {
        try {
            Element element = contentService.saveElement(elementDTO);
            return ResponseEntity.ok(new IDResponse(element.getId()));
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.ELEMENT_NOT_FOUND", messageSource.getMessage("content.ELEMENT_NOT_FOUND", null, LocaleHolder.getLocale())));
        }
    }

    @PreAuthorize("hasAuthority('ELEMENT:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/element/{id}/delete")
    public ResponseEntity deleteElement(@PathVariable(value = "id") UUID id) {
        try {
            contentService.deleteElement(id);
            return ResponseEntity.ok(EmptyResponse.getInstance());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.FAILED_TO_DELETE_ELEMENT", messageSource.getMessage("content.FAILED_TO_DELETE_ELEMENT", null, LocaleHolder.getLocale())));
        }
    }
}