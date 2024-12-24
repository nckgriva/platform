package com.gracelogic.platform.survey.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.survey.Path;
import com.gracelogic.platform.survey.dto.admin.SurveyLogicTriggerDTO;
import com.gracelogic.platform.survey.exception.BadDTOException;
import com.gracelogic.platform.survey.model.SurveyLogicTrigger;
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

import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_SURVEY_LOGIC)
public class SurveyLogicTriggerApi extends AbstractAuthorizedController {
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
    public ResponseEntity getSurveyLogicTriggers(
            @RequestParam(value = "surveyQuestionId", required = false) UUID surveyQuestionId,
            @RequestParam(value = "surveyPageId", required = false) UUID surveyPageId,
            @RequestParam(value = "surveyAnswerVariantId", required = false) UUID surveyAnswerVariantId,
            @RequestParam(value = "calculate", required = false, defaultValue = "false") Boolean calculate,
            @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
            @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
            @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
            @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {


        EntityListResponse<SurveyLogicTriggerDTO> properties = surveyService.getSurveyLogicTriggersPaged(surveyQuestionId, surveyPageId, surveyAnswerVariantId, calculate, count, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<SurveyLogicTriggerDTO>>(properties, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getSurveyLogicTrigger(@PathVariable(value = "id") UUID id) {
        try {
            SurveyLogicTriggerDTO dto = surveyService.getSurveyLogicTrigger(id);
            return new ResponseEntity<SurveyLogicTriggerDTO>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null,
                    LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('SURVEY:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveSurveyLogicTrigger(@RequestBody SurveyLogicTriggerDTO dto) {
        try {
            SurveyLogicTrigger surveyLogicTrigger = surveyService.saveSurveyLogicTrigger(dto);
            return new ResponseEntity<IDResponse>(new IDResponse(surveyLogicTrigger.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (BadDTOException badDTO) {
            return new ResponseEntity<>(new ErrorResponse("survey.BAD_DTO", badDTO.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasAuthority('SURVEY:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteSurveyLogicTrigger(@PathVariable(value = "id") UUID id) {
        try {
            surveyService.deleteSurveyLogicTrigger(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", dbMessageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}