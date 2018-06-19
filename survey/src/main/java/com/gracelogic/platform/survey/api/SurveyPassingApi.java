package com.gracelogic.platform.survey.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.survey.Path;
import com.gracelogic.platform.survey.dto.admin.SurveyPassingDTO;
import com.gracelogic.platform.survey.dto.user.PageAnswersDTO;
import com.gracelogic.platform.survey.dto.user.SurveyInteractionDTO;
import com.gracelogic.platform.survey.model.SurveyPassing;
import com.gracelogic.platform.survey.service.SurveyService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
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
@RequestMapping(value = Path.API_SURVEY_PASSING)
@Api(value = Path.API_SURVEY_PASSING, tags = {"Survey passing API"})
public class SurveyPassingApi extends AbstractAuthorizedController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource messageSource;

    @ApiOperation(
            value = "getSurveyPage",
            notes = "Get specified survey page",
            response = SurveyInteractionDTO.class
    )
    @RequestMapping(method = RequestMethod.GET, value = "/{passing_id}/{page}")
    @ResponseBody
    public ResponseEntity getSurveyPage(@PathVariable(value = "passing_id") UUID surveyPassingId,
                                        @PathVariable(value = "page") Integer pageIndex) {
        try {
            SurveyInteractionDTO dto = surveyService.getSurveyPage(surveyPassingId, pageIndex);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException notFoundException) {
            return new ResponseEntity<>(new ErrorResponse("surveys.NO_SUCH_SURVEY",
                    messageSource.getMessage("surveys.NO_SUCH_SURVEY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "saveAnswersAndContinue",
            notes = "Saves user answers and returns SurveyInteractionDTO, which contains survey conclusion or next page",
            response = SurveyInteractionDTO.class
    )
    @RequestMapping(method = RequestMethod.POST, value = "{passing_id}/save/")
    @ResponseBody
    public ResponseEntity saveAnswersAndContinue(@PathVariable(value = "passing_id") UUID surveyPassingId,
                                                 @RequestBody PageAnswersDTO pageAnswersDTO) {
        try {
            SurveyInteractionDTO dto = surveyService.saveAnswersAndContinue(surveyPassingId, pageAnswersDTO);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException notFoundException) {
            return new ResponseEntity<>(new ErrorResponse("surveys.NO_SUCH_SURVEY",
                    messageSource.getMessage("surveys.NO_SUCH_SURVEY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "continueSurvey",
            notes = "Restores last visited survey page",
            response = SurveyInteractionDTO.class
    )
    @RequestMapping(method = RequestMethod.GET, value = "/{passing_id}/continue")
    @ResponseBody
    public ResponseEntity continueSurvey(@PathVariable(value = "passing_id") UUID surveyPassingId) {
        try {
            SurveyInteractionDTO dto = surveyService.continueSurvey(surveyPassingId);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException notFoundException) {
            return new ResponseEntity<>(new ErrorResponse("surveys.NO_SUCH_SURVEY",
                    messageSource.getMessage("surveys.NO_SUCH_SURVEY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "getSurveyPassingResults",
            notes = "Get list of survey passing results",
            response = EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SURVEY_RESULT:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getSurveyPassingResults(@RequestParam(value = "surveyId", required = false) UUID surveyId,
                                     @RequestParam(value = "userId", required = false) UUID userId,
                                     @RequestParam(value = "lastVisitIP", required = false) String lastVisitIP,
                                     @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                     @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                     @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                     @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
        EntityListResponse<SurveyPassingDTO> results = surveyService.getSurveyPassingsPaged(surveyId, userId, lastVisitIP, count, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<SurveyPassingDTO>>(results, HttpStatus.OK);
    }

    @ApiOperation(
            value = "getSurveyPassing",
            notes = "Get survey passing",
            response = SurveyPassingDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY_RESULT:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getSurveyPassing(@PathVariable(value = "id") UUID id) {
        try {
            SurveyPassingDTO dto = surveyService.getSurveyPassing(id);
            return new ResponseEntity<SurveyPassingDTO>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", messageSource.getMessage("db.NOT_FOUND", null,
                    LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "saveSurveyPassing",
            notes = "Save survey passing",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY_RESULT:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveSurveyPassing(@RequestBody SurveyPassingDTO surveyPassingDTO) {
        try {
            SurveyPassing surveyPassing = surveyService.saveSurveyPassing(surveyPassingDTO);
            return new ResponseEntity<IDResponse>(new IDResponse(surveyPassing.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", messageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "deleteSurveyPassing",
            notes = "Delete survey passing",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SURVEY_RESULT:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteSurveyPassing(@PathVariable(value = "id") UUID id) {
        try {
            surveyService.deleteSurveyPassing(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", messageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
