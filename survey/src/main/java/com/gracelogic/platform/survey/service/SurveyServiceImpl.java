package com.gracelogic.platform.survey.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.filestorage.model.StoredFile;
import com.gracelogic.platform.survey.dto.admin.*;
import com.gracelogic.platform.survey.dto.user.PageAnswersDTO;
import com.gracelogic.platform.survey.dto.user.SurveyConclusionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyInteractionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyIntroductionDTO;
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

@Service("surveyService")
public class SurveyServiceImpl implements SurveyService {

    private static Logger logger = Logger.getLogger(SurveyServiceImpl.class);

    @Autowired
    private IdObjectService idObjectService;

    private enum LogicTriggerCheckItem {
        QUESTION,
        ANSWER,
    }
    private static HashMap<SurveyQuestion, List<SurveyAnswerVariant>> asListAnswerVariantHashMap(List<SurveyAnswerVariant> list) {
        HashMap<SurveyQuestion, List<SurveyAnswerVariant>> hashMap = new HashMap<>();

        for (SurveyAnswerVariant variant : list) {
            List<SurveyAnswerVariant> variantList = hashMap.get(variant.getSurveyQuestion());
            if (variantList != null) {
                variantList.add(variant); continue;
            }
            variantList = new ArrayList<>();
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
                triggerList.add(variant); continue;
            }
            triggerList = new ArrayList<>();
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
    public SurveyInteractionDTO startSurvey(UUID surveyId, AuthorizedUser user, String remoteAddress)
            throws ObjectNotFoundException, ForbiddenException {
        Survey survey = idObjectService.getObjectById(Survey.class, surveyId);

        if (survey == null) {
             throw new ObjectNotFoundException();
        }

        if (survey.getExpirationDate() != null && survey.getExpirationDate().before(new Date())) throw new ForbiddenException();

        if (user == null && survey.getParticipationType() == DataConstants.ParticipationType.AUTHORIZATION_REQUIRED.getValue()) {
            throw new ForbiddenException();
        }

        SurveySession surveySession = new SurveySession();
        surveySession.setStarted(new Date());
        surveySession.setLastVisitIP(remoteAddress);
        if (user != null) {
            surveySession.setUser(idObjectService.getObjectById(User.class, user.getId()));
        }

        if (survey.getTimeLimit() != null && survey.getTimeLimit() > 0) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MILLISECOND, survey.getTimeLimit().intValue());
            surveySession.setExpirationDate(c.getTime());
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
        List<SurveyPage> surveyPages = idObjectService.getList(SurveyPage.class, null,
                String.format("el.survey = '%s' AND el.pageIndex = '%s'", surveySession.getSurvey().getId(), pageIndex), null, null, null, null, 1);
        if (surveyPages.size() <= 0) {
            throw new ObjectNotFoundException();
        }

        SurveyPage surveyPage = surveyPages.get(0);
        SurveyPageDTO dto = SurveyPageDTO.prepare(surveyPage);

        // 1. Получение списка вопросов текущей страницы
        List<SurveyQuestion> questions = idObjectService.getList(SurveyQuestion.class, null,
                String.format("el.surveyPage = '%s'", surveyPage.getId()), null, "el.questionIndex", null, null, null);

        String questionIds = "";

        int i = 0;
        for (SurveyQuestion question : questions) {
            questionIds += "'" + question.getId() + "'"; if (i + 1 != questions.size()) questionIds += ", "; i++;
        }

        // 2. Получение логики. Для веба выбор только HIDE_QUESTION/SHOW_QUESTION
        HashMap<SurveyQuestion, List<SurveyLogicTrigger>> logicHashMap = asLogicTriggerListHashMap(idObjectService.getList(SurveyLogicTrigger.class, null,
                String.format("el.surveyQuestion IN (%s) AND (el.logicType = '%s' OR el.logicType = '%s')", questionIds,
                        DataConstants.LogicActionType.HIDE_QUESTION.getValue(), DataConstants.LogicActionType.SHOW_QUESTION.getValue()), null, null, null, null));

        // 3. Получение вариантов ответа
        HashMap<SurveyQuestion, List<SurveyAnswerVariant>> answerVariants = asListAnswerVariantHashMap(idObjectService.getList(SurveyAnswerVariant.class, null,
                String.format("el.surveyQuestion IN (%s)", questionIds), null, "el.sortOrder",
                null, null, null));

        List<SurveyQuestionDTO> surveyQuestionDTOs = new ArrayList<>();
        for (SurveyQuestion question : questions) {
             SurveyQuestionDTO surveyQuestionDTO = SurveyQuestionDTO.prepare(question);

             List<SurveyAnswerVariantDTO> answerVariantsDTO = new ArrayList<>();
             for (SurveyAnswerVariant answerVariant : answerVariants.get(question)) {
                 answerVariantsDTO.add(SurveyAnswerVariantDTO.prepare(answerVariant));
             }

             surveyQuestionDTO.setLogicTriggers(new ArrayList<SurveyLogicTriggerDTO>());

             for (SurveyLogicTrigger trigger : logicHashMap.get(question)) {
                 surveyQuestionDTO.getLogicTriggers().add(SurveyLogicTriggerDTO.prepare(trigger));
             }
             surveyQuestionDTO.setAnswers(answerVariantsDTO);
             surveyQuestionDTOs.add(surveyQuestionDTO);
        }

        dto.setQuestions(surveyQuestionDTOs);

        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyInteractionDTO getSurveyPage(UUID surveySessionId, int pageIndex)
            throws ObjectNotFoundException, ForbiddenException {
        SurveySession surveySession = idObjectService.getObjectById(SurveySession.class, surveySessionId);

        if (surveySession == null) throw new ObjectNotFoundException();
        if (surveySession.getEnded() != null) throw new ForbiddenException();
        if (surveySession.getExpirationDate() != null && surveySession.getExpirationDate().before(new Date())) throw new ForbiddenException();

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
        if (surveySession == null) throw new ObjectNotFoundException();
        if (surveySession.getEnded() != null) throw new ForbiddenException();
        if (surveySession.getExpirationDate() != null && surveySession.getExpirationDate().before(new Date())) throw new ForbiddenException();

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
        if (surveySession.getExpirationDate() != null && surveySession.getExpirationDate().before(new Date())) throw new ForbiddenException();

        boolean finishSurvey = false;

        int nextPage = surveySession.getLastVisitedPageIndex()+1;

        // список вопросов от полученных ответов
        List<SurveyQuestion> surveyQuestions = idObjectService.getList(SurveyQuestion.class, null,
                String.format("el.surveyPage = '%s'", surveySession.getLastVisitedPageIndex()),
                null, "el.questionIndex", null, null, null);

        // список вариантов ответов
        HashMap<SurveyQuestion, SurveyAnswerVariant> surveyAnswersHashMap = new HashMap<>();

        if (dto.containsNonTextAnswers()) {
            surveyAnswersHashMap = asAnswerVariantHashMap(
                    idObjectService.getList(SurveyAnswerVariant.class, null,
                            String.format("el.id IN (%s)", dto.getAnswerIdsSeparatedByCommas()),
                            null, null, null, null));
        }

        HashMap<SurveyQuestion, List<SurveyLogicTrigger>> triggersHashMap = asLogicTriggerListHashMap(
                idObjectService.getList(SurveyLogicTrigger.class, null,
                String.format("el.surveyPage = '%s'", surveySession.getLastVisitedPageIndex()),
                        null, null, null, null));

        List<SurveyLogicTrigger> pageTriggers = triggersHashMap.get(null);

        for (SurveyLogicTrigger trigger : pageTriggers) {
             if (trigger.getLogicActionType() == DataConstants.LogicActionType.GO_TO_PAGE.getValue()) {
                 nextPage = trigger.getPageIndex();
             }

            if (!finishSurvey)
                finishSurvey = trigger.getLogicActionType() == DataConstants.LogicActionType.END_SURVEY.getValue();
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
                    if (trigger.getLogicActionType() == DataConstants.LogicActionType.CHANGE_CONCLUSION.getValue()) {
                        surveySession.setConclusion(trigger.getNewConclusion());
                    }

                    if (trigger.getLogicActionType() == DataConstants.LogicActionType.CHANGE_LINK.getValue()) {
                        surveySession.setLink(trigger.getNewLink());
                    }

                    if (trigger.getLogicActionType() == DataConstants.LogicActionType.GO_TO_PAGE.getValue()) {
                        nextPage = trigger.getPageIndex();
                    }

                    if (!finishSurvey)
                        finishSurvey = trigger.getLogicActionType() == DataConstants.LogicActionType.END_SURVEY.getValue();
                }
            }

            SurveyQuestionAnswer surveyQuestionAnswer = new SurveyQuestionAnswer(surveySession,
                    question, answerVariant, textAnswer,
                    null); // TODO stored file

            idObjectService.save(surveyQuestionAnswer);
        }

