package com.gracelogic.platform.survey.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.filestorage.model.StoredFile;
import com.gracelogic.platform.survey.dto.admin.*;
import com.gracelogic.platform.survey.dto.user.PageAnswersDTO;
import com.gracelogic.platform.survey.dto.user.SurveyConclusionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyInteractionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyIntroductionDTO;
import com.gracelogic.platform.survey.exception.RespondentLimitException;
import com.gracelogic.platform.survey.exception.ResultDependencyException;
import com.gracelogic.platform.survey.exception.LogicDependencyException;
import com.gracelogic.platform.survey.model.*;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.ForbiddenException;
import com.gracelogic.platform.user.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SurveyServiceImpl implements SurveyService {

    private static Logger logger = Logger.getLogger(SurveyServiceImpl.class);

    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private DictionaryService ds;

    private enum LogicTriggerCheckItem {
        QUESTION,
        ANSWER,
    }

    private static HashMap<SurveyQuestion, List<SurveyAnswerVariant>> asListAnswerVariantHashMap(List<SurveyAnswerVariant> list) {
        HashMap<SurveyQuestion, List<SurveyAnswerVariant>> hashMap = new HashMap<>();

        for (SurveyAnswerVariant variant : list) {
            List<SurveyAnswerVariant> variantList = hashMap.get(variant.getSurveyQuestion());
            if (variantList != null) {
                variantList.add(variant);
                continue;
            }
            variantList = new LinkedList<>();
            variantList.add(variant);
            hashMap.put(variant.getSurveyQuestion(), variantList);
        }

        return hashMap;
    }

    private static HashMap<SurveyQuestion, SurveyAnswerVariant> asAnswerVariantHashMap(List<SurveyAnswerVariant> list) {
        HashMap<SurveyQuestion, SurveyAnswerVariant> hashMap = new HashMap<>();
        for (SurveyAnswerVariant answerVariant : list) {
            hashMap.put(answerVariant.getSurveyQuestion(), answerVariant);
        }
        return hashMap;
    }

    private static HashMap<SurveyQuestion, List<SurveyLogicTrigger>> asLogicTriggerListHashMap(List<SurveyLogicTrigger> list) {
        HashMap<SurveyQuestion, List<SurveyLogicTrigger>> hashMap = new HashMap<>();
        for (SurveyLogicTrigger variant : list) {
            List<SurveyLogicTrigger> triggerList = hashMap.get(variant.getSurveyQuestion());
            if (triggerList != null) {
                triggerList.add(variant);
                continue;
            }
            triggerList = new LinkedList<>();
            triggerList.add(variant);
            hashMap.put(variant.getSurveyQuestion(), triggerList);
        }
        return hashMap;
    }

    @Override
    public SurveyIntroductionDTO getSurveyIntroduction(UUID surveyId)
            throws ObjectNotFoundException {
        Survey survey = idObjectService.getObjectById(Survey.class, surveyId);
        if (survey == null) {
            throw new ObjectNotFoundException();
        }

        return new SurveyIntroductionDTO(survey);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyInteractionDTO startSurvey(UUID surveyId, AuthorizedUser user, String ipAddress)
            throws ObjectNotFoundException, RespondentLimitException, ForbiddenException {
        Survey survey = idObjectService.getObjectById(Survey.class, surveyId);
        if (survey == null) {
            throw new ObjectNotFoundException();
        }

        Date now = new Date();

        if (survey.getExpirationDate() != null && survey.getExpirationDate().before(now))
            throw new ForbiddenException();

        if (user == null && survey.getSurveyParticipationType().getId().equals(DataConstants.ParticipationTypes.AUTHORIZATION_REQUIRED.getValue())) {
            throw new ForbiddenException();
        }

        //Check max attempts
        if (survey.getMaxAttempts() != null && survey.getMaxAttempts() > 0) {
            Map<String, Object> params = new HashMap<>();
            String cause = "el.survey.id=:surveyId ";
            params.put("surveyId", survey.getId());
            if (user != null) {
                cause += "and el.user.id=:userId ";
                params.put("userId", user.getId());
            } else {
                cause += "and el.lastVisitIP=:ip ";
                params.put("ip", ipAddress);
            }

            Integer passesFromThisIP = idObjectService.checkExist(SurveySession.class, null, cause, params, survey.getMaxAttempts() + 1);

            if (passesFromThisIP >= survey.getMaxAttempts()) {
                throw new ForbiddenException();
            }
        }

        //Check max respondents
        if (survey.getMaxRespondents() != null && survey.getMaxRespondents() > 0) {
            Map<String, Object> params = new HashMap<>();
            String cause = "el.survey.id=:surveyId ";
            params.put("surveyId", survey.getId());

            Integer totalPasses = idObjectService.checkExist(SurveySession.class, null, cause,
                    params, survey.getMaxRespondents() + 1);
            if (totalPasses >= survey.getMaxRespondents()) {
                throw new RespondentLimitException();
            }
        }

        SurveySession surveySession = new SurveySession();
        surveySession.setStarted(now);
        surveySession.setLastVisitIP(ipAddress);
        if (user != null) {
            surveySession.setUser(idObjectService.getObjectById(User.class, user.getId()));
        }

        if (survey.getTimeLimit() != null && survey.getTimeLimit() > 0) {
            surveySession.setExpirationDate(new Date(now.getTime() + survey.getTimeLimit()));
        }

        surveySession.setLastVisitedPageIndex(0);
        surveySession.setLink(survey.getLink());
        surveySession.setConclusion(survey.getConclusion());
        surveySession.setSurvey(survey);
        idObjectService.save(surveySession);

        SurveyInteractionDTO surveyInteractionDTO = new SurveyInteractionDTO();
        surveyInteractionDTO.setSurveySessionId(surveySession.getId());
        surveyInteractionDTO.setSurveyPage(getSurveyPage(surveySession, 0));
        return surveyInteractionDTO;
    }

    private SurveyPageDTO getSurveyPage(SurveySession surveySession, int pageIndex) throws ObjectNotFoundException {
        Map<String, Object> params = new HashMap<>();
        String cause = "el.survey.id=:surveyId ";
        params.put("surveyId", surveySession.getSurvey().getId());
        params.put("pageIndex", pageIndex);

        List<SurveyPage> surveyPages = idObjectService.getList(SurveyPage.class, null, cause, params, null, null, null, 1);
        if (surveyPages.isEmpty()) {
            throw new ObjectNotFoundException();
        }

        SurveyPage surveyPage = surveyPages.iterator().next();
        SurveyPageDTO dto = SurveyPageDTO.prepare(surveyPage);

        // 1. Получение списка вопросов текущей страницы
        params.clear();
        cause = "el.surveyPage.id=:surveyPageId ";
        params.put("surveyPageId", surveyPage.getId());

        List<SurveyQuestion> questions = idObjectService.getList(SurveyQuestion.class, null,
                cause, params, "el.questionIndex", "ASC", null, null);


        Set<UUID> questionIds = new HashSet<>();
        for (SurveyQuestion question : questions) {
            questionIds.add(question.getId());
        }

        if (!questionIds.isEmpty()) {
            // 2. Получение логики. Для веба выбор только HIDE_QUESTION/SHOW_QUESTION
            params.clear();
            cause = "el.surveyQuestion.id in (:questionIds) AND el.logicActionType.id in (:logicActionTypeIds) ";
            params.put("questionIds", questionIds);
            Set<UUID> logicActionTypeIds = new HashSet<>();
            logicActionTypeIds.add(DataConstants.LogicActionTypes.HIDE_QUESTION.getValue());
            logicActionTypeIds.add(DataConstants.LogicActionTypes.SHOW_QUESTION.getValue());
            params.put("logicActionTypeIds", logicActionTypeIds);
            HashMap<SurveyQuestion, List<SurveyLogicTrigger>> logicHashMap = asLogicTriggerListHashMap(idObjectService.getList(SurveyLogicTrigger.class, null,
                    cause, params, null, null, null));

            // 3. Получение вариантов ответа
            params.clear();
            cause = "el.surveyQuestion.id in (:questionIds) ";
            params.put("questionIds", questionIds);

            HashMap<SurveyQuestion, List<SurveyAnswerVariant>> answerVariants = asListAnswerVariantHashMap(idObjectService.getList(SurveyAnswerVariant.class, null,
                    cause, params, "el.sortOrder", "ASC", null, null));

            List<SurveyQuestionDTO> surveyQuestionDTOs = new LinkedList<>();
            for (SurveyQuestion question : questions) {
                SurveyQuestionDTO surveyQuestionDTO = SurveyQuestionDTO.prepare(question);

                List<SurveyAnswerVariantDTO> answerVariantsDTO = new LinkedList<>();
                for (SurveyAnswerVariant answerVariant : answerVariants.get(question)) {
                    answerVariantsDTO.add(SurveyAnswerVariantDTO.prepare(answerVariant));
                }

                surveyQuestionDTO.setLogicTriggers(new LinkedList<SurveyLogicTriggerDTO>());

                for (SurveyLogicTrigger trigger : logicHashMap.get(question)) {
                    surveyQuestionDTO.getLogicTriggers().add(SurveyLogicTriggerDTO.prepare(trigger));
                }
                surveyQuestionDTO.setAnswers(answerVariantsDTO);
                surveyQuestionDTOs.add(surveyQuestionDTO);
            }

            dto.setQuestions(surveyQuestionDTOs);
        }

        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyInteractionDTO getSurveyPage(UUID surveySessionId, int pageIndex)
            throws ObjectNotFoundException, ForbiddenException {
        SurveySession surveySession = idObjectService.getObjectById(SurveySession.class, surveySessionId);

        if (surveySession == null) {
            throw new ObjectNotFoundException();
        }
        if (surveySession.getEnded() != null) {
            throw new ForbiddenException();
        }
        if (surveySession.getExpirationDate() != null && surveySession.getExpirationDate().before(new Date())) {
            throw new ForbiddenException();
        }

        surveySession.setLastVisitedPageIndex(pageIndex);
        idObjectService.save(surveySession);

        SurveyInteractionDTO surveyInteractionDTO = new SurveyInteractionDTO();
        surveyInteractionDTO.setSurveySessionId(surveySession.getId());
        surveyInteractionDTO.setSurveyPage(getSurveyPage(surveySession, pageIndex));

        return surveyInteractionDTO;
    }

    @Override
    public SurveyInteractionDTO continueSurvey(UUID surveySessionId) throws ObjectNotFoundException, ForbiddenException {
        SurveySession surveySession = idObjectService.getObjectById(SurveySession.class, surveySessionId);
        if (surveySession == null) {
            throw new ObjectNotFoundException();
        }
        if (surveySession.getEnded() != null) {
            throw new ForbiddenException();
        }
        if (surveySession.getExpirationDate() != null && surveySession.getExpirationDate().before(new Date())) {
            throw new ForbiddenException();
        }

        SurveyInteractionDTO surveyInteractionDTO = new SurveyInteractionDTO();
        surveyInteractionDTO.setSurveySessionId(surveySession.getId());
        surveyInteractionDTO.setSurveyPage(getSurveyPage(surveySession, surveySession.getLastVisitedPageIndex()));

        return surveyInteractionDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyInteractionDTO saveAnswersAndContinue(UUID surveySessionId, PageAnswersDTO dto) throws ObjectNotFoundException, ForbiddenException {
        final SurveySession surveySession = idObjectService.getObjectById(SurveySession.class, surveySessionId);

        if (surveySession == null) throw new ObjectNotFoundException();
        if (surveySession.getEnded() != null) throw new ForbiddenException();
        if (surveySession.getExpirationDate() != null && surveySession.getExpirationDate().before(new Date()))
            throw new ForbiddenException();

        boolean finishSurvey = false;

        int nextPage = surveySession.getLastVisitedPageIndex() + 1;

        // список вопросов от полученных ответов
        Map<String, Object> params = new HashMap<>();
        params.put("surveyPageId", dto.getSurveyPageId());

        List<SurveyQuestion> surveyQuestions = idObjectService.getList(SurveyQuestion.class, null,
                "el.surveyPage.id=:surveyPageId",
                params, "el.questionIndex", "ASC", null, null);

        // список вариантов ответов
        HashMap<SurveyQuestion, SurveyAnswerVariant> surveyAnswersHashMap = new HashMap<>();

        if (dto.containsNonTextAnswers()) {
            params.clear();
            params.put("answerIds", dto.getAnswers().keySet());
            surveyAnswersHashMap = asAnswerVariantHashMap(
                    idObjectService.getList(SurveyAnswerVariant.class, null,
                            "el.id in (:answerIds)", params, null, null, null));
        }

        params.clear();
        params.put("surveyPageId", dto.getSurveyPageId());
        HashMap<SurveyQuestion, List<SurveyLogicTrigger>> triggersHashMap = asLogicTriggerListHashMap(
                idObjectService.getList(SurveyLogicTrigger.class, null,
                        "el.surveyPage.id=:surveyPageId",
                        params, null, null, null));

        List<SurveyLogicTrigger> pageTriggers = triggersHashMap.get(null);

        for (SurveyLogicTrigger trigger : pageTriggers) {
            if (trigger.getSurveyLogicActionType().getId() == DataConstants.LogicActionTypes.GO_TO_PAGE.getValue()) {
                nextPage = trigger.getPageIndex();
            }

            if (!finishSurvey) {
                finishSurvey = trigger.getSurveyLogicActionType().getId().equals(DataConstants.LogicActionTypes.END_SURVEY.getValue());
            }
        }

        for (SurveyQuestion question : surveyQuestions) {
            SurveyAnswerVariant answerVariant = surveyAnswersHashMap.get(question);
            String textAnswer = dto.getAnswers().get(question.getId()).getTextAnswer();

            for (SurveyLogicTrigger trigger : triggersHashMap.get(question)) {
                LogicTriggerCheckItem logicTriggerCheckItem = LogicTriggerCheckItem.ANSWER;
                if (trigger.getAnswerVariant() == null) logicTriggerCheckItem = LogicTriggerCheckItem.QUESTION;

                boolean triggered = false;

                switch (logicTriggerCheckItem) {
                    case ANSWER:
                        boolean selectedTrigger = trigger.isInteractionRequired() && trigger.getAnswerVariant() == answerVariant;
                        boolean unselectedTrigger = !trigger.isInteractionRequired() && answerVariant != null && trigger.getAnswerVariant() != answerVariant;

                        triggered = selectedTrigger || unselectedTrigger;
                        break;
                    case QUESTION:
                        boolean answeredTrigger = trigger.isInteractionRequired() && (answerVariant != null || StringUtils.isNotBlank(textAnswer));
                        boolean unansweredTrigger = !trigger.isInteractionRequired() && answerVariant == null && StringUtils.isBlank(textAnswer);

                        triggered = answeredTrigger || unansweredTrigger;
                        break;
                }

                if (triggered) {
                    if (trigger.getSurveyLogicActionType().getId().equals(DataConstants.LogicActionTypes.CHANGE_CONCLUSION.getValue())) {
                        surveySession.setConclusion(trigger.getNewConclusion());
                    }

                    if (trigger.getSurveyLogicActionType().getId().equals(DataConstants.LogicActionTypes.CHANGE_LINK.getValue())) {
                        surveySession.setLink(trigger.getNewLink());
                    }

                    if (trigger.getSurveyLogicActionType().getId().equals(DataConstants.LogicActionTypes.GO_TO_PAGE.getValue())) {
                        nextPage = trigger.getPageIndex();
                    }

                    if (!finishSurvey) {
                        finishSurvey = trigger.getSurveyLogicActionType().getId().equals(DataConstants.LogicActionTypes.END_SURVEY.getValue());
                    }
                }
            }

            SurveyQuestionAnswer surveyQuestionAnswer = new SurveyQuestionAnswer(surveySession,
                    question, answerVariant, textAnswer,
                    null); // TODO: stored file

            idObjectService.save(surveyQuestionAnswer);
        }

        // если следующей страницы не существует, это финиш
        params.clear();
        params.put("surveyId", surveySession.getSurvey().getId());
        params.put("pageIndex", nextPage);
        if (!finishSurvey && idObjectService.checkExist(SurveyPage.class, null, "el.survey.id=:surveyId and el.pageIndex=:pageIndex",
                params, 1) == 0) {
            finishSurvey = true;
        }

        SurveyInteractionDTO surveyInteractionDTO = new SurveyInteractionDTO();
        if (finishSurvey) {
            SurveyConclusionDTO surveyConclusionDTO = new SurveyConclusionDTO();
            surveyConclusionDTO.setConclusion(surveySession.getConclusion());
            surveyConclusionDTO.setLink(surveySession.getLink());
            surveyInteractionDTO.setSurveyConclusion(surveyConclusionDTO);

            surveySession.setEnded(new Date());
        } else {
            surveyInteractionDTO.setSurveyPage(getSurveyPage(surveySession, nextPage));
            surveySession.setLastVisitedPageIndex(nextPage);
        }

        idObjectService.save(surveySession);
        return surveyInteractionDTO;
    }

    @Override
    public EntityListResponse<SurveyDTO> getSurveysPaged(String name, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (!StringUtils.isEmpty(name)) {
            params.put("name", "%%" + StringUtils.lowerCase(name) + "%%");
            cause += "and lower(el.name) like :name ";
        }

        int totalCount = idObjectService.getCount(Survey.class, null, countFetches, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<SurveyDTO> entityListResponse = new EntityListResponse<SurveyDTO>();
        entityListResponse.setEntity("survey");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<Survey> items = idObjectService.getList(Survey.class, null, cause, params, sortField, sortDir, startRecord, count);

        entityListResponse.setPartCount(items.size());
        for (Survey e : items) {
            SurveyDTO el = SurveyDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Survey saveSurvey(SurveyDTO dto, AuthorizedUser user) throws ObjectNotFoundException {
        Survey entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(Survey.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new Survey();
        }

        entity.setName(dto.getName());
        entity.setExpirationDate(dto.getExpirationDate());
        entity.setShowProgress(dto.getShowProgress());
        entity.setShowQuestionNumber(dto.getShowQuestionNumber());
        entity.setAllowReturn(dto.getAllowReturn());
        entity.setIntroduction(dto.getIntroduction());
        entity.setConclusion(dto.getConclusion());
        entity.setMaxRespondents(dto.getMaxRespondents());
        entity.setTimeLimit(dto.getTimeLimit());
        entity.setMaxAttempts(dto.getMaxAttempts());
        entity.setSurveyParticipationType(ds.get(SurveyParticipationType.class, dto.getParticipationTypeId()));
        entity.setOwner(idObjectService.getObjectById(User.class, user.getId()));
        return idObjectService.save(entity);
    }


    @Override
    public SurveyDTO getSurvey(UUID surveyId) throws ObjectNotFoundException {
        Survey entity = idObjectService.getObjectById(Survey.class, surveyId);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }
        return SurveyDTO.prepare(entity);
    }

    @Override
    public EntityListResponse<SurveyAnswerVariantDTO> getSurveyAnswerVariantsPaged(UUID surveyQuestionId, String description, Integer count, Integer page,
                                                                                   Integer start, String sortField, String sortDir) {
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (surveyQuestionId != null) {
            cause += "and el.surveyQuestion.id=:surveyQuestionId ";
            params.put("surveyQuestionId", surveyQuestionId);
        }
        if (!StringUtils.isEmpty(description)) {
            params.put("description", "%%" + StringUtils.lowerCase(description) + "%%");
            cause += "and lower(el.description) like :description ";
        }

        int totalCount = idObjectService.getCount(SurveyAnswerVariant.class, null, null, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<SurveyAnswerVariantDTO> entityListResponse = new EntityListResponse<>();
        entityListResponse.setEntity("surveyAnswerVariant");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<SurveyAnswerVariant> items = idObjectService.getList(SurveyAnswerVariant.class, null, cause,
                params, sortField, sortDir, startRecord, count);

        entityListResponse.setPartCount(items.size());
        for (SurveyAnswerVariant e : items) {
            SurveyAnswerVariantDTO el = SurveyAnswerVariantDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyAnswerVariant saveSurveyAnswerVariant(SurveyAnswerVariantDTO dto) throws ObjectNotFoundException {
        SurveyAnswerVariant entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(SurveyAnswerVariant.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new SurveyAnswerVariant();
        }

        entity.setDefaultVariant(dto.getDefaultVariant());
        entity.setSortOrder(dto.getSortOrder());
        entity.setSurveyQuestion(idObjectService.getObjectById(SurveyQuestion.class, dto.getSurveyQuestionId()));
        entity.setText(dto.getText());
        entity.setWeight(dto.getWeight());

        return idObjectService.save(entity);
    }


    @Override
    public SurveyAnswerVariantDTO getSurveyAnswerVariant(UUID id) throws ObjectNotFoundException {
        SurveyAnswerVariant entity = idObjectService.getObjectById(SurveyAnswerVariant.class, id);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }
        return SurveyAnswerVariantDTO.prepare(entity);
    }

    @Override
    public EntityListResponse<SurveyPageDTO> getSurveyPagesPaged(UUID surveyId, String description, Integer count, Integer page,
                                                                 Integer start, String sortField, String sortDir) {
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (surveyId != null) {
            cause += "and el.survey.id=:surveyId ";
            params.put("surveyId", surveyId);
        }

        if (!StringUtils.isEmpty(description)) {
            params.put("description", "%%" + StringUtils.lowerCase(description) + "%%");
            cause += "and lower(el.description) like :description ";
        }

        int totalCount = idObjectService.getCount(SurveyPage.class, null, countFetches, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<SurveyPageDTO> entityListResponse = new EntityListResponse<SurveyPageDTO>();
        entityListResponse.setEntity("surveyPage");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<SurveyPage> items = idObjectService.getList(SurveyPage.class, null, cause, params, sortField, sortDir, startRecord, count);

        entityListResponse.setPartCount(items.size());
        for (SurveyPage e : items) {
            SurveyPageDTO el = SurveyPageDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyPage saveSurveyPage(SurveyPageDTO dto) throws ObjectNotFoundException {
        SurveyPage entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(SurveyPage.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new SurveyPage();
        }

        entity.setDescription(dto.getDescription());
        entity.setPageIndex(dto.getPageIndex());
        entity.setSurvey(idObjectService.getObjectById(Survey.class, dto.getSurveyId()));
        return idObjectService.save(entity);
    }


    @Override
    public SurveyPageDTO getSurveyPage(UUID surveyPageId) throws ObjectNotFoundException {
        SurveyPage entity = idObjectService.getObjectById(SurveyPage.class, surveyPageId);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }
        return SurveyPageDTO.prepare(entity);
    }

    @Override
    public EntityListResponse<SurveyQuestionDTO> getSurveyQuestionsPaged(UUID surveyPageId, String text, Integer count, Integer page,
                                                                         Integer start, String sortField, String sortDir) {
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (surveyPageId != null) {
            cause += "and el.surveyPage.id=:surveyPageId ";
            params.put("surveyPageId", surveyPageId);
        }

        if (!StringUtils.isEmpty(text)) {
            params.put("text", "%%" + StringUtils.lowerCase(text) + "%%");
            cause += "and lower(el.text) like :text ";
        }

        int totalCount = idObjectService.getCount(SurveyQuestion.class, null, countFetches, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<SurveyQuestionDTO> entityListResponse = new EntityListResponse<SurveyQuestionDTO>();
        entityListResponse.setEntity("surveyQuestion");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<SurveyQuestion> items = idObjectService.getList(SurveyQuestion.class, null, cause, params, sortField, sortDir, startRecord, count);

        entityListResponse.setPartCount(items.size());
        for (SurveyQuestion e : items) {
            SurveyQuestionDTO el = SurveyQuestionDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyQuestion saveSurveyQuestion(SurveyQuestionDTO dto) throws ObjectNotFoundException {
        SurveyQuestion entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(SurveyQuestion.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new SurveyQuestion();
        }

        entity.setQuestionIndex(dto.getQuestionIndex());
        entity.setHidden(dto.getHidden());
        entity.setRequired(dto.getRequired());
        entity.setSurveyPage(idObjectService.getObjectById(SurveyPage.class, dto.getSurveyPageId()));
        entity.setText(dto.getText());
        entity.setSurveyQuestionType(ds.get(SurveyQuestionType.class, dto.getSurveyQuestionTypeId()));
        entity.setScaleMinValue(dto.getScaleMinValue());
        entity.setScaleMaxValue(dto.getScaleMaxValue());
        entity.setAttachmentExtensions(dto.getAttachmentExtensions());
        return idObjectService.save(entity);
    }


    @Override
    public SurveyQuestionDTO getSurveyQuestion(UUID surveyQuestionId) throws ObjectNotFoundException {
        SurveyQuestion entity = idObjectService.getObjectById(SurveyQuestion.class, surveyQuestionId);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }
        return SurveyQuestionDTO.prepare(entity);
    }

    @Override
    public EntityListResponse<SurveyLogicTriggerDTO> getSurveyLogicTriggersPaged(UUID surveyQuestionId, UUID surveyPageId, UUID surveyAnswerVariantId, Integer count, Integer page,
                                                                                 Integer start, String sortField, String sortDir) {
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (surveyPageId != null) {
            cause += "and el.surveyPage.id=:surveyPageId ";
            params.put("surveyPageId", surveyPageId);
        }
        if (surveyQuestionId != null) {
            cause += "and el.surveyQuestion.id=:surveyQuestionId ";
            params.put("surveyQuestionId", surveyQuestionId);
        }
        if (surveyQuestionId != null) {
            cause += "and el.surveyAnswerVariant.id=:surveyAnswerVariantId ";
            params.put("surveyAnswerVariantId", surveyAnswerVariantId);
        }

        int totalCount = idObjectService.getCount(SurveyLogicTrigger.class, null, countFetches, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<SurveyLogicTriggerDTO> entityListResponse = new EntityListResponse<SurveyLogicTriggerDTO>();
        entityListResponse.setEntity("surveyVariantLogic");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<SurveyLogicTrigger> items = idObjectService.getList(SurveyLogicTrigger.class, null, cause, params, sortField, sortDir, startRecord, count);

        entityListResponse.setPartCount(items.size());
        for (SurveyLogicTrigger e : items) {
            SurveyLogicTriggerDTO el = SurveyLogicTriggerDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyLogicTrigger saveSurveyLogicTrigger(SurveyLogicTriggerDTO dto) throws ObjectNotFoundException {
        SurveyLogicTrigger entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(SurveyLogicTrigger.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new SurveyLogicTrigger();
        }
        entity.setSurveyPage(idObjectService.getObjectById(SurveyPage.class, dto.getSurveyPageId()));
        entity.setSurveyQuestion(idObjectService.getObjectById(SurveyQuestion.class, dto.getSurveyQuestionId()));
        entity.setAnswerVariant(idObjectService.getObjectById(SurveyAnswerVariant.class, dto.getAnswerVariantId()));
        entity.setSurveyLogicActionType(ds.get(SurveyLogicActionType.class, dto.getLogicActionTypeId()));
        entity.setNewConclusion(dto.getNewConclusion());
        entity.setNewLink(dto.getNewLink());
        entity.setPageIndex(dto.getPageIndex());
        entity.setInteractionRequired(dto.isInteractionRequired());
        entity.setTargetQuestion(idObjectService.getObjectById(SurveyQuestion.class, dto.getTargetQuestionId()));

        return idObjectService.save(entity);
    }

    @Override
    public SurveyLogicTriggerDTO getSurveyLogicTrigger(UUID id) throws ObjectNotFoundException {
        SurveyLogicTrigger entity = idObjectService.getObjectById(SurveyLogicTrigger.class, id);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }
        return SurveyLogicTriggerDTO.prepare(entity);
    }

    @Override
    public EntityListResponse<SurveySessionDTO> getSurveySessionsPaged(UUID surveyId, UUID userId, String lastVisitIP,
                                                                       Integer count, Integer page, Integer start,
                                                                       String sortField, String sortDir) {
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (!StringUtils.isEmpty(lastVisitIP)) {
            params.put("ip", "%%" + lastVisitIP + "%%");
            cause += "and el.lastVisitIP like :ip ";
        }

        if (userId != null) {
            cause += "and el.user.id=:userId ";
            params.put("userId", userId);
        }

        if (surveyId != null) {
            cause += "and el.survey.id=:surveyId ";
            params.put("surveyId", surveyId);
        }

        int totalCount = idObjectService.getCount(SurveySession.class, null, countFetches, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<SurveySessionDTO> entityListResponse = new EntityListResponse<SurveySessionDTO>();
        entityListResponse.setEntity("surveySession");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<SurveySession> items = idObjectService.getList(SurveySession.class, null, cause, params, sortField, sortDir, startRecord, count);

        entityListResponse.setPartCount(items.size());
        for (SurveySession e : items) {
            SurveySessionDTO el = SurveySessionDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveySession saveSurveySession(SurveySessionDTO dto) throws ObjectNotFoundException {
        SurveySession entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(SurveySession.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new SurveySession();
        }

        entity.setUser(idObjectService.getObjectById(User.class, dto.getUserId()));
        entity.setLastVisitIP(dto.getLastVisitIP());
        entity.setSurvey(idObjectService.getObjectById(Survey.class, dto.getSurveyId()));
        entity.setStarted(dto.getStarted());
        entity.setEnded(dto.getEnded());
        entity.setExpirationDate(dto.getExpirationDate());
        entity.setLastVisitedPageIndex(dto.getLastVisitedPageIndex());

        return idObjectService.save(entity);
    }

    @Override
    public SurveySessionDTO getSurveySession(UUID id) throws ObjectNotFoundException {
        SurveySession entity = idObjectService.getObjectById(SurveySession.class, id);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }
        return SurveySessionDTO.prepare(entity);
    }

    @Override
    public EntityListResponse<SurveyQuestionAnswerDTO> getSurveyQuestionAnswersPaged(UUID surveySessionId,
                                                                                     Integer count, Integer page, Integer start,
                                                                                     String sortField, String sortDir) {
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (surveySessionId != null) {
            cause += String.format("and el.surveySession = '%s' ", surveySessionId);
        }

        int totalCount = idObjectService.getCount(SurveySession.class, null, countFetches, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<SurveyQuestionAnswerDTO> entityListResponse = new EntityListResponse<SurveyQuestionAnswerDTO>();
        entityListResponse.setEntity("surveyQuestionAnswer");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<SurveyQuestionAnswer> items = idObjectService.getList(SurveyQuestionAnswer.class, null, cause, params,
                sortField, sortDir, startRecord, count);

        entityListResponse.setPartCount(items.size());
        for (SurveyQuestionAnswer e : items) {
            SurveyQuestionAnswerDTO el = SurveyQuestionAnswerDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyQuestionAnswer saveSurveyQuestionAnswer(SurveyQuestionAnswerDTO dto) throws ObjectNotFoundException {
        SurveyQuestionAnswer entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(SurveyQuestionAnswer.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new SurveyQuestionAnswer();
        }

        entity.setSurveySession(idObjectService.getObjectById(SurveySession.class, dto.getSurveySessionId()));
        entity.setQuestion(idObjectService.getObjectById(SurveyQuestion.class, dto.getQuestionId()));
        entity.setAnswerVariant(idObjectService.getObjectById(SurveyAnswerVariant.class, dto.getAnswerVariantId()));
        entity.setText(dto.getText());
        entity.setStoredFile(idObjectService.getObjectById(StoredFile.class, dto.getStoredFile()));

        return idObjectService.save(entity);
    }

    @Override
    public SurveyQuestionAnswerDTO getSurveyQuestionAnswer(UUID id) throws ObjectNotFoundException {
        SurveyQuestionAnswer entity = idObjectService.getObjectById(SurveyQuestionAnswer.class, id);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }
        return SurveyQuestionAnswerDTO.prepare(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurvey(UUID id) throws LogicDependencyException, ResultDependencyException {
        Map<String, Object> params = new HashMap<>();
        params.put("surveyId", id);
        List<SurveyPage> pages = idObjectService.getList(SurveyPage.class, null,
                "el.survey.id=:surveyId", params, null, null, null);

        for (SurveyPage page : pages) {
            deleteSurveyPage(page.getId());
        }

        idObjectService.delete(Survey.class, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveyPage(UUID id) throws LogicDependencyException, ResultDependencyException {
        Map<String, Object> params = new HashMap<>();
        params.put("surveyPageId", id);

        List<SurveyQuestion> surveyQuestions = idObjectService.getList(SurveyQuestion.class, null,
                "el.surveyPage.id=:surveyPageId", params, null, null, null);

        for (SurveyQuestion question : surveyQuestions) {
            deleteSurveyQuestion(question.getId(), true); // если удаляется целая страница - подразумевается, что удаляется вся логика вместе с ней
        }

        idObjectService.delete(SurveyPage.class, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveyQuestion(UUID id, boolean deleteLogic) throws LogicDependencyException, ResultDependencyException {
        Map<String, Object> params = new HashMap<>();
        params.put("questionId", id);

        boolean isQuestionAnswered = idObjectService.checkExist(SurveyQuestionAnswer.class, null, "el.question.id=:questionId",
                params, 1) > 0;

        if (isQuestionAnswered) {
            throw new ResultDependencyException("Question is answered");
        }

        if (!deleteLogic) {
            boolean hasLogic = idObjectService.checkExist(SurveyLogicTrigger.class, null,
                    "el.surveyQuestion.id=:questionId or el.targetQuestion.id=:questionId", params, 1) > 0;

            if (hasLogic) {
                throw new LogicDependencyException("Question has corresponding logic");
            }
        } else {
            List<SurveyLogicTrigger> logicTriggers = idObjectService.getList(SurveyLogicTrigger.class, null,
                    "el.surveyQuestion.id=:questionId or el.targetQuestion.id=:questionId",
                    params, null, null, null);

            for (SurveyLogicTrigger trigger : logicTriggers) {
                deleteSurveyLogicTrigger(trigger.getId());
            }
        }

        List<SurveyAnswerVariant> answerVariants = idObjectService.getList(SurveyAnswerVariant.class, null,
                "el.surveyQuestion.id=:questionId", params, null, null, null);

        for (SurveyAnswerVariant var : answerVariants) {
            deleteSurveyAnswerVariant(var.getId(), deleteLogic);
        }

        idObjectService.delete(SurveyQuestion.class, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveyAnswerVariant(UUID id, boolean deleteLogic) throws LogicDependencyException, ResultDependencyException {
        Map<String, Object> params = new HashMap<>();
        params.put("answerVariantId", id);

        boolean isAnswered = idObjectService.checkExist(SurveyQuestionAnswer.class, null, "el.answerVariant.id=:answerVariantId",
               params, 1) > 0;
        if (isAnswered) {
            throw new ResultDependencyException("Selected as answer");
        }

        if (!deleteLogic) {
            boolean hasLogic = idObjectService.checkExist(SurveyLogicTrigger.class, null,
                    "el.answerVariant.id=:answerVariantId", params, 1) > 0;

            if (hasLogic) {
                throw new LogicDependencyException("answer has corresponding logic");
            }
        } else {
            List<SurveyLogicTrigger> logicTriggers = idObjectService.getList(SurveyLogicTrigger.class, null,
                    "el.answerVariant.id=:answerVariantId",
                    params, null, null, null);

            for (SurveyLogicTrigger trigger : logicTriggers) {
                deleteSurveyLogicTrigger(trigger.getId());
            }
        }

        idObjectService.delete(SurveyAnswerVariant.class, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveyLogicTrigger(UUID id) {
        idObjectService.delete(SurveyLogicTrigger.class, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveySession(UUID id) {
        Map<String, Object> params = new HashMap<>();
        params.put("surveySessionId", id);

        idObjectService.delete(SurveyQuestionAnswer.class, "el.surveySession.id=:surveySessionId", params);
        idObjectService.delete(SurveyLogicTrigger.class, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveyQuestionAnswer(UUID id) {
        idObjectService.delete(SurveyQuestionAnswer.class, id);
    }
}
