package com.gracelogic.platform.content.api;

import com.gracelogic.platform.content.Path;
import com.gracelogic.platform.content.dto.ElementDTO;
import com.gracelogic.platform.content.dto.SectionDTO;
import com.gracelogic.platform.content.dto.SectionPatternDTO;
import com.gracelogic.platform.content.model.Element;
import com.gracelogic.platform.content.service.ContentService;
import com.gracelogic.platform.db.dto.DateFormatConstants;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.IDResponse;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@RestController
@RequestMapping(value = Path.API_CONTENT)
@Api(value = Path.API_CONTENT, tags = {"Content API"},
        authorizations = @Authorization(value = "MybasicAuth"))
public class ContentApi extends AbstractAuthorizedController {
    @Autowired
    private ContentService contentService;

    @Autowired
    @Qualifier("contentMessageSource")
    private ResourceBundleMessageSource messageSource;

    @ApiOperation(
            value = "getSections",
            notes = "Get list of sections in a hierarchical form",
            responseContainer = "List",
            response = SectionDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/section", method = RequestMethod.GET)
    public ResponseEntity<List<SectionDTO>> getSections(@ApiParam(name = "parentId", value = "parentId") @RequestParam(value = "parentId", required = false) UUID parentId,
                                      @ApiParam(name = "onlyActive", value = "onlyActive") @RequestParam(value = "onlyActive", required = false, defaultValue = "true") Boolean onlyActive) {
        List<SectionDTO> sectionDTOs = contentService.getSectionsHierarchically(parentId, onlyActive);

        return ResponseEntity.ok(sectionDTOs);
    }

    @ApiOperation(
            value = "getSection",
            notes = "Get section",
            response = SectionDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(method = RequestMethod.GET, value = "/section/{id}")
    public ResponseEntity getSection(@ApiParam(name = "id", value = "id") @PathVariable(value = "id") UUID id) {
        try {
            SectionDTO sectionDTO = contentService.getSection(id);
            return ResponseEntity.ok(sectionDTO);
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.ELEMENT_NOT_FOUND", messageSource.getMessage("content.ELEMENT_NOT_FOUND", null, LocaleHolder.getLocale())));
        }
    }

    @ApiOperation(
            value = "getElements",
            notes = "Get list of elements",
            response = EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(value = "/element", method = RequestMethod.GET)
    public ResponseEntity<EntityListResponse<ElementDTO>> getElements(@ApiParam(name = "query", value = "query") @RequestParam(value = "query", required = false) String query,
                                                                      @ApiParam(name = "queryFields", value = "queryFields") @RequestParam(value = "queryFields", required = false) String sQueryFields,
                                                                      @ApiParam(name = "sectionIds", value = "sectionIds") @RequestParam(value = "sectionIds", required = false) String sSectionIds,
                                                                      @ApiParam(name = "active", value = "active") @RequestParam(value = "active", required = false) Boolean active,
                                                                      @ApiParam(name = "validOnDate", value = "validOnDate") @RequestParam(value = "validOnDate", required = false) String sValidOnDate,
                                                                      @ApiParam(name = "calculate", value = "calculate") @RequestParam(value = "calculate", defaultValue = "false") Boolean calculate,
                                                                      @ApiParam(name = "start", value = "start") @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                                                      @ApiParam(name = "page", value = "page") @RequestParam(value = "page", required = false) Integer page,
                                                                      @ApiParam(name = "count", value = "count") @RequestParam(value = "count", required = false, defaultValue = "10") Integer length,
                                                                      @ApiParam(name = "sortField", value = "sortField") @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                                                      @ApiParam(name = "sortDir", value = "sortDir") @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir,
                                                                      @ApiParam(name = "fields", value = "fields") @RequestParam Map<String, String> allRequestParams) {

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

        Date validOnDate = null;

        try {
            if (!StringUtils.isEmpty(sValidOnDate)) {
                validOnDate = DateFormatConstants.DEFAULT_DATE_FORMAT.get().parse(sValidOnDate);
            }
        } catch (Exception ignored) {
        }

        List<UUID> sectionIds = new LinkedList<>();
        if (!StringUtils.isEmpty(sSectionIds)) {
            for (String s : sSectionIds.split(",")) {
                sectionIds.add(UUID.fromString(s));
            }
        }

        List<String> queryFields = Collections.emptyList();
        if (!StringUtils.isEmpty(sQueryFields)) {
            queryFields = Arrays.asList(sQueryFields.split(","));
        }

        EntityListResponse<ElementDTO> elements = contentService.getElementsPaged(query, queryFields, sectionIds, active, validOnDate,
                fields, calculate, length, page, start, sortField, sortDir);

        return ResponseEntity.ok(elements);
    }

    @ApiOperation(
            value = "getElement",
            notes = "Get element",
            response = ElementDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(method = RequestMethod.GET, value = "/element/{id}")
    public ResponseEntity getElement(@ApiParam(name = "id", value = "id") @PathVariable(value = "id") UUID id,
                                     @ApiParam(name = "includeSectionPattern", value = "includeSectionPattern") @RequestParam(value = "includeSectionPattern", required = false, defaultValue = "false") Boolean includeSectionPattern) {
        try {
            ElementDTO elementDTO = contentService.getElement(id, includeSectionPattern);
            return ResponseEntity.ok(elementDTO);
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.ELEMENT_NOT_FOUND", messageSource.getMessage("content.ELEMENT_NOT_FOUND", null, LocaleHolder.getLocale())));
        }
    }

    @ApiOperation(
            value = "getElementByExternalId",
            notes = "Get element by external id",
            response = ElementDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(method = RequestMethod.GET, value = "/element/by-external-id/{externalId}")
    public ResponseEntity getElementByExternalId(@ApiParam(name = "externalId", value = "externalId") @PathVariable(value = "externalId") String externalId,
                                                 @ApiParam(name = "includeSectionPattern", value = "includeSectionPattern") @RequestParam(value = "includeSectionPattern", required = false, defaultValue = "false") Boolean includeSectionPattern) {
        try {
            ElementDTO elementDTO = contentService.getElementByExternalId(externalId, includeSectionPattern);
            return ResponseEntity.ok(elementDTO);
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.ELEMENT_NOT_FOUND", messageSource.getMessage("content.ELEMENT_NOT_FOUND", null, LocaleHolder.getLocale())));
        }
    }

    @ApiOperation(
            value = "getSectionPattern",
            notes = "Get section pattern by pattern id",
            response = SectionPatternDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(method = RequestMethod.GET, value = "/pattern/{id}")
    public ResponseEntity getSectionPattern(@ApiParam(name = "id", value = "id") @PathVariable(value = "id") UUID id) {
        try {
            SectionPatternDTO sectionPatternDTO = contentService.getSectionPattern(id);
            return ResponseEntity.ok(sectionPatternDTO);
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.PATTERN_NOT_FOUND", messageSource.getMessage("content.PATTERN_NOT_FOUND", null, LocaleHolder.getLocale())));
        }
    }

    @ApiOperation(
            value = "getSectionPatternBySection",
            notes = "Get section pattern by section id",
            response = SectionPatternDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @RequestMapping(method = RequestMethod.GET, value = "/section/{id}/pattern")
    public ResponseEntity getSectionPatternBySection(@ApiParam(name = "id", value = "id") @PathVariable(value = "id") UUID id) {
        try {
            SectionPatternDTO sectionPatternDTO = contentService.getSectionPatternBySection(id);
            return ResponseEntity.ok(sectionPatternDTO);
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.PATTERN_NOT_FOUND", messageSource.getMessage("content.PATTERN_NOT_FOUND", null, LocaleHolder.getLocale())));
        }
    }

    @ApiOperation(
            value = "saveElement",
            notes = "Save element",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('ELEMENT:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/element/save")
    public ResponseEntity saveElement(@ApiParam(name = "elementDTO", value = "elementDTO") @RequestBody ElementDTO elementDTO) {
        try {
            Element element = contentService.saveElement(elementDTO);
            return ResponseEntity.ok(new IDResponse(element.getId()));
        } catch (ObjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.ELEMENT_NOT_FOUND", messageSource.getMessage("content.ELEMENT_NOT_FOUND", null, LocaleHolder.getLocale())));
        }
    }

    @ApiOperation(
            value = "deleteElement",
            notes = "Delete element",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('ELEMENT:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/element/{id}/delete")
    public ResponseEntity deleteElement(@ApiParam(name = "id", value = "id") @PathVariable(value = "id") UUID id) {
        try {
            contentService.deleteElement(id);
            return ResponseEntity.ok(EmptyResponse.getInstance());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("content.FAILED_TO_DELETE_ELEMENT", messageSource.getMessage("content.FAILED_TO_DELETE_ELEMENT", null, LocaleHolder.getLocale())));
        }
    }
}