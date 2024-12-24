package com.gracelogic.platform.survey.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.survey.Path;
import com.gracelogic.platform.survey.dto.admin.SurveyAnswerVariantDTO;
import com.gracelogic.platform.survey.exception.LogicDependencyException;
import com.gracelogic.platform.survey.exception.ResultDependencyException;
import com.gracelogic.platform.survey.model.SurveyAnswerVariant;
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
@RequestMapping(value = Path.API_SURVEY_QUESTION_ANSWER_VARIANT)
public class SurveyAnswerVariantApi extends AbstractAuthorizedController {
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
    public ResponseEntity getSurveyAnswerVariants(@RequestParam(value = "surveyQuestionId", required = false) UUID surveyQuestionId,
                                                  @RequestParam(value = "description", required = false) String description,
                                                  @RequestParam(value = "calculate", required = false, defaultValue = "false") Boolean calculate,
                                                  @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                                  @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                                  @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                                  @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {


        EntityListResponse<SurveyAnswerVariantDTO> properties = surveyService.getSurveyAnswerVariantsPaged(surveyQuestionId, description, calculate, count, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<SurveyAnswerVariantDTO>>(properties, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SURVEY:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getSurveyAnswerVariant(@PathVariable(value = "id") UUID id) {
        try {
            SurveyAnswerVariantDTO dto = surveyService.getSurveyAnswerVariant(id);
            return new ResponseEntity<SurveyAnswerVariantDTO>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null,
                    LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('SURVEY:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveSurveyAnswerVariant(@RequestBody SurveyAnswerVariantDTO dto) {
        try {
            SurveyAnswerVariant surveyAnswerVariant = surveyService.saveSurveyAnswerVariant(dto);
            return new ResponseEntity<IDResponse>(new IDResponse(surveyAnswerVariant.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", dbMessageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasAuthority('SURVEY:DELETE')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteSurveyAnswerVariant(@PathVariable(value = "id") UUID id,
                                                    @RequestParam(value = "deleteAnswers", required = false) Boolean deleteAnswers) {
        try {
            surveyService.deleteSurveyAnswerVariant(id, false, deleteAnswers != null ? deleteAnswers : false);
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