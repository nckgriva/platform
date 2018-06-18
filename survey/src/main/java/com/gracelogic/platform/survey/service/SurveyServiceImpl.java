package com.gracelogic.platform.survey.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.survey.dto.admin.*;
import com.gracelogic.platform.survey.dto.user.PageAnswersDTO;
import com.gracelogic.platform.survey.dto.user.SurveyConclusionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyIntroductionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyPassingDTO;
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
    private static HashMap<SurveyQuestion, SurveyVariantLogic> asVariantLogicHashMap(List<SurveyVariantLogic> list) {
        HashMap<SurveyQuestion, SurveyVariantLogic> hashMap = new HashMap<>();
        for (SurveyVariantLogic variantLogic : list) {
            hashMap.put(variantLogic.getSurveyQuestion(), variantLogic);
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
    public SurveyPassingDTO startSurvey(UUID surveyId, AuthorizedUser user, String remoteAddress)
            throws ObjectNotFoundException {
        Survey survey = idObjectService.getObjectById(Survey.class, surveyId);

        if (survey == null) {
             throw new ObjectNotFoundException();
        }

        SurveyPassing surveyPassing = new SurveyPassing();
        surveyPassing.setStarted(new Date());
        surveyPassing.setLastVisitIP(remoteAddress);
        if (user != null) {
            surveyPassing.setUser(idObjectService.getObjectById(User.class, user.getId()));
        }

        // TODO: если опрос имеет лимит времени, запустить поток (хотя и без потока можно)
        surveyPassing.setLastVisitedPageIndex(0);
        // указываем максимальное количество страниц в опросе один раз
        surveyPassing.setFinishPageIndex(idObjectService.getCount(SurveyPage.class, null, null,
                String.format("el.survey = '%s'", survey.getId()), null));
        surveyPassing.setLink(survey.getLink());
        surveyPassing.setConclusion(survey.getConclusion());
        surveyPassing.setSurvey(survey);

        idObjectService.save(surveyPassing);

        SurveyPassingDTO surveyPassingDTO = new SurveyPassingDTO();
        surveyPassingDTO.setSurveyPassingId(surveyPassing.getId());
        surveyPassingDTO.setSurveyPage(getSurveyPage(surveyPassing, 0));
        return surveyPassingDTO;
    }

    private SurveyPageDTO getSurveyPage(SurveyPassing surveyPassing, int pageIndex) throws ObjectNotFoundException {
        Survey survey = idObjectService.getObjectById(Survey.class, surveyPassing.getSurvey().getId());
        if (survey == null) throw new ObjectNotFoundException();

        List<SurveyPage> surveyPages = idObjectService.getList(SurveyPage.class, null,
                String.format("el.pageIndex = '%s'", pageIndex), null, null, null, null, 1);
        if (surveyPages.size() <= 0) {
            throw new ObjectNotFoundException();
        }

        SurveyPage surveyPage = surveyPages.get(0);
        SurveyPageDTO dto = SurveyPageDTO.prepare(surveyPage);

        // 1. Получение списка вопросов текущей страницы
        List<SurveyQuestion> questions = idObjectService.getList(SurveyQuestion.class, null,
                String.format("el.surveyPage = '%s'", surveyPage.getId()), null, "el.sortOrder", null, null, null);

        String questionIds = "";

        int i = 0;
        for (SurveyQuestion question : questions) {
            questionIds += "'" + question.getId() + "'"; if (i + 1 != questions.size()) questionIds += ", "; i++;
        }

        // 2. Получение логики вариантов ответа. Для веба выбор только HIDE_QUESTION/SHOW_QUESTION
        HashMap<SurveyQuestion, SurveyVariantLogic> logicHashMap = asVariantLogicHashMap(idObjectService.getList(SurveyVariantLogic.class, null,
                String.format("el.surveyQuestion IN (%s) AND (el.logicType = '%s' OR el.logicType = '%s')", questionIds,
                        DataConstants.LogicType.HIDE_QUESTION.getValue(), DataConstants.LogicType.SHOW_QUESTION.getValue()), null, null, null, null));

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

             SurveyVariantLogic variantLogic = logicHashMap.get(question);
             surveyQuestionDTO.setVariantLogic(SurveyVariantLogicDTO.prepare(variantLogic));
             surveyQuestionDTO.setAnswers(answerVariantsDTO);
             surveyQuestionDTOs.add(surveyQuestionDTO);
        }

        dto.setQuestions(surveyQuestionDTOs);

        return dto;
    }

    @Override
    public SurveyPageDTO getSurveyPage(UUID surveyPassingId, int pageIndex)
            throws ObjectNotFoundException, ForbiddenException {
        SurveyPassing surveyPassing = idObjectService.getObjectById(SurveyPassing.class, surveyPassingId);

        if (surveyPassing == null) throw new ObjectNotFoundException();
        if (surveyPassing.getEnded() != null) {
            throw new ForbiddenException(); // TODO: time limit
        }

        return getSurveyPage(surveyPassing, pageIndex);
    }

    @Override
    public SurveyPageDTO continueSurvey(UUID surveyPassingId) throws ObjectNotFoundException {
        SurveyPassing surveyPassing = idObjectService.getObjectById(SurveyPassing.class, surveyPassingId);
        if (surveyPassing == null) throw new ObjectNotFoundException();

        return getSurveyPage(surveyPassing, surveyPassing.getLastVisitedPageIndex());
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyPassingDTO saveAnswersAndContinue(UUID surveyPassingId, PageAnswersDTO dto) throws ObjectNotFoundException, ForbiddenException {
        final SurveyPassing surveyPassing = idObjectService.getObjectById(SurveyPassing.class, surveyPassingId);

        if (surveyPassing == null) throw new ObjectNotFoundException();
        if (surveyPassing.getEnded() != null) throw new ForbiddenException(); // TODO: time limit

        boolean finishSurvey = false;

        int nextPage = surveyPassing.getLastVisitedPageIndex()+1;
        if (nextPage >= surveyPassing.getFinishPageIndex()) { // если на данный момент пользователь проходит опрос на последней странице, это финиш
            finishSurvey = true;
        }

        // список вопросов от полученных ответов
        HashMap<UUID, SurveyQuestion> surveyQuestionsHashMap = asUUIDHashMap(idObjectService.getList(SurveyQuestion.class, null,
                String.format("el.id IN (%s)", dto.getQuestionIdsSeparatedByCommas()),
                null, null, null, null, null));

        // список вариантов ответов
        HashMap<SurveyQuestion, SurveyAnswerVariant> surveyAnswersHashMap = asAnswerVariantHashMap(
                idObjectService.getList(SurveyAnswerVariant.class, null,
                String.format("el.id IN (%s)", dto.getAnswerIdsSeparatedByCommas()),
                null, null, null, null));

        HashMap<SurveyQuestion, SurveyVariantLogic> logicHashMap = asVariantLogicHashMap(
                idObjectService.getList(SurveyVariantLogic.class, null,
                String.format("el.surveyQuestion IN (%s)", dto.getQuestionIdsSeparatedByCommas()),
                        null, null, null, null));

        for (Map.Entry<UUID, SurveyQuestion> entry : surveyQuestionsHashMap.entrySet()) {
            SurveyQuestion question = entry.getValue();
            SurveyAnswerVariant answerVariant = surveyAnswersHashMap.get(entry.getValue());
            String textAnswer = dto.getAnswers().get(entry.getValue().getId()).getTextAnswer();

            boolean hasAnswer = answerVariant != null || textAnswer != null;
            if (question.isRequired() && !hasAnswer) {
                throw new ForbiddenException();
            }

            SurveyVariantLogic variantLogic = logicHashMap.get(question);
            if (variantLogic != null) {
                boolean triggersWhenSelected = variantLogic.isSelectionRequired() && variantLogic.getAnswerVariant() == answerVariant;
                boolean triggersWhenUnselected = !variantLogic.isSelectionRequired() && answerVariant != null && variantLogic.getAnswerVariant() != answerVariant;

                if (triggersWhenSelected || triggersWhenUnselected) {

                    if (variantLogic.getLogicType() == DataConstants.LogicType.CHANGE_CONCLUSION.getValue()) {
                        surveyPassing.setConclusion(variantLogic.getNewConclusion());
                    }

                    if (variantLogic.getLogicType() == DataConstants.LogicType.CHANGE_LINK.getValue()) {
                        surveyPassing.setLink(variantLogic.getNewLink());
                    }

                    if (variantLogic.getLogicType() == DataConstants.LogicType.GO_TO_PAGE.getValue()) {
                        nextPage = variantLogic.getPageIndex();
                    }

                    if (!finishSurvey)
                        finishSurvey = variantLogic.getLogicType() == DataConstants.LogicType.END_SURVEY.getValue();
                }
            }

            SurveyQuestionAnswer surveyQuestionAnswer = new SurveyQuestionAnswer(surveyPassing,
                    question, answerVariant, textAnswer,
                    null); // TODO stored file

            idObjectService.save(surveyQuestionAnswer);
        }

        SurveyPassingDTO surveyPassingDTO = new SurveyPassingDTO();

        if (finishSurvey) {
            SurveyConclusionDTO surveyConclusionDTO = new SurveyConclusionDTO();
            surveyConclusionDTO.setConclusion(surveyPassing.getConclusion());
            surveyConclusionDTO.setLink(surveyPassing.getLink());
            surveyPassingDTO.setSurveyConclusion(surveyConclusionDTO);

            surveyPassing.setEnded(new Date());
        } else {
            surveyPassingDTO.setSurveyPage(getSurveyPage(surveyPassing, nextPage));
            surveyPassing.setLastVisitedPageIndex(nextPage);
        }

        idObjectService.save(surveyPassing);

        return surveyPassingDTO;
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
        entity.setExpires(dto.getExpires());
        entity.setShowProgress(dto.getShowProgress());
        entity.setShowQuestionNumber(dto.getShowQuestionNumber());
        entity.setAllowReturn(dto.getAllowReturn());
        entity.setIntroduction(dto.getIntroduction());
        entity.setConclusion(dto.getConclusion());
        entity.setMaximumRespondents(dto.getMaximumRespondents());
        entity.setTimeLimit(dto.getTimeLimit());
        entity.setParticipationType(dto.getParticipationType());
        entity.setAnswerSavingType(dto.getAnswerSavingType());
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurvey(UUID id) {
        idObjectService.delete(Survey.class, id);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveyAnswerVariant(UUID id) {
        idObjectService.delete(SurveyAnswerVariant.class, id);
    }

    @Override
    public EntityListResponse<SurveyPageDTO> getSurveyPagesPaged(Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveyPage(UUID id) {
        idObjectService.delete(SurveyPage.class, id);
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

        entity.setSortOrder(dto.getSortOrder());
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveyQuestion(UUID id) {
        idObjectService.delete(SurveyQuestion.class, id);
    }

    @Override
    public EntityListResponse<SurveyVariantLogicDTO> getSurveyVariantLogicsPaged(Integer count, Integer page,
                                                                         Integer start, String sortField, String sortDir) {
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        int totalCount = idObjectService.getCount(Survey.class, null, countFetches, cause, params);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<SurveyVariantLogicDTO> entityListResponse = new EntityListResponse<SurveyVariantLogicDTO>();
        entityListResponse.setEntity("surveyVariantLogic");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<SurveyVariantLogic> items = idObjectService.getList(SurveyVariantLogic.class, null, cause, params, sortField, sortDir, startRecord, count);

        entityListResponse.setPartCount(items.size());
        for (SurveyVariantLogic e : items) {
            SurveyVariantLogicDTO el = SurveyVariantLogicDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SurveyVariantLogic saveSurveyVariantLogic(SurveyVariantLogicDTO dto) throws ObjectNotFoundException {
        SurveyVariantLogic entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(SurveyVariantLogic.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new SurveyVariantLogic();
        }

        entity.setAnswerVariant(idObjectService.getObjectById(SurveyAnswerVariant.class, dto.getAnswerVariant()));
        entity.setLogicType(dto.getLogicType());
        entity.setNewConclusion(dto.getNewConclusion());
        entity.setNewLink(dto.getNewLink());
        entity.setPageIndex(dto.getPageIndex());
        entity.setSelectionRequired(dto.isSelectionRequired());
        entity.setSurveyQuestion(idObjectService.getObjectById(SurveyQuestion.class, dto.getSurveyQuestion()));
        if (dto.getTargetQuestion() != null) {
            entity.setTargetQuestion(idObjectService.getObjectById(SurveyQuestion.class, dto.getTargetQuestion()));
        }
        idObjectService.save(entity);
        return entity;
    }


    @Override
    public SurveyVariantLogicDTO getSurveyVariantLogic(UUID id) throws ObjectNotFoundException {
        SurveyVariantLogic entity = idObjectService.getObjectById(SurveyVariantLogic.class, id);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }
        return SurveyVariantLogicDTO.prepare(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSurveyVariantLogic(UUID id) {
        idObjectService.delete(SurveyVariantLogic.class, id);
    }
}
