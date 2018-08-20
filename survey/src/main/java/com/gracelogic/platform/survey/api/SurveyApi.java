package com.gracelogic.platform.survey.api;


import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.property.dto.PropertyDTO;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.survey.Path;
import com.gracelogic.platform.survey.dto.admin.SurveyDTO;
import com.gracelogic.platform.survey.dto.user.SurveyInteractionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyIntroductionDTO;
import com.gracelogic.platform.survey.exception.*;
import com.gracelogic.platform.survey.model.Survey;
import com.gracelogic.platform.survey.service.SurveyService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.user.exception.ForbiddenException;
import com.gracelogic.platform.web.ServletUtils;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.IDResponse;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.*;

@Controller
@RequestMapping(value = Path.API_SURVEY)
@Api(value = Path.API_SURVEY, tags = {"Survey API"})
public class SurveyApi extends AbstractAuthorizedController {
    @Autowired
    private SurveyService surveyService;

    @Autowired
    @Qualifier("surveyMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @Autowired
    private PropertyService propertyService;

    @ApiOperation(
            value = "exportResults",
            notes = "Exports survey results to csv file"
    )
    @PreAuthorize("hasAuthority('SURVEY_RESULT:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value="/{id}/export")
    public void exportResults(@PathVariable(value = "id") UUID surveyId, HttpServletResponse response) {
        try {
            String test = surveyService.exportResults(surveyId);
            byte[] bytes = test.getBytes();
            IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
            response.setContentType("application/csv");
            response.setContentLength(bytes.length);
            response.flushBuffer();
        } catch (Exception ignored) {

        }
    }

    @ApiOperation(
            value = "getBaseUrl",
            notes = "Returns base url for survey.link setup purposes",
            response = PropertyDTO.class
    )
    @RequestMapping(method = RequestMethod.GET, value = "/base_url")
    @ResponseBody
    @PreAuthorize("hasAuthority('SURVEY:SAVE')")
    public ResponseEntity getBaseUrl() {
        String propertyName = "web:base_url";
        String propertyValue = propertyService.getPropertyValue(propertyName);

        PropertyDTO propertyDTO = new PropertyDTO();
        propertyDTO.setValue(propertyValue);
        propertyDTO.setName(propertyName);

        return new ResponseEntity<>(propertyDTO, HttpStatus.OK);
    }

    @ApiOperation(
            value = "getInitialSurveyInfo",
            notes = "Sends initial survey information to user. This method does not begin the survey.",
            response = SurveyIntroductionDTO.class
    )
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/info")
    @ResponseBody
    public ResponseEntity getInitialSurveyInfo(@PathVariable(value = "id") UUID surveyId) {
        try {
            SurveyIntroductionDTO dto = surveyService.getSurveyIntroduction(surveyId);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException notFoundException) {
            return new ResponseEntity<>(new ErrorResponse("survey.NO_SUCH_SURVEY",
                    messageSource.getMessage("survey.NO_SUCH_SURVEY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException forbiddenException) {
            return new ResponseEntity<>(new ErrorResponse("survey.FORBIDDEN",
                    messageSource.getMessage("survey.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "startSurvey",
            notes = "Starts survey and sends SurveyInteractionDTO with first page back to user",
            response = SurveyInteractionDTO.class
    )
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/start")
    @ResponseBody
    public ResponseEntity startSurvey(HttpServletRequest request,
                                      @PathVariable(value = "id") UUID surveyId) {
        try {
            SurveyInteractionDTO dto = surveyService.startSurvey(surveyId, getUser(), ServletUtils.getRemoteAddress(request));
            return new ResponseEntity<SurveyInteractionDTO>(dto, HttpStatus.OK);
        } catch (RespondentLimitException respondentLimitException) {
            return new ResponseEntity<>(new ErrorResponse("survey.RESPONDENT_LIMIT",
                    messageSource.getMessage("survey.RESPONDENT_LIMIT", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException forbiddenException) {
            return new ResponseEntity<>(new ErrorResponse("survey.FORBIDDEN",
                    messageSource.getMessage("survey.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ObjectNotFoundException notFoundException) {
            return new ResponseEntity<>(new ErrorResponse("survey.NO_SUCH_SURVEY",
                    messageSource.getMessage("survey.NO_SUCH_SURVEY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (MaxAttemptsHitException maxAttemptsException) {
            return new ResponseEntity<>(new ErrorResponse("survey.MAX_ATTEMPTS_HIT",
                    messageSource.getMessage("survey.MAX_ATTEMPTS_HIT", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "startSurveyPreview",
            notes = "Starts survey preview. This method is only available to users which have SURVEY:SHOW grant.",
            response = SurveyInteractionDTO.class
    )
    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/start_preview")
    @ResponseBody
    public ResponseEntity startSurveyPreview(HttpServletRequest request,
                                      @PathVariable(value = "id") UUID surveyId) {
        try {
            SurveyInteractionDTO dto = surveyService.startSurveyPreview(surveyId, getUser(), ServletUtils.getRemoteAddress(request));
            return new ResponseEntity<SurveyInteractionDTO>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null,
                    LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "getSurveys",
            notes = "Get list of surveys",
            response = EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getSurveys(@RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                     @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                     @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                     @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {


        EntityListResponse<SurveyDTO> properties = surveyService.getSurveysPaged(name, count, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<SurveyDTO>>(properties, HttpStatus.OK);
    }

    @ApiOperation(
            value = "getSurvey",
            notes = "Get survey",
            response = SurveyDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getSurvey(@PathVariable(value = "id") UUID id,
                                    @RequestParam(value = "entireSurvey", required = false) Boolean entireSurvey) {
        try {
            SurveyDTO dto = surveyService.getSurvey(id, entireSurvey != null ? entireSurvey : false);
            return new ResponseEntity<SurveyDTO>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null,
                    LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "saveSurvey",
            notes = "Save survey",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveSurvey(@RequestBody SurveyDTO surveyDTO,
                                     @RequestParam(value = "entireSurvey", required = false) Boolean entireSurvey) {
        try {
            Survey survey;
            if (!entireSurvey) {
                survey = surveyService.saveSurvey(surveyDTO, getUser());
            } else {
                survey = surveyService.saveEntireSurvey(surveyDTO, getUser());
            }
            return new ResponseEntity<IDResponse>(new IDResponse(survey.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ResultDependencyException resultDependency) {
            return new ResponseEntity<>(new ErrorResponse("survey.RESULT_DEPENDENCY", messageSource.getMessage("survey.RESULT_DEPENDENCY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (LogicDependencyException logicDependency) {
            return new ResponseEntity<>(new ErrorResponse("survey.LOGIC_DEPENDENCY",
                    messageSource.getMessage("survey.LOGIC_DEPENDENCY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (BadDTOException badDTO) {
            return new ResponseEntity<>(new ErrorResponse("survey.BAD_DTO", badDTO.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (PersistenceException persistenceException) {
            return new ResponseEntity<>(new ErrorResponse("survey.DEPENDENCY_ERROR", persistenceException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "deleteSurvey",
            notes = "Delete survey",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SURVEY:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteSurvey(@PathVariable(value = "id") UUID id) {
        try {
            surveyService.deleteSurvey(id);
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
