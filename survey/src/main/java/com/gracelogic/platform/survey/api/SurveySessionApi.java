package com.gracelogic.platform.survey.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.survey.Path;
import com.gracelogic.platform.survey.dto.admin.SurveySessionDTO;
import com.gracelogic.platform.survey.dto.user.PageAnswersDTO;
import com.gracelogic.platform.survey.dto.user.SurveyInteractionDTO;
import com.gracelogic.platform.survey.exception.UnansweredException;
import com.gracelogic.platform.survey.model.SurveySession;
import com.gracelogic.platform.survey.service.SurveyService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.user.exception.ForbiddenException;
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
@RequestMapping(value = Path.API_SURVEY_SESSION)
@Api(value = Path.API_SURVEY_SESSION, tags = {"Survey session API"})
public class SurveySessionApi extends AbstractAuthorizedController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    @Qualifier("surveyMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @ApiOperation(
            value = "goToPage",
            notes = "Go to the specified survey page",
            response = SurveyInteractionDTO.class
    )
    @RequestMapping(method = RequestMethod.GET, value = "/{session_id}/{page}")
    @ResponseBody
    public ResponseEntity goToPage(@PathVariable(value = "session_id") UUID surveySessionId,
                                        @PathVariable(value = "page") Integer pageIndex) {
        try {
            SurveyInteractionDTO dto = surveyService.goToPage(surveySessionId, pageIndex);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ForbiddenException forbiddenException) {
            return new ResponseEntity<>(new ErrorResponse("survey.FORBIDDEN",
                    messageSource.getMessage("survey.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ObjectNotFoundException notFoundException) {
            return new ResponseEntity<>(new ErrorResponse("survey.NO_SUCH_SESSION",
                    messageSource.getMessage("survey.NO_SUCH_SESSION", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "goBack",
            notes = "Returns back to the previous page, using page visit history",
            response = SurveyInteractionDTO.class
    )
    @RequestMapping(method = RequestMethod.GET, value = "/{session_id}/back")
    @ResponseBody
    public ResponseEntity goBack(@PathVariable(value = "session_id") UUID surveySessionId) {
        try {
            SurveyInteractionDTO dto = surveyService.goBack(surveySessionId);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ForbiddenException forbiddenException) {
            return new ResponseEntity<>(new ErrorResponse("survey.FORBIDDEN",
                    messageSource.getMessage("survey.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ObjectNotFoundException notFoundException) {
            return new ResponseEntity<>(new ErrorResponse("survey.NO_SUCH_SESSION",
                    messageSource.getMessage("survey.NO_SUCH_SESSION", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "saveAnswersAndContinue",
            notes = "Saves user answers and returns SurveyInteractionDTO, which contains survey conclusion or next page",
            response = SurveyInteractionDTO.class
    )
    @RequestMapping(method = RequestMethod.POST, value = "/{session_id}/save")
    @ResponseBody
    public ResponseEntity saveAnswersAndContinue(@PathVariable(value = "session_id") UUID surveySessionId,
                                                 @RequestBody PageAnswersDTO pageAnswersDTO) {
        try {
            SurveyInteractionDTO dto = surveyService.saveAnswersAndContinue(surveySessionId, pageAnswersDTO);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ForbiddenException forbiddenException) {
            return new ResponseEntity<>(new ErrorResponse("survey.FORBIDDEN",
                    messageSource.getMessage("survey.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ObjectNotFoundException notFoundException) {
            return new ResponseEntity<>(new ErrorResponse("survey.NO_SUCH_SESSION",
                    messageSource.getMessage("survey.NO_SUCH_SESSION", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (UnansweredException unansweredException) {
            return new ResponseEntity<>(new ErrorResponse("survey.UNANSWERED",
                    messageSource.getMessage("survey.UNANSWERED", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }

    }

    @ApiOperation(
            value = "continueSurvey",
            notes = "Restores last visited survey page",
            response = SurveyInteractionDTO.class
    )
    @RequestMapping(method = RequestMethod.GET, value = "/{session_id}/continue")
    @ResponseBody
    public ResponseEntity continueSurvey(@PathVariable(value = "session_id") UUID surveySessionId) {
        try {
            SurveyInteractionDTO dto = surveyService.continueSurvey(surveySessionId);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ForbiddenException forbiddenException) {
            return new ResponseEntity<>(new ErrorResponse("survey.FORBIDDEN",
                    messageSource.getMessage("survey.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ObjectNotFoundException notFoundException) {
            return new ResponseEntity<>(new ErrorResponse("survey.NO_SUCH_SESSION",
                    messageSource.getMessage("survey.NO_SUCH_SESSION", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "getSurveySessions",
            notes = "Get list of survey sessions",
            response = EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SURVEY_RESULT:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getSurveySessions(@RequestParam(value = "surveyId", required = false) UUID surveyId,
                                     @RequestParam(value = "userId", required = false) UUID userId,
                                     @RequestParam(value = "lastVisitIP", required = false) String lastVisitIP,
                                     @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                     @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                     @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                     @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {
        EntityListResponse<SurveySessionDTO> results = surveyService.getSurveySessionsPaged(surveyId, userId, lastVisitIP, count, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<SurveySessionDTO>>(results, HttpStatus.OK);
    }

    @ApiOperation(
            value = "getSurveySession",
            notes = "Get survey passing",
            response = SurveySessionDTO.class
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
            SurveySessionDTO dto = surveyService.getSurveySession(id);
            return new ResponseEntity<SurveySessionDTO>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null,
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
    public ResponseEntity saveSurveyPassing(@RequestBody SurveySessionDTO surveySessionDTO) {
        try {
            SurveySession surveySession = surveyService.saveSurveySession(surveySessionDTO);
            return new ResponseEntity<IDResponse>(new IDResponse(surveySession.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
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
            surveyService.deleteSurveySession(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
