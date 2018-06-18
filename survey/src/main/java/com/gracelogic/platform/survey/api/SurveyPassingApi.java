package com.gracelogic.platform.survey.api;

import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.survey.Path;
import com.gracelogic.platform.survey.dto.admin.SurveyPageDTO;
import com.gracelogic.platform.survey.dto.user.PageAnswersDTO;
import com.gracelogic.platform.survey.dto.user.SurveyIntroductionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyPassingDTO;
import com.gracelogic.platform.survey.exception.HitRespondentsLimitException;
import com.gracelogic.platform.survey.exception.SurveyExpiredException;
import com.gracelogic.platform.survey.model.SurveyPassing;
import com.gracelogic.platform.survey.service.SurveyService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.user.exception.ForbiddenException;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.IDResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_USER_PASSING)
@Api(value = Path.API_USER_PASSING, tags = {"User survey passing API"})
public class SurveyPassingApi extends AbstractAuthorizedController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource messageSource;

    @ApiOperation(
            value = "getSurveyPage",
            notes = "Get specified survey page",
            response = SurveyPageDTO.class
    )
    @RequestMapping(method = RequestMethod.POST, value = "/{passing_id}/{page}")
    @ResponseBody
    public ResponseEntity getSurveyPage(@PathVariable(value = "passing_id") UUID surveyPassingId,
                                        @PathVariable(value = "page") Integer pageIndex) {
        try {
            SurveyPageDTO dto = surveyService.getSurveyPage(surveyPassingId, pageIndex);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException notFoundException) {
            return new ResponseEntity<>(new ErrorResponse("surveys.NO_SUCH_SURVEY",
                    messageSource.getMessage("surveys.NO_SUCH_SURVEY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException forbiddenException) {
            return new ResponseEntity<>(new ErrorResponse("surveys.FORBIDDEN",
                    messageSource.getMessage("surveys.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(
            value = "saveAnswersAndContinue",
            notes = "Saves user answers and returns SurveyPassingDTO, which contains survey conclusion or next page",
            response = SurveyPassingDTO.class
    )
    @RequestMapping(method = RequestMethod.GET, value = "{passing_id}/save/")
    @ResponseBody
    public ResponseEntity saveAnswersAndContinue(@PathVariable(value = "passing_id") UUID surveyPassingId,
                                                 PageAnswersDTO pageAnswersDTO) {
        try {
            SurveyPassingDTO dto = surveyService.saveAnswersAndContinue(surveyPassingId, pageAnswersDTO);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException notFoundException) {
            return new ResponseEntity<>(new ErrorResponse("surveys.NO_SUCH_SURVEY",
                    messageSource.getMessage("surveys.NO_SUCH_SURVEY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException forbiddenException) {
            return new ResponseEntity<>(new ErrorResponse("surveys.FORBIDDEN",
                    messageSource.getMessage("surveys.FORBIDDEN", null, LocaleHolder.getLocale())), HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(
            value = "continueSurvey",
            notes = "Restores last visited survey page",
            response = SurveyPageDTO.class
    )
    @RequestMapping(method = RequestMethod.GET, value = "/{passing_id}/continue")
    @ResponseBody
    public ResponseEntity continueSurvey(@PathVariable(value = "passing_id") UUID surveyPassingId) {
        try {
            SurveyPageDTO dto = surveyService.continueSurvey(surveyPassingId);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ObjectNotFoundException notFoundException) {
            return new ResponseEntity<>(new ErrorResponse("surveys.NO_SUCH_SURVEY",
                    messageSource.getMessage("surveys.NO_SUCH_SURVEY", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
