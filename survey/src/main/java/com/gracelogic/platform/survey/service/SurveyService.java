package com.gracelogic.platform.survey.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.survey.dto.admin.*;
import com.gracelogic.platform.survey.dto.user.PageAnswersDTO;
import com.gracelogic.platform.survey.dto.user.SurveyInteractionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyIntroductionDTO;
import com.gracelogic.platform.survey.exception.*;
import com.gracelogic.platform.survey.model.*;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.ForbiddenException;

import java.util.Set;
import java.util.UUID;

public interface SurveyService {

    String exportResults(UUID surveyId) throws ObjectNotFoundException, InternalErrorException;

    String exportCatalogItems(UUID catalogId) throws ObjectNotFoundException;

    void importCatalogItems(ImportCatalogItemsDTO dto) throws ObjectNotFoundException;

    SurveyIntroductionDTO getSurveyIntroduction(UUID surveyId) throws ObjectNotFoundException, ForbiddenException;

    SurveyIntroductionDTO getSurveyIntroductionByExternalId(String externalId)
            throws ObjectNotFoundException, ForbiddenException;

    SurveyInteractionDTO startSurvey(UUID surveyId, AuthorizedUser user, String ipAddress)
            throws ObjectNotFoundException, RespondentLimitException, ForbiddenException, MaxAttemptsException;

    SurveyInteractionDTO startSurveyByExternalId(String externalId, AuthorizedUser user, String ipAddress)
            throws ObjectNotFoundException, RespondentLimitException, ForbiddenException, MaxAttemptsException;

    SurveyInteractionDTO startSurveyPreview(UUID surveyId, AuthorizedUser user, String ipAddress)
            throws ObjectNotFoundException;

    SurveyInteractionDTO startSurveyPreviewByExternalId(String externalSurveyId, AuthorizedUser user, String ipAddress)
            throws ObjectNotFoundException;

    SurveyInteractionDTO saveAnswersAndContinue(UUID surveySessionId, PageAnswersDTO dto)
            throws ObjectNotFoundException, ForbiddenException, UnansweredException, UnansweredOtherOptionException;

    SurveyInteractionDTO goToPage(UUID surveySessionId, int pageIndex) throws ObjectNotFoundException, ForbiddenException;

    SurveyInteractionDTO goBack(UUID surveySessionId) throws ObjectNotFoundException, ForbiddenException;

    SurveyInteractionDTO continueSurvey(UUID surveySessionId) throws ObjectNotFoundException, ForbiddenException;

    EntityListResponse<SurveyDTO> getSurveysPaged(String name, Boolean getExpired, Integer count, Integer page, Integer start, String sortField, String sortDir, boolean calculate);

    Survey saveEntireSurvey(SurveyDTO surveyDTO, AuthorizedUser user)
            throws ObjectNotFoundException, LogicDependencyException, ResultDependencyException, BadDTOException;

    Survey saveSurvey(SurveyDTO dto, AuthorizedUser user) throws ObjectNotFoundException;

    SurveyDTO getSurvey(UUID surveyId, boolean entire) throws ObjectNotFoundException;

    SurveyDTO getSurveyByExternalId(String externalId, boolean entire) throws ObjectNotFoundException;

    void deleteSurvey(UUID id, boolean deleteAnswers) throws LogicDependencyException, ResultDependencyException;

    EntityListResponse<SurveyAnswerVariantDTO> getSurveyAnswerVariantsPaged(UUID surveyQuestionId, String description, Integer count, Integer page,
                                                                            Integer start, String sortField, String sortDir, boolean calculate);

    SurveyAnswerVariant saveSurveyAnswerVariant(SurveyAnswerVariantDTO dto) throws ObjectNotFoundException;

    SurveyAnswerVariantDTO getSurveyAnswerVariant(UUID id) throws ObjectNotFoundException;

    void deleteSurveyAnswerVariant(UUID id, boolean deleteLogic, boolean deleteAnswers) throws LogicDependencyException, ResultDependencyException;

