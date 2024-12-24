package com.gracelogic.platform.survey.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.survey.Path;
import com.gracelogic.platform.survey.dto.admin.SurveyAnswerVariantCatalogItemDTO;
import com.gracelogic.platform.survey.model.SurveyAnswerVariantCatalogItem;
import com.gracelogic.platform.survey.service.SurveyService;
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
@RequestMapping(value = Path.API_SURVEY_QUESTION_ANSWER_VARIANT_CATALOG_ITEM)
public class SurveyAnswerVariantCatalogItemApi {
    @Autowired
    private SurveyService surveyService;

    @Autowired
    @Qualifier("surveyMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getCatalogItems(@RequestParam(value = "catalogId", required = false) UUID catalogId,
                                          @RequestParam(value = "text", required = false) String text,
                                          @RequestParam(value = "calculate", required = false, defaultValue = "false") Boolean calculate,
                                          @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                          @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                          @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                          @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
        EntityListResponse<SurveyAnswerVariantCatalogItemDTO> properties = surveyService.getCatalogItemsPaged(catalogId, text, calculate, count, null, start, sortField, sortDir);
        return new ResponseEntity<>(properties, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getCatalogItem(@PathVariable(value = "id") UUID id) {
        try {
            SurveyAnswerVariantCatalogItemDTO dto = surveyService.getCatalogItem(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null,
                    LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('SURVEY:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveCatalogItem(@RequestBody SurveyAnswerVariantCatalogItemDTO dto) {
        try {
            SurveyAnswerVariantCatalogItem surveyAnswerVariantCatalogItem = surveyService.saveCatalogItem(dto);
            return new ResponseEntity<IDResponse>(new IDResponse(surveyAnswerVariantCatalogItem.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('SURVEY:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteCatalogItem(@PathVariable(value = "id") UUID id) {
        try {
            surveyService.deleteCatalogItem(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
