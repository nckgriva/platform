package com.gracelogic.platform.survey.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.survey.dto.admin.SurveyAnswerVariantDTO;
import com.gracelogic.platform.survey.dto.admin.SurveyDTO;
import com.gracelogic.platform.survey.dto.admin.SurveyPageDTO;
import com.gracelogic.platform.survey.dto.admin.SurveyQuestionDTO;
import com.gracelogic.platform.survey.dto.user.SurveyIntroductionDTO;
import com.gracelogic.platform.survey.exception.HitRespondentsLimitException;
import com.gracelogic.platform.survey.model.*;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.ForbiddenException;
import com.gracelogic.platform.user.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service("surveyService")
public class SurveyServiceImpl implements SurveyService {

    private static Logger logger = Logger.getLogger(SurveyServiceImpl.class);

    @Autowired
    private IdObjectService idObjectService;

    @Override
    public SurveyIntroductionDTO getSurveyIntroduction(UUID surveyId, String remoteAddress, AuthorizedUser user)
            throws ObjectNotFoundException, ForbiddenException, HitRespondentsLimitException {
        Survey survey = idObjectService.getObjectById(Survey.class, surveyId);
        if (survey == null) {
            throw new ObjectNotFoundException();
        }

        // authorization limit
        if (user == null && survey.getParticipationType() == DataConstants.ParticipationType.AUTHORIZATION_REQUIRED.getValue()) {
            throw new ForbiddenException();
        }

        // ip-limit
        if (survey.getParticipationType() == DataConstants.ParticipationType.IP_LIMITED.getValue() ||
                survey.getParticipationType() == DataConstants.ParticipationType.COOKIE_IP_LIMITED.getValue()) {

            Integer passesFromIP = idObjectService.getCount(SurveyPassing.class, null, null,
                    String.format("el.lastVisitIP = '%s'", remoteAddress),null);

            if (passesFromIP > 0) {
                throw new ForbiddenException();
            }
        }

        // TODO: что если лимит респондентов - 100, и в один момент стартовало 200 человек?
        if (survey.getMaximumRespondents() > 0) {
            Integer totalPasses = idObjectService.getCount(SurveyPassing.class, null, null,
                    String.format("el.SURVEY = '%s' AND el.ENDED IS NOT NULL", survey.getId()), null);
            if (totalPasses >= survey.getMaximumRespondents()) {
                throw new HitRespondentsLimitException();
            }
        }

        Integer totalQuestions = idObjectService.getCount(SurveyQuestion.class, null, null,
                String.format("el.SURVEY_PAGE_ID = '%s'", surveyId), null);

        return new SurveyIntroductionDTO(survey.getIntroduction(), survey.getTimeLimit(), totalQuestions);
    }

    public SurveyPageDTO getSurveyPage(int pageIndex, String remoteAddress, AuthorizedUser user) throws ObjectNotFoundException, ForbiddenException {

        Integer surveyPassings = idObjectService.getCount(SurveyPassing.class, null, null,
                String.format("(el.USER = '%s' OR el.lastVisitIP = '%s') AND el.ENDED IS NULL", user != null ? user.getId() : null, remoteAddress),
                null);

        // похоже, страница была запрошена без старта опроса
        if (surveyPassings <= 0) {
            throw new ForbiddenException();
        }

        List<SurveyPage> surveyPages = idObjectService.getList(SurveyPage.class, null,
                String.format("el.pageIndex = '%s'", pageIndex), null, null, null, null, 1);
        if (surveyPages.size() <= 0) {
            throw new ObjectNotFoundException();
        }

        SurveyPage surveyPage = surveyPages.get(0);

        List<SurveyQuestion> questions = idObjectService.getList(SurveyQuestion.class, null,
                String.format("el.surveyPage = '%s'", surveyPage.getId()), null, "el.sortOrder", null, null, null);

        SurveyPageDTO dto = SurveyPageDTO.prepare(surveyPage);

        SurveyQuestionDTO[] surveyQuestionDTOS = new SurveyQuestionDTO[questions.size()];
        HashMap<UUID, List<SurveyAnswerVariantDTO>> answersCache = new HashMap<>();

        String questionIds = "";

        int i = 0;
        for (SurveyQuestion question : questions) {
            surveyQuestionDTOS[i] = SurveyQuestionDTO.prepare(question);
            answersCache.put(question.getId(), new ArrayList<SurveyAnswerVariantDTO>());

            questionIds += question.getId();
            if (i + 1 != questions.size()) questionIds += ", ";
            i++;
        }

        dto.setQuestions(surveyQuestionDTOS);

        List<SurveyAnswerVariant> variants = idObjectService.getList(SurveyAnswerVariant.class, null,
                String.format("el.surveyQuestion IN ('%s')", questionIds), null, "el.sortOrder",
                null, null, null);


        for (SurveyAnswerVariant variant : variants) {
            answersCache.get(variant.getSurveyQuestion().getId()).add(SurveyAnswerVariantDTO.prepare(variant));
        }

        for (SurveyQuestionDTO questionDTO : dto.getQuestions()) {
            List<SurveyAnswerVariantDTO> answersList = answersCache.get(questionDTO.getId());
            questionDTO.setAnswers(answersList.toArray(new SurveyAnswerVariantDTO[answersList.size()]));
        }

        return dto;
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
}
