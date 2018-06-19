package com.gracelogic.platform.survey.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.survey.dto.admin.*;
import com.gracelogic.platform.survey.dto.user.PageAnswersDTO;
import com.gracelogic.platform.survey.dto.user.SurveyInteractionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyIntroductionDTO;
import com.gracelogic.platform.survey.exception.ResultDependencyException;
import com.gracelogic.platform.survey.exception.LogicDependencyException;
import com.gracelogic.platform.survey.model.*;
import com.gracelogic.platform.user.dto.AuthorizedUser;

import java.util.UUID;

public interface SurveyService {

    SurveyIntroductionDTO getSurveyIntroduction(UUID surveyId) throws ObjectNotFoundException;

    SurveyInteractionDTO startSurvey(UUID surveyId, AuthorizedUser user, String remoteAddress) throws ObjectNotFoundException;
    SurveyInteractionDTO saveAnswersAndContinue(UUID surveyPassingId, PageAnswersDTO dto) throws ObjectNotFoundException;

    SurveyInteractionDTO getSurveyPage(UUID surveyPassingId, int pageIndex) throws ObjectNotFoundException;
    SurveyInteractionDTO continueSurvey(UUID surveyPassingId) throws ObjectNotFoundException;

    EntityListResponse<SurveyDTO> getSurveysPaged(String name, Integer count, Integer page, Integer start, String sortField, String sortDir);
    Survey saveSurvey(SurveyDTO dto, AuthorizedUser user) throws ObjectNotFoundException;
    SurveyDTO getSurvey(UUID surveyId) throws ObjectNotFoundException;
    void deleteSurvey(UUID id) throws LogicDependencyException, ResultDependencyException;

    EntityListResponse<SurveyAnswerVariantDTO> getSurveyAnswerVariantsPaged(String description, Integer count, Integer page,
                                                                            Integer start, String sortField, String sortDir);
    SurveyAnswerVariant saveSurveyAnswerVariant(SurveyAnswerVariantDTO dto) throws ObjectNotFoundException;
    SurveyAnswerVariantDTO getSurveyAnswerVariant(UUID id) throws ObjectNotFoundException;
    void deleteSurveyAnswerVariant(UUID id, boolean deleteLogic) throws LogicDependencyException, ResultDependencyException;

    EntityListResponse<SurveyPageDTO> getSurveyPagesPaged(String description, Integer count, Integer page,
                                                          Integer start, String sortField, String sortDir);
    SurveyPage saveSurveyPage(SurveyPageDTO dto) throws ObjectNotFoundException;
    SurveyPageDTO getSurveyPage(UUID id) throws ObjectNotFoundException;
    void deleteSurveyPage(UUID id) throws LogicDependencyException, ResultDependencyException;

    EntityListResponse<SurveyQuestionDTO> getSurveyQuestionsPaged(String text, Integer count, Integer page,
                                                                  Integer start, String sortField, String sortDir);
    SurveyQuestion saveSurveyQuestion(SurveyQuestionDTO dto) throws ObjectNotFoundException;
    SurveyQuestionDTO getSurveyQuestion(UUID surveyQuestionId) throws ObjectNotFoundException;
    void deleteSurveyQuestion(UUID id, boolean deleteLogic) throws LogicDependencyException, ResultDependencyException;

    EntityListResponse<SurveyLogicTriggerDTO> getSurveyLogicTriggersPaged(Integer count, Integer page,
                                                                          Integer start, String sortField, String sortDir);
    SurveyLogicTrigger saveSurveyLogicTrigger(SurveyLogicTriggerDTO dto) throws ObjectNotFoundException;
    SurveyLogicTriggerDTO getSurveyLogicTrigger(UUID id) throws ObjectNotFoundException;
    void deleteSurveyLogicTrigger(UUID id);

    EntityListResponse<SurveyPassingDTO> getSurveyPassingsPaged(UUID surveyId, UUID userId, String lastVisitIP, Integer count, Integer page,
                                                                    Integer start, String sortField, String sortDir);
    SurveyPassing saveSurveyPassing(SurveyPassingDTO dto) throws ObjectNotFoundException;
    SurveyPassingDTO getSurveyPassing(UUID id) throws ObjectNotFoundException;
    void deleteSurveyPassing(UUID id);

    EntityListResponse<SurveyQuestionAnswerDTO> getSurveyQuestionAnswersPaged(UUID surveyPassingId, Integer count, Integer page,
                                                          Integer start, String sortField, String sortDir);
    SurveyQuestionAnswer saveSurveyQuestionAnswer(SurveyQuestionAnswerDTO dto) throws ObjectNotFoundException;
    SurveyQuestionAnswerDTO getSurveyQuestionAnswer(UUID id) throws ObjectNotFoundException;
    void deleteSurveyQuestionAnswer(UUID id);
}
