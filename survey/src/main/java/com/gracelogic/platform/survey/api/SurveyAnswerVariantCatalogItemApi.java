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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(value = Path.API_SURVEY_QUESTION_ANSWER_VARIANT_CATALOG_ITEM, tags = {"Survey answer variant catalog item API"})
public class SurveyAnswerVariantCatalogItemApi {
    @Autowired
    private SurveyService surveyService;

    @Autowired
    @Qualifier("surveyMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @ApiOperation(
            value = "getSurveyAnswerVariantCatalogItems",
            notes = "Get list of survey answer variant catalog items",
            response = EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getSurveyAnswerVariantCatalogItems(@RequestParam(value = "name", required = false) String name,
                                                         @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                                         @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                                         @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                                         @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
        EntityListResponse<SurveyAnswerVariantCatalogItemDTO> properties = surveyService.getSurveyAnswerVariantCatalogItemsPaged(name, count, null, start, sortField, sortDir);
        return new ResponseEntity<>(properties, HttpStatus.OK);
    }

    @ApiOperation(
            value = "getSurveyAnswerVariant",
            notes = "Get survey answer variant",
            response = SurveyAnswerVariantCatalogItemDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getSurveyAnswerVariantCatalogItem(@PathVariable(value = "id") UUID id) {
        try {
            SurveyAnswerVariantCatalogItemDTO dto = surveyService.getSurveyAnswerVariantCatalogItem(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null,
                    LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "saveSurveyAnswerVariantCatalogItem",
            notes = "Save survey answer variant CatalogItem",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveSurveyAnswerVariantCatalogItem(@RequestBody SurveyAnswerVariantCatalogItemDTO dto) {
        try {
            SurveyAnswerVariantCatalogItem surveyAnswerVariantCatalogItem = surveyService.saveSurveyAnswerVariantCatalogItem(dto);
            return new ResponseEntity<IDResponse>(new IDResponse(surveyAnswerVariantCatalogItem.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }

    }

    @ApiOperation(
            value = "deleteSurveyAnswerVariantCatalogItem",
            notes = "Delete survey answer variant CatalogItem",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SURVEY:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteSurveyAnswerVariantCatalogItem(@PathVariable(value = "id") UUID id) {
        try {
            surveyService.deleteSurveyAnswerVariantCatalogItem(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
