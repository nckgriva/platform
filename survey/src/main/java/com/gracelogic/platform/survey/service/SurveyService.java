package com.gracelogic.platform.survey.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.survey.dto.admin.SurveyAnswerVariantDTO;
import com.gracelogic.platform.survey.dto.admin.SurveyDTO;
import com.gracelogic.platform.survey.dto.admin.SurveyPageDTO;
import com.gracelogic.platform.survey.dto.admin.SurveyQuestionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyIntroductionDTO;
import com.gracelogic.platform.survey.exception.HitRespondentsLimitException;
import com.gracelogic.platform.survey.model.Survey;
import com.gracelogic.platform.survey.model.SurveyAnswerVariant;
import com.gracelogic.platform.survey.model.SurveyPage;
import com.gracelogic.platform.survey.model.SurveyQuestion;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.ForbiddenException;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public interface SurveyService {

    SurveyIntroductionDTO getSurveyIntroduction(UUID surveyId, String remoteAddress, AuthorizedUser user)
            throws ObjectNotFoundException, ForbiddenException, HitRespondentsLimitException;

    EntityListResponse<SurveyDTO> getSurveysPaged(String name, Integer count, Integer page, Integer start, String sortField, String sortDir);
    Survey saveSurvey(SurveyDTO dto, AuthorizedUser user) throws ObjectNotFoundException;
    SurveyDTO getSurvey(UUID surveyId) throws ObjectNotFoundException;
    void deleteSurvey(UUID id);

    EntityListResponse<SurveyAnswerVariantDTO> getSurveyAnswerVariantsPaged(String description, Integer count, Integer page,
                                                                            Integer start, String sortField, String sortDir);
    SurveyAnswerVariant saveSurveyAnswerVariant(SurveyAnswerVariantDTO dto) throws ObjectNotFoundException;
    SurveyAnswerVariantDTO getSurveyAnswerVariant(UUID id) throws ObjectNotFoundException;
    void deleteSurveyAnswerVariant(UUID id);

    EntityListResponse<SurveyPageDTO> getSurveyPagesPaged(Integer count, Integer page,
                                                          Integer start, String sortField, String sortDir);
    SurveyPage saveSurveyPage(SurveyPageDTO dto) throws ObjectNotFoundException;
    SurveyPageDTO getSurveyPage(UUID id) throws ObjectNotFoundException;
    void deleteSurveyPage(UUID id);

    EntityListResponse<SurveyQuestionDTO> getSurveyQuestionsPaged(String text, Integer count, Integer page,
                                                                  Integer start, String sortField, String sortDir);
    SurveyQuestion saveSurveyQuestion(SurveyQuestionDTO dto) throws ObjectNotFoundException;
    SurveyQuestionDTO getSurveyQuestion(UUID surveyQuestionId) throws ObjectNotFoundException;
    void deleteSurveyQuestion(UUID id);
}
