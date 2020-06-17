package com.gracelogic.platform.survey.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.survey.Path;
import com.gracelogic.platform.survey.dto.admin.SurveyPageDTO;
import com.gracelogic.platform.survey.exception.LogicDependencyException;
import com.gracelogic.platform.survey.exception.ResultDependencyException;
import com.gracelogic.platform.survey.model.SurveyPage;
import com.gracelogic.platform.survey.service.SurveyService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.IDResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping(value = Path.API_SURVEY_PAGE)
@Api(value = Path.API_SURVEY_PAGE, tags = {"Survey page API"})
public class SurveyPageApi extends AbstractAuthorizedController {
    @Autowired
    private SurveyService surveyService;

    @Autowired
    @Qualifier("surveyMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @ApiOperation(
            value = "getSurveyPages",
            notes = "Get list of survey pages",
            response = EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getSurveyPages(@RequestParam(value = "surveyId", required = false) UUID surveyId,
                                         @RequestParam(value = "description", required = false) String description,
                                         @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                         @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                         @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                         @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir,
                                         @RequestParam(value = "calculate", required = false, defaultValue = "false") Boolean calculate) {

        EntityListResponse<SurveyPageDTO> properties = surveyService.getSurveyPagesPaged(surveyId, description, count, null, start, sortField, sortDir, calculate);
        return new ResponseEntity<EntityListResponse<SurveyPageDTO>>(properties, HttpStatus.OK);
    }

    @ApiOperation(
            value = "getSurveyPage",
            notes = "Get survey page",
            response = SurveyPageDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getSurveyPage(@PathVariable(value = "id") UUID id) {
        try {
            SurveyPageDTO dto = surveyService.getSurveyPage(id);
            return new ResponseEntity<SurveyPageDTO>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null,
                    LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "saveSurveyPage",
            notes = "Save survey page",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveSurveyPage(@RequestBody SurveyPageDTO surveyPageDTO) {
        try {
            SurveyPage surveyPage = surveyService.saveSurveyPage(surveyPageDTO);
            return new ResponseEntity<IDResponse>(new IDResponse(surveyPage.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "deleteSurveyPage",
            notes = "Delete survey page",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SURVEY:DELETE')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteSurveyPage(@PathVariable(value = "id") UUID id,
                                           @RequestParam(value = "deleteAnswers", required = false) Boolean deleteAnswers) {
        try {
            surveyService.deleteSurveyPage(id, deleteAnswers != null ? deleteAnswers : false);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (ResultDependencyException resultDependency) {
            return new ResponseEntity<>(new ErrorResponse("survey.RESULT_DEPENDENCY",
                    messageSource.getMessage("survey.RESULT_DEPENDENCY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (LogicDependencyException logicDependency) {
            return new ResponseEntity<>(new ErrorResponse("survey.LOGIC_DEPENDENCY",
                    messageSource.getMessage("survey.LOGIC_DEPENDENCY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
