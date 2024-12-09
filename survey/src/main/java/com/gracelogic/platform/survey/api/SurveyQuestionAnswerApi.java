package com.gracelogic.platform.survey.api;


import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.survey.Path;
import com.gracelogic.platform.survey.dto.admin.SurveyQuestionAnswerDTO;
import com.gracelogic.platform.survey.model.SurveyQuestionAnswer;
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
@RequestMapping(value = Path.API_SURVEY_ANSWER)
public class SurveyQuestionAnswerApi extends AbstractAuthorizedController {
    @Autowired
    private SurveyService surveyService;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource dbMessageSource;

    @PreAuthorize("hasAuthority('SURVEY_RESULT:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getSurveyQuestionAnswers(@RequestParam(value = "surveyPassingId", required = false) UUID surveyPassingId,
                                                   @RequestParam(value = "calculate", required = false, defaultValue = "false") Boolean calculate,
                                                   @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                                   @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                                   @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                                   @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {


        EntityListResponse<SurveyQuestionAnswerDTO> properties = surveyService.getSurveyQuestionAnswersPaged(surveyPassingId, calculate, count, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<SurveyQuestionAnswerDTO>>(properties, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SURVEY_RESULT:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getSurveyQuestionAnswer(@PathVariable(value = "id") UUID id) {
        try {
            SurveyQuestionAnswerDTO dto = surveyService.getSurveyQuestionAnswer(id);
            return new ResponseEntity<SurveyQuestionAnswerDTO>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null,
                    LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('SURVEY_RESULT:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveSurveyQuestionAnswer(@RequestBody SurveyQuestionAnswerDTO surveyQuestionAnswerDTO) {
        try {
            SurveyQuestionAnswer surveyQuestionAnswer = surveyService.saveSurveyQuestionAnswer(surveyQuestionAnswerDTO);
            return new ResponseEntity<IDResponse>(new IDResponse(surveyQuestionAnswer.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasAuthority('SURVEY_RESULT:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteSurveyQuestionAnswer(@PathVariable(value = "id") UUID id) {
        try {
            surveyService.deleteSurveyQuestionAnswer(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