        // если следующей страницы не существует, это финиш
        if (!finishSurvey && idObjectService.getCount(SurveyPage.class, null, null,
                String.format("el.page_index = '%s'", nextPage), null) == 0) {
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
        entity.setMaximumRespondents(dto.getMaximumRespondents());
        entity.setTimeLimit(dto.getTimeLimit());
        entity.setMaxAttempts(dto.getMaxAttempts());
        entity.setParticipationType(dto.getParticipationType());
        entity.setOwner(idObjectService.getObjectById(User.class, user.getId()));
        idObjectService.save(entity);
        return entity;
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
    public EntityListResponse<SurveyAnswerVariantDTO> getSurveyAnswerVariantsPaged(String description, Integer count, Integer page,
                                                                                   Integer start, String sortField, String sortDir) {
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (!StringUtils.isEmpty(description)) {
            params.put("description", "%%" + StringUtils.lowerCase(description) + "%%");
            cause += "and lower(el.description) like :description ";
        }

        int totalCount = idObjectService.getCount(Survey.class, null, null, cause, params);
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
        entity.setSurveyQuestion(idObjectService.getObjectById(SurveyQuestion.class, dto.getSurveyQuestion()));
        entity.setText(dto.getText());
        entity.setWeight(dto.getWeight());

        idObjectService.save(entity);
        return entity;
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
    public EntityListResponse<SurveyPageDTO> getSurveyPagesPaged(String description, Integer count, Integer page,
                                                                 Integer start, String sortField, String sortDir) {
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (!StringUtils.isEmpty(description)) {
            params.put("description", "%%" + StringUtils.lowerCase(description) + "%%");
            cause += "and lower(el.description) like :description ";
        }

        int totalCount = idObjectService.getCount(Survey.class, null, countFetches, cause, params);
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
        entity.setSurvey(idObjectService.getObjectById(Survey.class, dto.getSurvey()));
        idObjectService.save(entity);
        return entity;
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
    public EntityListResponse<SurveyQuestionDTO> getSurveyQuestionsPaged(String text, Integer count, Integer page,
                                                                         Integer start, String sortField, String sortDir) {
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (!StringUtils.isEmpty(text)) {
            params.put("text", "%%" + StringUtils.lowerCase(text) + "%%");
            cause += "and lower(el.text) like :text ";
        }

        int totalCount = idObjectService.getCount(Survey.class, null, countFetches, cause, params);
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
        entity.setSurveyPage(idObjectService.getObjectById(SurveyPage.class, dto.getSurveyPage()));
        entity.setText(dto.getText());
        entity.setType(dto.getType());
        idObjectService.save(entity);
        return entity;
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
    public EntityListResponse<SurveyLogicTriggerDTO> getSurveyLogicTriggersPaged(Integer count, Integer page,
                                                                                 Integer start, String sortField, String sortDir) {
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        int totalCount = idObjectService.getCount(Survey.class, null, countFetches, cause, params);
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
        entity.setSurveyPage(idObjectService.getObjectById(SurveyPage.class, dto.getSurveyPage()));
        if (dto.getSurveyQuestion() != null) entity.setSurveyQuestion(idObjectService.getObjectById(SurveyQuestion.class, dto.getSurveyQuestion()));
        if (dto.getAnswerVariant() != null) entity.setAnswerVariant(idObjectService.getObjectById(SurveyAnswerVariant.class, dto.getAnswerVariant()));
        entity.setLogicActionType(dto.getLogicActionType());
        entity.setNewConclusion(dto.getNewConclusion());
        entity.setNewLink(dto.getNewLink());
        entity.setPageIndex(dto.getPageIndex());
        entity.setInteractionRequired(dto.isInteractionRequired());
        if (dto.getTargetQuestion() != null) entity.setTargetQuestion(idObjectService.getObjectById(SurveyQuestion.class, dto.getTargetQuestion()));

        idObjectService.save(entity);
        return entity;
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
            cause += String.format("and el.user = '%s' ", userId);
        }

        if (surveyId != null) {
            cause += String.format("and el.survey = '%s' ", surveyId);
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

        if (dto.getUser() != null) {
            entity.setUser(idObjectService.getObjectById(User.class, dto.getUser()));
        }
        entity.setLastVisitIP(dto.getLastVisitIP());
        entity.setSurvey(idObjectService.getObjectById(Survey.class, dto.getSurvey()));
        entity.setStarted(dto.getStarted());
        entity.setEnded(dto.getEnded());
        entity.setExpirationDate(dto.getExpirationDate());
        entity.setLastVisitedPageIndex(dto.getLastVisitedPageIndex());

        idObjectService.save(entity);
        return entity;
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

        entity.setSurveySession(idObjectService.getObjectById(SurveySession.class, dto.getSurveySession()));
        entity.setQuestion(idObjectService.getObjectById(SurveyQuestion.class, dto.getQuestion()));
        if (dto.getAnswerVariant() != null) entity.setAnswerVariant(idObjectService.getObjectById(SurveyAnswerVariant.class, dto.getAnswerVariant()));
        entity.setTextAnswer(dto.getTextAnswer());
        if (dto.getStoredFile() != null) entity.setStoredFile(idObjectService.getObjectById(StoredFile.class, dto.getStoredFile()));

        idObjectService.save(entity);
        return entity;
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
        List<SurveyPage> pages = idObjectService.getList(SurveyPage.class, null,
                String.format("el.surveyPage = '%s'", id), null, null, null, null);

        for (SurveyPage page : pages) {
            deleteSurveyPage(page.getId());
        }

        idObjectService.delete(Survey.class, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveyPage(UUID id) throws LogicDependencyException, ResultDependencyException {
        List<SurveyQuestion> surveyQuestions = idObjectService.getList(SurveyQuestion.class, null,
                String.format("el.surveyPage = '%s'", id), null, null, null, null);

        for (SurveyQuestion question : surveyQuestions) {
            deleteSurveyQuestion(question.getId(), true); // если удаляется целая страница - подразумевается, что удаляется вся логика вместе с ней
        }

        idObjectService.delete(SurveyPage.class, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveyQuestion(UUID id, boolean deleteLogic) throws LogicDependencyException, ResultDependencyException {
        boolean isQuestionAnswered = idObjectService.getCount(SurveyQuestionAnswer.class, null, null,
                String.format("el.question = '%s'", id), null) > 0;

        if (isQuestionAnswered) throw new ResultDependencyException("question is answered");

        if (!deleteLogic) {
            boolean hasLogic = idObjectService.getCount(SurveyLogicTrigger.class, null, null,
                    String.format("el.surveyQuestion = '%s' OR el.targetQuestion = '%s'", id, id), null) > 0;

            if (hasLogic) throw new LogicDependencyException("question has corresponding logic");
        } else {
            List<SurveyLogicTrigger> logicTriggers = idObjectService.getList(SurveyLogicTrigger.class, null,
                    String.format("el.surveyQuestion = '%s' OR el.targetQuestion = '%s'", id, id),
                    null, null, null, null);

            for (SurveyLogicTrigger trigger : logicTriggers) {
                deleteSurveyLogicTrigger(trigger.getId());
            }
        }

        List<SurveyAnswerVariant> answerVariants = idObjectService.getList(SurveyAnswerVariant.class, null,
                String.format("el.surveyQuestion = '%s'", id), null, null, null, null);

        for (SurveyAnswerVariant var : answerVariants) {
            deleteSurveyAnswerVariant(var.getId(), deleteLogic);
        }

        idObjectService.delete(SurveyQuestion.class, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveyAnswerVariant(UUID id, boolean deleteLogic) throws LogicDependencyException, ResultDependencyException {
        boolean isAnswered = idObjectService.getCount(SurveyQuestionAnswer.class, null, null,
                String.format("el.answerVariant = '%s'", id), null) > 0;
        if (isAnswered) throw new ResultDependencyException("selected as answer");

        if (!deleteLogic) {
            boolean hasLogic = idObjectService.getCount(SurveyLogicTrigger.class, null, null,
                    String.format("el.answerVariant = '%s'", id), null) > 0;

            if (hasLogic) throw new LogicDependencyException("answer has corresponding logic");
        } else {
            List<SurveyLogicTrigger> logicTriggers = idObjectService.getList(SurveyLogicTrigger.class, null,
                    String.format("el.answerVariant = '%s'", id),
                    null, null, null, null);

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
    public void deleteSurveySession(UUID id)
    {
        idObjectService.delete(SurveyQuestionAnswer.class, String.format("el.surveySession = '%s'", id), null);
        idObjectService.delete(SurveyLogicTrigger.class, id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveyQuestionAnswer(UUID id) {
        idObjectService.delete(SurveyQuestionAnswer.class, id);
    }
}
