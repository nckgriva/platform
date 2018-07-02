package com.gracelogic.platform.survey.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.survey.dto.admin.*;
import com.gracelogic.platform.survey.dto.user.PageAnswersDTO;
import com.gracelogic.platform.survey.dto.user.SurveyInteractionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyIntroductionDTO;
import com.gracelogic.platform.survey.exception.RespondentLimitException;
import com.gracelogic.platform.survey.exception.ResultDependencyException;
import com.gracelogic.platform.survey.exception.LogicDependencyException;
import com.gracelogic.platform.survey.model.*;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.ForbiddenException;

import java.util.UUID;

public interface SurveyService {

    SurveyIntroductionDTO getSurveyIntroduction(UUID surveyId) throws ObjectNotFoundException, ForbiddenException;

    SurveyInteractionDTO startSurvey(UUID surveyId, AuthorizedUser user, String ipAddress)
            throws ObjectNotFoundException, RespondentLimitException, ForbiddenException;

    SurveyInteractionDTO saveAnswersAndContinue(UUID surveySessionId, PageAnswersDTO dto) throws ObjectNotFoundException, ForbiddenException;

    SurveyInteractionDTO getSurveyPage(UUID surveySessionId, int pageIndex) throws ObjectNotFoundException, ForbiddenException;

    SurveyInteractionDTO continueSurvey(UUID surveySessionId) throws ObjectNotFoundException, ForbiddenException;

    EntityListResponse<SurveyDTO> getSurveysPaged(String name, Integer count, Integer page, Integer start, String sortField, String sortDir);

    Survey saveEntireSurvey(SurveyDTO surveyDTO, AuthorizedUser user)
            throws ObjectNotFoundException, LogicDependencyException, ResultDependencyException;

    Survey saveSurvey(SurveyDTO dto, AuthorizedUser user) throws ObjectNotFoundException;

    SurveyDTO getSurvey(UUID surveyId) throws ObjectNotFoundException;

    void deleteSurvey(UUID id) throws LogicDependencyException, ResultDependencyException;

    EntityListResponse<SurveyAnswerVariantDTO> getSurveyAnswerVariantsPaged(UUID surveyQuestionId, String description, Integer count, Integer page,
                                                                            Integer start, String sortField, String sortDir);

    SurveyAnswerVariant saveSurveyAnswerVariant(SurveyAnswerVariantDTO dto) throws ObjectNotFoundException;

    SurveyAnswerVariantDTO getSurveyAnswerVariant(UUID id) throws ObjectNotFoundException;

    void deleteSurveyAnswerVariant(UUID id, boolean deleteLogic) throws LogicDependencyException, ResultDependencyException;

    EntityListResponse<SurveyPageDTO> getSurveyPagesPaged(UUID surveyId, String description, Integer count, Integer page,
                                                          Integer start, String sortField, String sortDir);

    SurveyPage saveSurveyPage(SurveyPageDTO dto) throws ObjectNotFoundException;

    SurveyPageDTO getSurveyPage(UUID id) throws ObjectNotFoundException;

    void deleteSurveyPage(UUID id) throws LogicDependencyException, ResultDependencyException;

    EntityListResponse<SurveyQuestionDTO> getSurveyQuestionsPaged(UUID surveyPageId, String text, Integer count, Integer page,
                                                                  Integer start, String sortField, String sortDir);

    SurveyQuestion saveSurveyQuestion(SurveyQuestionDTO dto) throws ObjectNotFoundException;

    SurveyQuestionDTO getSurveyQuestion(UUID surveyQuestionId) throws ObjectNotFoundException;

    void deleteSurveyQuestion(UUID id, boolean deleteLogic) throws LogicDependencyException, ResultDependencyException;

    EntityListResponse<SurveyLogicTriggerDTO> getSurveyLogicTriggersPaged(UUID surveyQuestionId, UUID surveyPageId, UUID surveyAnswerVariantId, Integer count, Integer page,
                                                                          Integer start, String sortField, String sortDir);

    SurveyLogicTrigger saveSurveyLogicTrigger(SurveyLogicTriggerDTO dto) throws ObjectNotFoundException;

    SurveyLogicTriggerDTO getSurveyLogicTrigger(UUID id) throws ObjectNotFoundException;

    void deleteSurveyLogicTrigger(UUID id);

    EntityListResponse<SurveySessionDTO> getSurveySessionsPaged(UUID surveyId, UUID userId, String lastVisitIP, Integer count, Integer page,
                                                                Integer start, String sortField, String sortDir);

    SurveySession saveSurveySession(SurveySessionDTO dto) throws ObjectNotFoundException;

    SurveySessionDTO getSurveySession(UUID id) throws ObjectNotFoundException;

    void deleteSurveySession(UUID id);

    EntityListResponse<SurveyQuestionAnswerDTO> getSurveyQuestionAnswersPaged(UUID surveySessionId, Integer count, Integer page,
                                                                              Integer start, String sortField, String sortDir);

    SurveyQuestionAnswer saveSurveyQuestionAnswer(SurveyQuestionAnswerDTO dto) throws ObjectNotFoundException;

    SurveyQuestionAnswerDTO getSurveyQuestionAnswer(UUID id) throws ObjectNotFoundException;

    void deleteSurveyQuestionAnswer(UUID id);
}
