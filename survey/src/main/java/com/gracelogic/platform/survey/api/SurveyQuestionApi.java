package com.gracelogic.platform.survey.api;


import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.survey.Path;
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
public class SurveyQuestionApi extends AbstractAuthorizedController {
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