    EntityListResponse<SurveyPageDTO> getSurveyPagesPaged(UUID surveyId, String description, Integer count, Integer page,
                                                          Integer start, String sortField, String sortDir, boolean calculate);

    SurveyPage saveSurveyPage(SurveyPageDTO dto) throws ObjectNotFoundException;

    SurveyPageDTO getSurveyPage(UUID id) throws ObjectNotFoundException;

    void deleteSurveyPage(UUID id, boolean deleteAnswers) throws LogicDependencyException, ResultDependencyException;

    EntityListResponse<SurveyQuestionDTO> getSurveyQuestionsPaged(UUID surveyId, UUID surveyPageId, Set<UUID> questionTypes,
                                                                  String text, boolean withVariants, Integer count, Integer page,
                                                                  Integer start, String sortField, String sortDir, boolean calculate);

    SurveyQuestion saveSurveyQuestion(SurveyQuestionDTO dto) throws ObjectNotFoundException, BadDTOException;

    SurveyQuestionDTO getSurveyQuestion(UUID surveyQuestionId) throws ObjectNotFoundException;

    void deleteSurveyQuestion(UUID id, boolean deleteLogic, boolean deleteAnswers) throws LogicDependencyException, ResultDependencyException;

    EntityListResponse<SurveyLogicTriggerDTO> getSurveyLogicTriggersPaged(UUID surveyQuestionId, UUID surveyPageId, UUID surveyAnswerVariantId, Integer count, Integer page,
                                                                          Integer start, String sortField, String sortDir, boolean calculate);

    SurveyLogicTrigger saveSurveyLogicTrigger(SurveyLogicTriggerDTO dto) throws ObjectNotFoundException, BadDTOException;

    SurveyLogicTriggerDTO getSurveyLogicTrigger(UUID id) throws ObjectNotFoundException;

    void deleteSurveyLogicTrigger(UUID id);

    EntityListResponse<SurveySessionDTO> getSurveySessionsPaged(UUID surveyId, UUID userId, String lastVisitIP, Integer count, Integer page,
                                                                Integer start, String sortField, String sortDir, boolean calculate);

    SurveySession saveSurveySession(SurveySessionDTO dto) throws ObjectNotFoundException;

    SurveySessionDTO getSurveySession(UUID id) throws ObjectNotFoundException;

    void deleteSurveySession(UUID id);

    EntityListResponse<SurveyQuestionAnswerDTO> getSurveyQuestionAnswersPaged(UUID surveySessionId, Integer count, Integer page,
                                                                              Integer start, String sortField, String sortDir, boolean calculate);

    SurveyQuestionAnswer saveSurveyQuestionAnswer(SurveyQuestionAnswerDTO dto) throws ObjectNotFoundException;

    SurveyQuestionAnswerDTO getSurveyQuestionAnswer(UUID id) throws ObjectNotFoundException;

    void deleteSurveyQuestionAnswer(UUID id);

    EntityListResponse<SurveyAnswerVariantCatalogDTO> getCatalogsPaged(String name, Integer count, Integer page,
                                                                       Integer start, String sortField, String sortDir, boolean calculate);

    SurveyAnswerVariantCatalogDTO getCatalog(UUID id) throws ObjectNotFoundException;

    SurveyAnswerVariantCatalog saveCatalog(SurveyAnswerVariantCatalogDTO dto) throws ObjectNotFoundException;

    void deleteCatalog(UUID id);

    EntityListResponse<SurveyAnswerVariantCatalogItemDTO> getCatalogItemsPaged(UUID catalogId, String text, Integer count, Integer page,
                                                                               Integer start, String sortField, String sortDir, boolean calculate);

    SurveyAnswerVariantCatalogItemDTO getCatalogItem(UUID id) throws ObjectNotFoundException;

    SurveyAnswerVariantCatalogItem saveCatalogItem(SurveyAnswerVariantCatalogItemDTO dto) throws ObjectNotFoundException;

    void deleteCatalogItem(UUID id);
}
