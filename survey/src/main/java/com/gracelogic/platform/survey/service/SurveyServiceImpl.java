package com.gracelogic.platform.survey.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.filestorage.model.StoredFile;
import com.gracelogic.platform.survey.dto.admin.*;
import com.gracelogic.platform.survey.dto.user.*;
import com.gracelogic.platform.survey.exception.*;
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
        PAGE,
        QUESTION,
        ANSWER,
    }

    /**
     * Represents result set as UUID HashMap
     * @param list ResultSet
     * @param <T>
     */
    private static <T extends IdObject<UUID>> HashMap<UUID, T> asUUIDHashMap(List<T> list) {
        HashMap<UUID, T> hashMap = new HashMap<>();
        for (T t : list) {
            hashMap.put(t.getId(), t);
        }
        return hashMap;
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


    public String exportResults(UUID surveyId) throws ObjectNotFoundException {
        Survey survey = idObjectService.getObjectById(Survey.class, surveyId);
        if (survey == null) throw new ObjectNotFoundException();
        String results = "question_answer_id;question_id;question_text;answer_variant_id;answer_text;answer_matrix_row;answer_matrix_column\n";

        Map<String, Object> params = new HashMap<>();
        params.put("surveyId", surveyId);
        List<SurveyQuestionAnswer> listAnswers = idObjectService.getList(SurveyQuestionAnswer.class,
                "left join el.surveySession ss", "ss.survey.id=:surveyId ",
                params, null, null, null);
        params.clear();

        HashSet<UUID> questionIds = new HashSet<>();
        for (SurveyQuestionAnswer answer : listAnswers) {
            questionIds.add(answer.getQuestion().getId());
        }
        params.put("questionIds", questionIds);

        HashMap<UUID, SurveyQuestion> questionsHashMap = asUUIDHashMap(idObjectService.getList(SurveyQuestion.class, null, "el.id in (:questionIds) ",
                params, null, null, null));
        HashMap<UUID, SurveyAnswerVariant> answerVariantHashMap = asUUIDHashMap(idObjectService.getList(SurveyAnswerVariant.class,
                null, "el.surveyQuestion.id in (:questionIds) ",
                params, null, null, null, null));
        params.clear();

        for (SurveyQuestionAnswer answer : listAnswers) {
            results += String.format("%s;%s;%s;%s;%s;%s;%s\n", answer.getId(), answer.getSurveyQuestion().getId(),
                    questionsHashMap.get(answer.getSurveyQuestion().getId()).getText(), answer.getAnswerVariant() != null ? answer.getAnswerVariant().getId() : "",
                    answer.getAnswerVariant() != null ? answerVariantHashMap.get(answer.getAnswerVariant().getId()).getText() : "",
                    answer.getSelectedMatrixRow() != null ? answer.getSelectedMatrixRow() : "", answer.getSelectedMatrixColumn() != null ? answer.getSelectedMatrixColumn() : "");
        }

        return results;
    }

    @Override
    public SurveyIntroductionDTO getSurveyIntroduction(UUID surveyId)
            throws ObjectNotFoundException, ForbiddenException {
        Survey survey = idObjectService.getObjectById(Survey.class, surveyId);
        if (survey == null) throw new ObjectNotFoundException();
        if (!survey.isActive()) throw new ForbiddenException();

        return new SurveyIntroductionDTO(survey);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyInteractionDTO startSurveyPreview(UUID surveyId, AuthorizedUser user, String ipAddress)
            throws ObjectNotFoundException{
        Survey survey = idObjectService.getObjectById(Survey.class, surveyId);
        if (survey == null) {
            throw new ObjectNotFoundException();
        }

        Date now = new Date();

        SurveySession surveySession = new SurveySession();
        surveySession.setStarted(now);
        surveySession.setLastVisitIP(ipAddress);
        if (user != null) {
            surveySession.setUser(idObjectService.getObjectById(User.class, user.getId()));
        }

        if (survey.getTimeLimit() != null && survey.getTimeLimit() > 0) {
            surveySession.setExpirationDate(new Date(now.getTime() + survey.getTimeLimit()));
        }

        Integer[] pageVisitHistory = new Integer[1];
        pageVisitHistory[0] = 0;
        surveySession.setPageVisitHistory(pageVisitHistory);
        surveySession.setLink(survey.getLink());
        surveySession.setPreviewSession(true);
        surveySession.setConclusion(survey.getConclusion());
        surveySession.setSurvey(survey);
        idObjectService.save(surveySession);

        SurveyInteractionDTO surveyInteractionDTO = new SurveyInteractionDTO();
        surveyInteractionDTO.setSurveySessionId(surveySession.getId());
        surveyInteractionDTO.setSurveyPage(getSurveyPage(surveySession, 0));
        return surveyInteractionDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyInteractionDTO startSurvey(UUID surveyId, AuthorizedUser user, String ipAddress)
            throws ObjectNotFoundException, RespondentLimitException, ForbiddenException, MaxAttemptsHitException {

        Survey survey = idObjectService.getObjectById(Survey.class, surveyId);
        if (survey == null) {
            throw new ObjectNotFoundException();
        }

        Date now = new Date();

        if (!survey.isActive()) throw new ForbiddenException();

        if (survey.getExpirationDate() != null && survey.getExpirationDate().before(now))
            throw new ForbiddenException();

        if (survey.getStartDate() != null && survey.getStartDate().after(now))
            throw new ForbiddenException();

        if (user == null && survey.getSurveyParticipationType().getId().equals(DataConstants.ParticipationTypes.AUTHORIZATION_REQUIRED.getValue())) {
            throw new ForbiddenException();
        }

        // Check max attempts
        if (survey.getMaxAttempts() != null && survey.getMaxAttempts() > 0) {
            Map<String, Object> params = new HashMap<>();
            String cause = "el.survey.id=:surveyId ";
            params.put("surveyId", survey.getId());

            if (survey.getSurveyParticipationType().getId().equals(DataConstants.ParticipationTypes.AUTHORIZATION_REQUIRED.getValue())) {
                cause += "and el.user.id=:userId ";
                params.put("userId", user.getId());
            }

            if (survey.getSurveyParticipationType().getId().equals(DataConstants.ParticipationTypes.IP_LIMITED.getValue()) ||
                    survey.getSurveyParticipationType().getId().equals(DataConstants.ParticipationTypes.COOKIE_IP_LIMITED.getValue()) ) {
                cause += "and el.lastVisitIP=:ip ";
                params.put("ip", ipAddress);
            }

            Integer passesFromThisIP = idObjectService.checkExist(SurveySession.class, null, cause, params, survey.getMaxAttempts() + 1);

            if (passesFromThisIP >= survey.getMaxAttempts()) {
                throw new MaxAttemptsHitException();
            }
        }

        // Check max respondents
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

        Integer[] pageVisitHistory = new Integer[1];
        pageVisitHistory[0] = 0;
        surveySession.setPageVisitHistory(pageVisitHistory);
        surveySession.setLink(survey.getLink());
        surveySession.setConclusion(survey.getConclusion());
        surveySession.setSurvey(survey);
        surveySession.setPreviewSession(false);
        idObjectService.save(surveySession);

        SurveyInteractionDTO surveyInteractionDTO = new SurveyInteractionDTO();
        surveyInteractionDTO.setSurveySessionId(surveySession.getId());
        surveyInteractionDTO.setSurveyPage(getSurveyPage(surveySession, 0));
        return surveyInteractionDTO;
    }

    private SurveyPageDTO getSurveyPage(SurveySession surveySession, int pageIndex) throws ObjectNotFoundException {
        Map<String, Object> params = new HashMap<>();
        String cause = "el.survey.id = :surveyId and el.pageIndex = :pageIndex ";
        params.put("surveyId", surveySession.getSurvey().getId());
        params.put("pageIndex", pageIndex);

        List<SurveyPage> surveyPages = idObjectService.getList(SurveyPage.class, null, cause, params, null, null, null, 1);
        if (surveyPages.isEmpty()) {
            throw new ObjectNotFoundException();
        }

        SurveyPage surveyPage = surveyPages.iterator().next();
        SurveyPageDTO dto = SurveyPageDTO.prepare(surveyPage);

        // 1. Getting list of questions of the current page
        params.clear();
        cause = "el.surveyPage.id=:surveyPageId ";
        params.put("surveyPageId", surveyPage.getId());

        List<SurveyQuestion> questions = idObjectService.getList(SurveyQuestion.class, null,
                cause, params, "el.questionIndex", "ASC", null, null);

        // 2. Getting the logic. For the web, select only HIDE_QUESTION / SHOW_QUESTION
        params.clear();
        cause = "el.surveyPage.pageIndex = :pageIndex AND el.surveyLogicActionType.id in (:logicActionTypeIds) ";
        params.put("pageIndex", pageIndex);
        Set<UUID> logicActionTypeIds = new HashSet<>();
        logicActionTypeIds.add(DataConstants.LogicActionTypes.HIDE_QUESTION.getValue());
        logicActionTypeIds.add(DataConstants.LogicActionTypes.SHOW_QUESTION.getValue());
        params.put("logicActionTypeIds", logicActionTypeIds);
        List<SurveyLogicTrigger> logicTriggers = idObjectService.getList(SurveyLogicTrigger.class,
                null, cause, params, null, null, null);

        List<SurveyLogicTriggerDTO> logicTriggersDTO = new LinkedList<>();
        for (SurveyLogicTrigger trigger : logicTriggers) {
            logicTriggersDTO.add(SurveyLogicTriggerDTO.prepare(trigger));
        }
        dto.setLogicTriggers(logicTriggersDTO);

        Set<UUID> questionIds = new HashSet<>();
        for (SurveyQuestion question : questions) {
            questionIds.add(question.getId());
        }

        if (!questionIds.isEmpty()) {
            // 3. Getting answer variants
            params.clear();
            cause = "el.surveyQuestion.id in (:questionIds) ";
            params.put("questionIds", questionIds);

            HashMap<SurveyQuestion, List<SurveyAnswerVariant>> answerVariants = asListAnswerVariantHashMap(idObjectService.getList(SurveyAnswerVariant.class, null,
                    cause, params, "el.sortOrder", "ASC", null, null));

            List<SurveyQuestionDTO> surveyQuestionDTOs = new LinkedList<>();
            for (SurveyQuestion question : questions) {
                List<SurveyAnswerVariantDTO> answerVariantsDTO = null;
                List<SurveyAnswerVariant> answersList = answerVariants.get(question);
                if (answersList != null) {
                    answerVariantsDTO = new LinkedList<>();
                    for (SurveyAnswerVariant answerVariant : answersList) {
                        answerVariantsDTO.add(SurveyAnswerVariantDTO.prepare(answerVariant));
                    }
                }
                SurveyQuestionDTO surveyQuestionDTO = SurveyQuestionDTO.prepare(question);
                surveyQuestionDTO.setAnswerVariants(answerVariantsDTO);
                surveyQuestionDTOs.add(surveyQuestionDTO);
            }
            dto.setQuestions(surveyQuestionDTOs);
        }

        return dto;
    }

    /**
     * Saves or updates entire survey. Deletes specified pages, questions, answers and logic triggers.
     * @param surveyDTO dto
     * @return Created/updated survey id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Survey saveEntireSurvey(SurveyDTO surveyDTO, AuthorizedUser user)
            throws ObjectNotFoundException, LogicDependencyException, ResultDependencyException, BadDTOException {

        Survey survey = saveSurvey(surveyDTO, user);
        Map<String, Object> params = new HashMap<>();

        // Do not change operation order
        // 1. Delete specified survey pages
        if (surveyDTO.getPagesToDelete() != null && surveyDTO.getPagesToDelete().size() > 0) {
            params.put("ids", surveyDTO.getPagesToDelete());
            List<SurveyPage> pages = idObjectService.getList(SurveyPage.class, null, "el.id in (:ids)", params,
                    null, null, null, null);
            for (SurveyPage page : pages)
                deleteSurveyPage(page.getId());
            params.clear();
        }

        if (surveyDTO.getPages() != null) {
            for (SurveyPageDTO surveyPageDTO : surveyDTO.getPages()) {

                // 2. Delete specified survey logic triggers
                if (surveyPageDTO.getLogicTriggersToDelete() != null && surveyPageDTO.getLogicTriggersToDelete().size() > 0) {
                    params.put("ids", surveyPageDTO.getLogicTriggersToDelete());
                    List<SurveyLogicTrigger> logicTriggers = idObjectService.getList(SurveyLogicTrigger.class, null, "el.id in (:ids)", params,
                            null, null, null, null);
                    for (SurveyLogicTrigger trigger : logicTriggers)
                        deleteSurveyLogicTrigger(trigger.getId());

                    params.clear();
                }

                // 3. Delete specified survey questions
                if (surveyPageDTO.getQuestionsToDelete() != null && surveyPageDTO.getQuestionsToDelete().size() > 0) {
                    params.put("ids", surveyPageDTO.getQuestionsToDelete());
                    List<SurveyQuestion> questions = idObjectService.getList(SurveyQuestion.class, null, "el.id in (:ids)", params,
                            null, null, null, null);
                    for (SurveyQuestion question : questions)
                        deleteSurveyQuestion(question.getId(), true);

                    params.clear();
                }

                for (SurveyQuestionDTO questionDTO : surveyPageDTO.getQuestions()) {
                    // 4. Delete specified survey answer variants
                    if (questionDTO.getAnswersToDelete() != null && questionDTO.getAnswersToDelete().size() > 0) {
                        params.put("ids", questionDTO.getAnswersToDelete());
                        List<SurveyAnswerVariant> answers = idObjectService.getList(SurveyAnswerVariant.class, null, "el.id in (:ids)", params,
                                null, null, null, null);
                        for (SurveyAnswerVariant a : answers)
                            deleteSurveyAnswerVariant(a.getId(), true);

                        params.clear();
                    }
                }
            }
        }

        for (SurveyPageDTO surveyPageDTO : surveyDTO.getPages()) {
            surveyPageDTO.setSurveyId(survey.getId());
            SurveyPage surveyPage = saveSurveyPage(surveyPageDTO);

            // page layer: here we can update existing logic triggers OR add new PAGE LOGIC TRIGGER
            for (SurveyLogicTriggerDTO logicTriggerDTO : surveyPageDTO.getLogicTriggers()) {
                logicTriggerDTO.setSurveyPageId(surveyPage.getId());

                if (logicTriggerDTO.getId() == null && (logicTriggerDTO.getAnswerVariantId() != null ||
                        logicTriggerDTO.getSurveyQuestionId() != null ||
                        logicTriggerDTO.getTargetQuestionId() != null)) {
                    throw new BadDTOException("Logic trigger on page " + surveyPage.getPageIndex() + " contains incompatible fields. " +
                            "If you're trying to create new logic trigger for question or answer variant, put this model to corresponding DTO.");
                }
                saveSurveyLogicTrigger(logicTriggerDTO);
            }

            for (SurveyQuestionDTO surveyQuestionDTO : surveyPageDTO.getQuestions()) {
                surveyQuestionDTO.setSurveyPageId(surveyPage.getId());
                SurveyQuestion surveyQuestion = saveSurveyQuestion(surveyQuestionDTO);

                // question layer: NEW QUESTION LOGIC TRIGGERS ONLY
                for (SurveyLogicTriggerDTO logicTriggerDTO : surveyQuestionDTO.getLogicTriggersToAdd()) {
                    logicTriggerDTO.setSurveyQuestionId(surveyQuestion.getId());
                    logicTriggerDTO.setSurveyPageId(surveyPage.getId());
                    if (logicTriggerDTO.getId() != null) {
                        throw new BadDTOException("Logic trigger for question " + surveyQuestion.getText() + " already contains id. " +
                                "If you're trying to update logic trigger, put this model to SurveyPageDTO.logicTriggers.");
                    }
                    if (logicTriggerDTO.getAnswerVariantId() != null) {
                        throw new BadDTOException("Logic trigger for question " + surveyQuestion.getText() + " contains incompatible fields." +
                                "If you're trying to create new logic trigger for answer variant, put this model to corresponding DTO.");
                    }
                    saveSurveyLogicTrigger(logicTriggerDTO);
                }

                for (SurveyAnswerVariantDTO answerVariantDTO : surveyQuestionDTO.getAnswerVariants()) {
                    answerVariantDTO.setSurveyQuestionId(surveyQuestion.getId());
                    SurveyAnswerVariant variant = saveSurveyAnswerVariant(answerVariantDTO);

                    // answer layer: NEW ANSWER VARIANT LOGIC TRIGGERS ONLY
                    for (SurveyLogicTriggerDTO logicTriggerDTO : answerVariantDTO.getLogicTriggersToAdd()) {
                        logicTriggerDTO.setSurveyQuestionId(surveyQuestion.getId());
                        logicTriggerDTO.setSurveyPageId(surveyPage.getId());
                        logicTriggerDTO.setAnswerVariantId(variant.getId());

                        if (logicTriggerDTO.getId() != null) {
                            throw new BadDTOException("Logic trigger for answer variant " + variant.getText() + " already contains id. " +
                                    "If you're trying to update logic trigger, put this model to SurveyPageDTO.logicTriggers.");
                        }
                        saveSurveyLogicTrigger(logicTriggerDTO);
                    }
                }
            }
        }
        return survey;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyInteractionDTO goToPage(UUID surveySessionId, int pageIndex)
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

        Integer[] pagesHistory = Arrays.copyOf(surveySession.getPageVisitHistory(),
                surveySession.getPageVisitHistory().length + 1);
        pagesHistory[pagesHistory.length-1] = pageIndex;

        surveySession.setPageVisitHistory(pagesHistory);
        idObjectService.save(surveySession);

        SurveyInteractionDTO surveyInteractionDTO = new SurveyInteractionDTO();
        surveyInteractionDTO.setSurveySessionId(surveySession.getId());
        surveyInteractionDTO.setSurveyPage(getSurveyPage(surveySession, pageIndex));

        return surveyInteractionDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public SurveyInteractionDTO goBack(UUID surveySessionId) throws ObjectNotFoundException, ForbiddenException {
        SurveySession surveySession = idObjectService.getObjectById(SurveySession.class, surveySessionId);
        if (surveySession == null) {
            throw new ObjectNotFoundException();
        }
        if (surveySession.getEnded() != null || // session already ended
                (surveySession.getExpirationDate() != null && surveySession.getExpirationDate().before(new Date())) || // hit time limit
                (surveySession.getPageVisitHistory() == null || surveySession.getPageVisitHistory().length <= 1)) { // trying to go back to nothing
            throw new ForbiddenException();
        }

        Integer[] visitHistory = Arrays.copyOf(surveySession.getPageVisitHistory(), surveySession.getPageVisitHistory().length-1);
        surveySession.setPageVisitHistory(visitHistory);

        idObjectService.save(surveySession);

        int previousPageIndex = visitHistory[visitHistory.length-1];

        // question id, List<answerDTO>
        HashMap<UUID, List<AnswerDTO>> pageAnswers = new HashMap<>();

        Map<String, Object> params = new HashMap<>();
        params.put("pageIndex", previousPageIndex);
        params.put("surveySessionId", surveySession.getId());

        List<SurveyQuestionAnswer> answersList = idObjectService.getList(SurveyQuestionAnswer.class, "left join el.surveyPage sp",
                "el.surveySession.id = :surveySessionId AND sp.pageIndex=:pageIndex",
                params, null, null, null);

        for (SurveyQuestionAnswer model : answersList) {
            List<AnswerDTO> answerDTOs = pageAnswers.get(model.getSurveyQuestion().getId());
            if (answerDTOs == null) {
                answerDTOs = new ArrayList<>();
                pageAnswers.put(model.getSurveyQuestion().getId(), answerDTOs);
            }
            answerDTOs.add(AnswerDTO.fromModel(model));
        }

        SurveyInteractionDTO surveyInteractionDTO = new SurveyInteractionDTO();
        surveyInteractionDTO.setSurveySessionId(surveySession.getId());
        surveyInteractionDTO.setSurveyPage(getSurveyPage(surveySession, previousPageIndex));
        surveyInteractionDTO.setPageAnswers(pageAnswers);
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
        surveyInteractionDTO.setSurveyPage(getSurveyPage(surveySession,
                surveySession.getPageVisitHistory()[surveySession.getPageVisitHistory().length-1]));

        return surveyInteractionDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyInteractionDTO saveAnswersAndContinue(UUID surveySessionId, PageAnswersDTO dto)
            throws ObjectNotFoundException, ForbiddenException, UnansweredException {

        SurveySession surveySession = idObjectService.getObjectById(SurveySession.class, surveySessionId);
        final Date dateNow = new Date();

        if (surveySession == null) {
            throw new ObjectNotFoundException();
        }
        if (surveySession.getEnded() != null) {
            throw new ForbiddenException();
        }

        // if user passing time limited survey
        if (surveySession.getExpirationDate() != null && surveySession.getExpirationDate().before(dateNow)) {
            throw new ForbiddenException();
        }

        boolean finishSurvey = false;

        int lastVisitedPageIndex = surveySession.getPageVisitHistory()[surveySession.getPageVisitHistory().length-1];
        logger.info("lastVisitPageIndex: " + lastVisitedPageIndex);

        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", surveySession.getId());
        params.put("pageIndex", lastVisitedPageIndex);
        List<SurveyQuestionAnswer> possibleAnswers = idObjectService.getList(SurveyQuestionAnswer.class, "left join el.surveyPage sp",
                "el.surveySession.id = :sessionId AND sp.pageIndex = :pageIndex",
                params, null, null, null);

        for (SurveyQuestionAnswer questionAnswer : possibleAnswers) {
            idObjectService.delete(SurveyQuestionAnswer.class, questionAnswer.getId());
        }

        int nextPage = lastVisitedPageIndex + 1;

        // 1. Getting the list of questions on the last visited page
        params.clear();
        params.put("lastVisitedPageIndex", lastVisitedPageIndex);
        params.put("surveyId", surveySession.getSurvey().getId());
        HashMap<UUID, SurveyQuestion> surveyQuestionsHashMap = asUUIDHashMap(idObjectService.getList(SurveyQuestion.class, "left join el.surveyPage sp left join sp.survey sv",
                "sv.id=:surveyId and sp.pageIndex=:lastVisitedPageIndex",
                params, "el.questionIndex", "ASC", null, null));

        // 2. Getting all answer variants
        // Stored by survey answer id
        HashMap<UUID, SurveyAnswerVariant> surveyAnswersHashMap = new HashMap<>();
        HashMap<SurveyQuestion, List<SurveyAnswerVariant>> answerVariantsByQuestion = new HashMap<>();
        if (dto.containsNonTextAnswers()) {
            params.clear();
            params.put("questionIds", surveyQuestionsHashMap.keySet());
            List<SurveyAnswerVariant> list = idObjectService.getList(SurveyAnswerVariant.class, null,
                    "el.surveyQuestion.id in (:questionIds)", params, null, null, null);
            surveyAnswersHashMap = asUUIDHashMap(list);
            answerVariantsByQuestion = asListAnswerVariantHashMap(list);
        }

        // 3. Getting logic by last visited page
        params.clear();
        params.put("lastVisitedPageIndex", lastVisitedPageIndex);
        params.put("surveyId", surveySession.getSurvey().getId());
        // TODO: sort logic by 1. PAGE -> 2. QUESTION INDEX -> 3. ANSWER INDEX
        List<SurveyLogicTrigger> logicTriggers =
                idObjectService.getList(SurveyLogicTrigger.class, "left join el.surveyPage sp left join sp.survey sv",
                        "sv.id=:surveyId and  sp.pageIndex=:lastVisitedPageIndex",
                        params, null, null, null);

        Set<UUID> answeredQuestions = new HashSet<>();
        Set<UUID> selectedAnswers = new HashSet<>();

        // question id, answer
        HashMap<UUID, List<SurveyQuestionAnswer>> matrixAnswers = new HashMap<>();

        // save received answers
        for (Map.Entry<UUID, List<AnswerDTO>> entry : dto.getAnswers().entrySet()) {
            for (AnswerDTO answerDTO : entry.getValue()) {
                SurveyQuestion question = surveyQuestionsHashMap.get(entry.getKey());
                SurveyAnswerVariant answerVariant = null;
                if (answerDTO.getAnswerVariantId() != null)
                    answerVariant = surveyAnswersHashMap.get(answerDTO.getAnswerVariantId());

                // if user selected custom variant and didn't answered in text field of required question
                if (answerVariant != null && question.getRequired() &&
                        answerVariant.getCustomVariant() != null && answerVariant.getCustomVariant() &&
                        StringUtils.isBlank(answerDTO.getText())) { // DO NOT simplify this if
                    throw new UnansweredException("Custom text field of question " + question.getText() + " is required");
                }

                boolean isTextFieldRequired = question.getSurveyQuestionType().getId().equals(DataConstants.QuestionTypes.TEXT_MULTILINE.getValue()) ||
                        question.getSurveyQuestionType().getId().equals(DataConstants.QuestionTypes.TEXT_SINGLE_LINE.getValue()) ||
                        question.getSurveyQuestionType().getId().equals(DataConstants.QuestionTypes.RATING_SCALE.getValue());

                if (question.getRequired() && isTextFieldRequired && StringUtils.isBlank(answerDTO.getText())) {
                    throw new UnansweredException("Text field of question \"" + question.getText() + "\" is required");
                }

                boolean isMatrixQuestion = question.getSurveyQuestionType().getId().equals(DataConstants.QuestionTypes.MATRIX_CHECKBOX.getValue()) ||
                        question.getSurveyQuestionType().getId().equals(DataConstants.QuestionTypes.MATRIX_RADIOBUTTON.getValue());

                if (isMatrixQuestion && (answerDTO.getSelectedMatrixColumn() == null || answerDTO.getSelectedMatrixRow() == null)) {
                    throw new UnansweredException("You're answering to matrix question \"" + question.getText() + "\" without specified row index or column index");
                }

                if (isMatrixQuestion) {
                    int maxRows = question.getMatrixRows().length;
                    if (answerVariantsByQuestion.get(question) != null) { // if matrix has custom variant
                        maxRows++;
                    }

                    boolean rowIndexCheck = answerDTO.getSelectedMatrixRow() >= 0 && answerDTO.getSelectedMatrixRow() < maxRows;
                    boolean columnIndexCheck = answerDTO.getSelectedMatrixColumn() >= 0 && answerDTO.getSelectedMatrixColumn() < question.getMatrixColumns().length;

                    if (!rowIndexCheck) {
                        throw new ForbiddenException("You're answering to matrix row that don't exist. Expected 0-" + (maxRows-1)
                                + ", but received " + answerDTO.getSelectedMatrixRow());
                    }

                    if (!columnIndexCheck) {
                        throw new ForbiddenException("You're answering to matrix row that don't exist. Expected 0-" +
                                (question.getMatrixColumns().length-1) + ", but received " + answerDTO.getSelectedMatrixColumn());
                    }
                }

                SurveyQuestionAnswer surveyQuestionAnswer = new SurveyQuestionAnswer(surveySession,
                        question,
                        answerVariant,
                        answerDTO.getText(),
                        null); // TODO: stored file

                surveyQuestionAnswer.setSelectedMatrixRow(answerDTO.getSelectedMatrixRow());
                surveyQuestionAnswer.setSelectedMatrixColumn(answerDTO.getSelectedMatrixColumn());
                idObjectService.save(surveyQuestionAnswer);
                // matrix question requirements will be checked later
                if (!isMatrixQuestion) {
                    answeredQuestions.add(question.getId());
                } else {
                    // put answer to matrixAnswers if matrix marked as required
                    if (question.getRequired()) {
                        List<SurveyQuestionAnswer> list = matrixAnswers.get(question.getId());
                        if (list == null) {
                            list = new ArrayList<>();
                            matrixAnswers.put(question.getId(), list);
                        }
                        list.add(surveyQuestionAnswer);
                    }
                }

                if (answerVariant != null)
                    selectedAnswers.add(answerVariant.getId());
            }
        }

        // check all rows of required matrix question
        for (Map.Entry<UUID, List<SurveyQuestionAnswer>> entry : matrixAnswers.entrySet()) {
            SurveyQuestion question = surveyQuestionsHashMap.get(entry.getKey());
            HashSet<Integer> rowsTotal = new HashSet<>();
            for (int i = 0; i < question.getMatrixRows().length; i++) {
                rowsTotal.add(i);
            }

            if (answerVariantsByQuestion.get(question) != null) { // if matrix has custom variant
                rowsTotal.add(question.getMatrixRows().length); // add it as last row
            }

            HashSet<Integer> rowIndexesAnswered = new HashSet<>();
            for (SurveyQuestionAnswer answer : entry.getValue()) {
                rowIndexesAnswered.add(answer.getSelectedMatrixRow());
            }

            if (rowIndexesAnswered.equals(rowsTotal)) {
                answeredQuestions.add(entry.getKey());
            }
        }

        for (Map.Entry<UUID, SurveyQuestion> entry : surveyQuestionsHashMap.entrySet()) {
            if (entry.getValue().getRequired() && !answeredQuestions.contains(entry.getKey()))
                throw new UnansweredException();
        }

        for (SurveyLogicTrigger trigger : logicTriggers) {
            LogicTriggerCheckItem checkItem = LogicTriggerCheckItem.PAGE;
            if (trigger.getSurveyQuestion() != null) {
                checkItem = LogicTriggerCheckItem.QUESTION;
                if (trigger.getAnswerVariant() != null) checkItem = LogicTriggerCheckItem.ANSWER;
            }

            boolean triggered = false;
            switch (checkItem) {
                case PAGE:
                    triggered = true;
                    break;
                case QUESTION:
                    boolean answeredTrigger = trigger.isInteractionRequired() && answeredQuestions.contains(trigger.getSurveyQuestion().getId());
                    boolean unansweredTrigger = !trigger.isInteractionRequired() && !answeredQuestions.contains(trigger.getSurveyQuestion().getId());
                    triggered = answeredTrigger || unansweredTrigger;
                    break;
                case ANSWER:
                    boolean selectedTrigger = trigger.isInteractionRequired() && selectedAnswers.contains(trigger.getSurveyQuestion().getId());
                    boolean unselectedTrigger = !trigger.isInteractionRequired() && !selectedAnswers.contains(trigger.getSurveyQuestion().getId());
                    triggered = selectedTrigger || unselectedTrigger;
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

        // if next page is not exists, this is finish
        params.clear();
        params.put("surveyId", surveySession.getSurvey().getId());
        params.put("pageIndex", nextPage);
        if (!finishSurvey && idObjectService.checkExist(SurveyPage.class, null, "el.survey.id=:surveyId and el.pageIndex=:pageIndex",
                params, 1) == 0) {
            finishSurvey = true;
        }

        SurveyInteractionDTO surveyInteractionDTO = new SurveyInteractionDTO();
        surveyInteractionDTO.setSurveySessionId(surveySession.getId());
        if (finishSurvey) {
            SurveyConclusionDTO surveyConclusionDTO = new SurveyConclusionDTO();
            surveyConclusionDTO.setConclusion(surveySession.getConclusion());
            surveyConclusionDTO.setLink(surveySession.getLink());
            surveyInteractionDTO.setSurveyConclusion(surveyConclusionDTO);

            surveySession.setEnded(dateNow);
        } else {
            surveyInteractionDTO.setSurveyPage(getSurveyPage(surveySession, nextPage));

            Integer[] pagesHistory = Arrays.copyOf(surveySession.getPageVisitHistory(),
                    surveySession.getPageVisitHistory().length + 1);
            pagesHistory[pagesHistory.length-1] = nextPage;
            surveySession.setPageVisitHistory(pagesHistory);
        }

        if (finishSurvey && surveySession.getPreviewSession()) {
            params.clear();
            params.put("surveySessionId", surveySession.getId());
            idObjectService.delete(SurveyQuestionAnswer.class, "el.surveySession.id = :surveySessionId", params);
            idObjectService.delete(SurveySession.class, surveySession.getId());
        } else {
            idObjectService.save(surveySession);
        }
        return surveyInteractionDTO;
    }

    @Override
    public EntityListResponse<SurveyDTO> getSurveysPaged(String name, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<>();

        if (!StringUtils.isEmpty(name)) {
            params.put("name", "%%" + StringUtils.lowerCase(name) + "%%");
            cause += "and lower(el.name) like :name ";
        }

        int totalCount = idObjectService.getCount(Survey.class, null, countFetches, cause, params);

        EntityListResponse<SurveyDTO> entityListResponse = new EntityListResponse<>(totalCount, count, page, start);

        List<Survey> items = idObjectService.getList(Survey.class, null, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);

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

        entity.setActive(dto.getActive());
        entity.setName(dto.getName());
        entity.setStartDate(dto.getStartDate());
        entity.setExpirationDate(dto.getExpirationDate());
        entity.setShowProgress(dto.getShowProgress());
        entity.setShowQuestionNumber(dto.getShowQuestionNumber());
        entity.setAllowReturn(dto.getAllowReturn());
        entity.setIntroduction(dto.getIntroduction());
        entity.setConclusion(dto.getConclusion());
        entity.setLink(dto.getLink());
        entity.setMaxRespondents(dto.getMaxRespondents());
        entity.setTimeLimit(dto.getTimeLimit());
        entity.setMaxAttempts(dto.getMaxAttempts());
        entity.setSurveyParticipationType(ds.get(SurveyParticipationType.class, dto.getParticipationTypeId()));
        entity.setOwner(idObjectService.getObjectById(User.class, user.getId()));
        return idObjectService.save(entity);
    }


    @Override
    public SurveyDTO getSurvey(UUID surveyId, boolean entire) throws ObjectNotFoundException {
        Survey entity = idObjectService.getObjectById(Survey.class, surveyId);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }

        SurveyDTO surveyDTO = SurveyDTO.prepare(entity);
        if (entire) {
            Map<String, Object> params = new HashMap<>();
            params.put("surveyId", surveyId);
            List<SurveyPage> surveyPages = idObjectService.getList(SurveyPage.class, null, "el.survey.id = :surveyId", params,
                    "el.pageIndex ASC", null, null);

            HashMap<UUID, SurveyPageDTO> pagesDTO = new HashMap<>();
            for (SurveyPage page : surveyPages) {
                pagesDTO.put(page.getId(), SurveyPageDTO.prepare(page));
            }

            if (!pagesDTO.isEmpty()) {
                params.clear();
                params.put("pageIds", pagesDTO.keySet());
                List<SurveyQuestion> surveyQuestions = idObjectService.getList(SurveyQuestion.class, null, "el.surveyPage.id in (:pageIds)", params,
                        "el.questionIndex ASC", null, null);

                List<SurveyLogicTrigger> logicTriggers = idObjectService.getList(SurveyLogicTrigger.class, null, "el.surveyPage.id in (:pageIds)", params,
                        null, null, null);

                HashMap<UUID, SurveyQuestionDTO> questionsDTO = new HashMap<>();
                for (SurveyQuestion question : surveyQuestions) {
                    SurveyQuestionDTO questionDTO = SurveyQuestionDTO.prepare(question);
                    questionsDTO.put(question.getId(), questionDTO);
                    pagesDTO.get(question.getSurveyPage().getId()).getQuestions().add(questionDTO);
                }

                for (SurveyLogicTrigger trigger : logicTriggers) {
                    SurveyLogicTriggerDTO dto = SurveyLogicTriggerDTO.prepare(trigger);
                    pagesDTO.get(trigger.getSurveyPage().getId()).getLogicTriggers().add(dto);
                }

                if (!questionsDTO.isEmpty()) {
                    params.clear();
                    params.put("questionIds", questionsDTO.keySet());
                    List<SurveyAnswerVariant> surveyAnswerVariant = idObjectService.getList(SurveyAnswerVariant.class, null, "el.surveyQuestion.id in (:questionIds)", params,
                            "el.sortOrder ASC", null, null);

                    for (SurveyAnswerVariant variant : surveyAnswerVariant) {
                        SurveyAnswerVariantDTO variantDTO = SurveyAnswerVariantDTO.prepare(variant);
                        questionsDTO.get(variant.getSurveyQuestion().getId()).getAnswerVariants().add(variantDTO);
                    }
                }

                for (SurveyPageDTO page : pagesDTO.values()) {
                    surveyDTO.getPages().add(page);
                }

                Collections.sort(surveyDTO.getPages(), new Comparator<SurveyPageDTO>() {
                    @Override
                    public int compare(SurveyPageDTO o1, SurveyPageDTO o2) {
                        return o1.getPageIndex().compareTo(o2.getPageIndex());
                    }
                });

            }
        }
        return surveyDTO;
    }

    @Override
    public EntityListResponse<SurveyAnswerVariantDTO> getSurveyAnswerVariantsPaged(UUID surveyQuestionId, String description, Integer count, Integer page,
                                                                                   Integer start, String sortField, String sortDir) {
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<>();

        if (surveyQuestionId != null) {
            cause += "and el.surveyQuestion.id=:surveyQuestionId ";
            params.put("surveyQuestionId", surveyQuestionId);
        }
        if (!StringUtils.isEmpty(description)) {
            params.put("description", "%%" + StringUtils.lowerCase(description) + "%%");
            cause += "and lower(el.description) like :description ";
        }

        int totalCount = idObjectService.getCount(SurveyAnswerVariant.class, null, null, cause, params);

        EntityListResponse<SurveyAnswerVariantDTO> entityListResponse = new EntityListResponse<>(totalCount, count, page, start);

        List<SurveyAnswerVariant> items = idObjectService.getList(SurveyAnswerVariant.class, null, cause,
                params, sortField, sortDir, entityListResponse.getStartRecord(), count);

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

        entity.setCustomVariant(dto.getCustomVariant());
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
        HashMap<String, Object> params = new HashMap<>();

        if (surveyId != null) {
            cause += "and el.survey.id=:surveyId ";
            params.put("surveyId", surveyId);
        }

        if (!StringUtils.isEmpty(description)) {
            params.put("description", "%%" + StringUtils.lowerCase(description) + "%%");
            cause += "and lower(el.description) like :description ";
        }

        int totalCount = idObjectService.getCount(SurveyPage.class, null, countFetches, cause, params);

        EntityListResponse<SurveyPageDTO> entityListResponse = new EntityListResponse<>(totalCount, count, page, start);

        List<SurveyPage> items = idObjectService.getList(SurveyPage.class, null, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);

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
    public EntityListResponse<SurveyQuestionDTO> getSurveyQuestionsPaged(UUID surveyId, UUID surveyPageId, String text, boolean withVariants, Integer count, Integer page,
                                                                         Integer start, String sortField, String sortDir) {
        String countFetches = "left join el.surveyPage sp ";
        String fetches = "left join el.surveyPage sp ";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<>();

        if (surveyId != null) {
            cause += "and sp.survey.id=:surveyId ";
            params.put("surveyId", surveyId);
        }

        if (surveyPageId != null) {
            cause += "and el.surveyPage.id=:surveyPageId ";
            params.put("surveyPageId", surveyPageId);
        }

        if (!StringUtils.isEmpty(text)) {
            params.put("text", "%%" + StringUtils.lowerCase(text) + "%%");
            cause += "and lower(el.text) like :text ";
        }

        int totalCount = idObjectService.getCount(SurveyQuestion.class, null, countFetches, cause, params);

        EntityListResponse<SurveyQuestionDTO> entityListResponse = new EntityListResponse<>(totalCount, count, page, start);

        List<SurveyQuestion> items = idObjectService.getList(SurveyQuestion.class, fetches, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);
        Set<UUID> questionIds = new HashSet<>();
        List<SurveyAnswerVariant> variants = Collections.emptyList();
        if (withVariants) {
            for (SurveyQuestion surveyQuestion : items) {
                questionIds.add(surveyQuestion.getId());
            }
            if (!questionIds.isEmpty()) {
                Map<String, Object> pms = new HashMap<>();
                pms.put("questionIds", questionIds);
                variants = idObjectService.getList(SurveyAnswerVariant.class, null, "el.surveyQuestion.id in (:questionIds)", pms, "el.sortOrder ASC", null, null);
            }
        }

        for (SurveyQuestion e : items) {
            SurveyQuestionDTO el = SurveyQuestionDTO.prepare(e);
            entityListResponse.addData(el);
            if (withVariants) {
                for (SurveyAnswerVariant v : variants) {
                    if (v.getSurveyQuestion().getId().equals(e.getId())) {
                        SurveyAnswerVariantDTO dto = SurveyAnswerVariantDTO.prepare(v);
                        el.getAnswerVariants().add(dto);
                    }
                }
            }
        }

        return entityListResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyQuestion saveSurveyQuestion(SurveyQuestionDTO dto) throws ObjectNotFoundException, BadDTOException {
        SurveyQuestion entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(SurveyQuestion.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new SurveyQuestion();
        }

        SurveyQuestionType surveyQuestionType = ds.get(SurveyQuestionType.class, dto.getSurveyQuestionTypeId());
        if (surveyQuestionType.getId().equals(DataConstants.QuestionTypes.RATING_SCALE.getValue())) {

            if (dto.getScaleMinValue() == null) throw new BadDTOException("Question \"" + dto.getText() + "\" has no scale min value");
            if (dto.getScaleMaxValue() == null) throw new BadDTOException("Question \"" + dto.getText() + "\" has no scale max value");

            if (dto.getScaleStepValue() == null) dto.setScaleStepValue(1);
            if (dto.getScaleMinValueLabel() == null) dto.setScaleMinValueLabel(dto.getScaleMinValue().toString());
            if (dto.getScaleMaxValueLabel() == null) dto.setScaleMaxValueLabel(dto.getScaleMaxValue().toString());
        }

        if (surveyQuestionType.getId().equals(DataConstants.QuestionTypes.MATRIX_CHECKBOX.getValue()) ||
            surveyQuestionType.getId().equals(DataConstants.QuestionTypes.MATRIX_RADIOBUTTON.getValue())) {
            if (dto.getMatrixColumns() == null || dto.getMatrixColumns().length == 0)
                throw new BadDTOException("Question \"" + dto.getText() + "\" requires at least one column");

            if (dto.getMatrixRows() == null || dto.getMatrixRows().length == 0)
                throw new BadDTOException("Question \"" + dto.getText() + "\" requires at least one row");
        }

        SurveyPage surveyPage = idObjectService.getObjectById(SurveyPage.class, dto.getSurveyPageId());
        if (surveyPage == null) {
            throw new BadDTOException("Survey page for question \"" + dto.getText() + "\" is not found");
        }

        entity.setScaleStepValue(dto.getScaleStepValue());
        entity.setScaleMaxValueLabel(dto.getScaleMaxValueLabel());
        entity.setScaleMinValueLabel(dto.getScaleMinValueLabel());
        entity.setMatrixColumns(dto.getMatrixColumns());
        entity.setMatrixRows(dto.getMatrixRows());
        entity.setQuestionIndex(dto.getQuestionIndex());
        entity.setHidden(dto.getHidden());
        entity.setRequired(dto.getRequired());
        entity.setSurveyPage(surveyPage);
        entity.setText(dto.getText());
        entity.setSurveyQuestionType(surveyQuestionType);
        entity.setScaleMinValue(dto.getScaleMinValue());
        entity.setScaleMaxValue(dto.getScaleMaxValue());
        entity.setAttachmentExtensions(dto.getAttachmentExtensions());
        entity.setDescription(dto.getDescription());
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
        HashMap<String, Object> params = new HashMap<>();

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

        EntityListResponse<SurveyLogicTriggerDTO> entityListResponse = new EntityListResponse<>(totalCount, count, page, start);

        List<SurveyLogicTrigger> items = idObjectService.getList(SurveyLogicTrigger.class, null, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);

        for (SurveyLogicTrigger e : items) {
            SurveyLogicTriggerDTO el = SurveyLogicTriggerDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyLogicTrigger saveSurveyLogicTrigger(SurveyLogicTriggerDTO dto) throws ObjectNotFoundException, BadDTOException {
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

        if (dto.getPageIndex() == null && dto.getLogicActionTypeId().equals(DataConstants.LogicActionTypes.GO_TO_PAGE.getValue())) {
            throw new BadDTOException("expected page index, received null");
        }

        entity.setPageIndex(dto.getPageIndex());
        entity.setInteractionRequired(dto.getInteractionRequired());
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
        HashMap<String, Object> params = new HashMap<>();

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

        EntityListResponse<SurveySessionDTO> entityListResponse = new EntityListResponse<>(totalCount, count, page, start);

        List<SurveySession> items = idObjectService.getList(SurveySession.class, null, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);

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
        entity.setPageVisitHistory(dto.getPageVisitHistory());

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
        HashMap<String, Object> params = new HashMap<>();

        if (surveySessionId != null) {
            cause += String.format("and el.surveySession = '%s' ", surveySessionId);
        }

        int totalCount = idObjectService.getCount(SurveySession.class, null, countFetches, cause, params);

        EntityListResponse<SurveyQuestionAnswerDTO> entityListResponse = new EntityListResponse<>(totalCount, count, page, start);

        List<SurveyQuestionAnswer> items = idObjectService.getList(SurveyQuestionAnswer.class, null, cause, params,
                sortField, sortDir, entityListResponse.getStartRecord(), count);

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
        entity.setSurveyPage(idObjectService.getObjectById(SurveyPage.class, dto.getSurveyPageId()));
        entity.setText(dto.getText());
        entity.setStoredFile(idObjectService.getObjectById(StoredFile.class, dto.getStoredFile()));
        entity.setSelectedMatrixColumn(dto.getSelectedMatrixColumn());
        entity.setSelectedMatrixRow(dto.getSelectedMatrixRow());

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
