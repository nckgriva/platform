package com.gracelogic.platform.survey.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.survey.dto.admin.*;
import com.gracelogic.platform.survey.dto.user.PageAnswersDTO;
import com.gracelogic.platform.survey.dto.user.SurveyConclusionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyIntroductionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyPassingDTO;
import com.gracelogic.platform.survey.exception.HitRespondentsLimitException;
import com.gracelogic.platform.survey.exception.SurveyExpiredException;
import com.gracelogic.platform.survey.model.*;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.ForbiddenException;
import java.util.UUID;

public interface SurveyService {

    SurveyIntroductionDTO getSurveyIntroduction(UUID surveyId) throws ObjectNotFoundException;

    SurveyPassingDTO startSurvey(UUID surveyId, AuthorizedUser user, String remoteAddress) throws ObjectNotFoundException;
    SurveyPassingDTO saveAnswersAndContinue(UUID surveyPassingId, PageAnswersDTO dto) throws ObjectNotFoundException, ForbiddenException;

    SurveyPageDTO getSurveyPage(UUID surveyPassingId, int pageIndex) throws ObjectNotFoundException, ForbiddenException;
    SurveyPageDTO continueSurvey(UUID surveyPassingId) throws ObjectNotFoundException;

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

    EntityListResponse<SurveyVariantLogicDTO> getSurveyVariantLogicsPaged(Integer count, Integer page,
                                                                          Integer start, String sortField, String sortDir);
    SurveyVariantLogic saveSurveyVariantLogic(SurveyVariantLogicDTO dto) throws ObjectNotFoundException;
    SurveyVariantLogicDTO getSurveyVariantLogic(UUID id) throws ObjectNotFoundException;
    void deleteSurveyVariantLogic(UUID id);
}
