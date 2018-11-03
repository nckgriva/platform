package com.gracelogic.platform.survey.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.survey.Path;
import com.gracelogic.platform.survey.dto.admin.ImportCatalogItemsDTO;
import com.gracelogic.platform.survey.dto.admin.SurveyAnswerVariantCatalogDTO;
import com.gracelogic.platform.survey.model.SurveyAnswerVariantCatalog;
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

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_SURVEY_QUESTION_ANSWER_VARIANT_CATALOG)
@Api(value = Path.API_SURVEY_QUESTION_ANSWER_VARIANT_CATALOG, tags = {"Survey answer variant catalog API"})
public class SurveyAnswerVariantCatalogApi {
    @Autowired
    private SurveyService surveyService;

    @Autowired
    @Qualifier("surveyMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @ApiOperation(
            value = "getCatalogs",
            notes = "Get list of survey answer variant catalog",
            response = EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getCatalogs(@RequestParam(value = "name", required = false) String name,
                                      @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                      @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                      @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                      @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
        EntityListResponse<SurveyAnswerVariantCatalogDTO> properties = surveyService.getCatalogsPaged(name, count, null, start, sortField, sortDir);
        return new ResponseEntity<>(properties, HttpStatus.OK);
    }

    @ApiOperation(
            value = "getCatalog",
            notes = "Get survey answer variant catalog",
            response = SurveyAnswerVariantCatalogDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getCatalog(@PathVariable(value = "id") UUID id) {
        try {
            SurveyAnswerVariantCatalogDTO dto = surveyService.getCatalog(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null,
                    LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "exportCatalogItems",
            notes = "Exports catalog items in csv format",
            response = SurveyAnswerVariantCatalogDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/export")
    @ResponseBody
    public void exportCatalogItems(@PathVariable(value = "id") UUID catalogId, HttpServletResponse response) {
        try {
            String results = surveyService.exportCatalogItems(catalogId);
            String date = new SimpleDateFormat("dd_MM_yyyy").format(new Date());
            String fileName = "catalog_export_" + date + ".csv";

            response.setContentType("text/csv; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition", String.format("attachment;filename=%s", fileName));
            response.getWriter().print(results);

            response.flushBuffer();
        } catch (Exception exception) {
            try {
                PrintWriter pw = response.getWriter();
                exception.printStackTrace(pw);
                pw.close();
            } catch (Exception ignored) { }
        }
    }

    @ApiOperation(
            value = "importCatalogItems",
            notes = "Imports catalog items",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/import")
    @ResponseBody
    public ResponseEntity importCatalogItems(@RequestBody ImportCatalogItemsDTO dto) {
        try {
            surveyService.importCatalogItems(dto);
            return new ResponseEntity<>(new IDResponse(dto.getCatalogId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "saveCatalog",
            notes = "Save survey answer variant catalog",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveCatalog(@RequestBody SurveyAnswerVariantCatalogDTO dto) {
        try {
            SurveyAnswerVariantCatalog surveyAnswerVariantCatalog = surveyService.saveCatalog(dto);
            return new ResponseEntity<IDResponse>(new IDResponse(surveyAnswerVariantCatalog.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "deleteCatalog",
            notes = "Delete survey answer variant catalog",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SURVEY:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteCatalog(@PathVariable(value = "id") UUID id) {
        try {
            surveyService.deleteCatalog(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
