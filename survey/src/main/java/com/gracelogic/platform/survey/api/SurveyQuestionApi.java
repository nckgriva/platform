package com.gracelogic.platform.survey.api;


import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.survey.Path;
import com.gracelogic.platform.survey.dto.admin.GetSurveyQuestionsRequest;
import com.gracelogic.platform.survey.dto.admin.SurveyQuestionDTO;
import com.gracelogic.platform.survey.exception.BadDTOException;
import com.gracelogic.platform.survey.exception.LogicDependencyException;
import com.gracelogic.platform.survey.exception.ResultDependencyException;
import com.gracelogic.platform.survey.model.SurveyQuestion;
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
@RequestMapping(value = Path.API_SURVEY_QUESTION)
@Api(value = Path.API_SURVEY_QUESTION, tags = {"Survey question API"})
public class SurveyQuestionApi extends AbstractAuthorizedController {
    @Autowired
    private SurveyService surveyService;

    @Autowired
    @Qualifier("surveyMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @ApiOperation(
            value = "getSurveyQuestions",
            notes = "Get list of survey questions",
            response = EntityListResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getSurveyQuestions(@RequestParam(value = "surveyId", required = false) UUID surveyId,
                                             @RequestParam(value = "surveyPageId", required = false) UUID surveyPageId,
                                             @RequestParam(value = "text", required = false) String text,
                                             @RequestParam(value = "withVariants", required = false, defaultValue = "false") Boolean withVariants,
                                             @RequestParam(value = "calculate", required = false, defaultValue = "false") Boolean calculate,
                                             @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                             @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                             @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                             @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {


        EntityListResponse<SurveyQuestionDTO> properties = surveyService.getSurveyQuestionsPaged(surveyId, surveyPageId, null, text, withVariants, calculate, count, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<SurveyQuestionDTO>>(properties, HttpStatus.OK);
    }

//    @ApiOperation(
//            value = "getSurveyQuestions",
//            notes = "Get list of survey questions",
//            response = EntityListResponse.class
//    )
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "OK"),
//            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
//            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
//    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
//    @RequestMapping(method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity getSurveyQuestions(@RequestBody GetSurveyQuestionsRequest request) {
//
//        if (request.getWithVariants() == null) request.setWithVariants(false);
//        if (request.getStart() == null) request.setStart(0);
//        if (request.getCount() == null) request.setStart(10);
//        if (request.getSortField() == null) request.setSortField("el.created");
//        if (request.getSortDir() == null) request.setSortDir("desc");
//
//        EntityListResponse<SurveyQuestionDTO> properties =
//                surveyService.getSurveyQuestionsPaged(request.getSurveyId(), request.getSurveyPageId(),
//                        request.getSurveyQuestionTypes(),
//                        request.getText(), request.getWithVariants(), request.getCount(), null,
//                        request.getStart(), request.getSortField(), request.getSortDir());
//        return new ResponseEntity<>(properties, HttpStatus.OK);
//    }

    @ApiOperation(
            value = "getSurveyQuestion",
            notes = "Get survey question",
            response = SurveyQuestionDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getSurveyQuestion(@PathVariable(value = "id") UUID id) {
        try {
            SurveyQuestionDTO dto = surveyService.getSurveyQuestion(id);
            return new ResponseEntity<SurveyQuestionDTO>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null,
                    LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "saveSurveyQuestion",
            notes = "Save survey question",
            response = IDResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @PreAuthorize("hasAuthority('SURVEY:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveSurveyQuestion(@RequestBody SurveyQuestionDTO surveyQuestionDTO) {
        try {
            SurveyQuestion surveyQuestion = surveyService.saveSurveyQuestion(surveyQuestionDTO);
            return new ResponseEntity<IDResponse>(new IDResponse(surveyQuestion.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (BadDTOException badDTO) {
            return new ResponseEntity<>(new ErrorResponse("survey.BAD_DTO", badDTO.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "deleteSurveyQuestion",
            notes = "Delete survey question",
            response = EmptyResponse.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)})
    @PreAuthorize("hasAuthority('SURVEY:DELETE')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteSurveyQuestion(@PathVariable(value = "id") UUID id,
                                               @RequestParam(value = "deleteAnswers", required = false) Boolean deleteAnswers) {
        try {
            surveyService.deleteSurveyQuestion(id, false, deleteAnswers != null ? deleteAnswers : false);
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
