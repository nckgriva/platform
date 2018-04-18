package com.gracelogic.platform.feedback.service;

import com.gracelogic.platform.feedback.dao.FeedbackDao;
import com.gracelogic.platform.feedback.dto.FeedbackDTO;
import com.gracelogic.platform.feedback.model.Feedback;
import com.gracelogic.platform.feedback.model.FeedbackType;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.notification.dto.Message;
import com.gracelogic.platform.notification.dto.SendingType;
import com.gracelogic.platform.notification.service.MessageSenderService;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.user.service.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private static Logger logger = Logger.getLogger(FeedbackServiceImpl.class);

    @Autowired
    private FeedbackDao feedbackDao;

    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private MessageSenderService sender;

    @Autowired
    private PropertyService propertyService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveFeedback(FeedbackDTO feedbackDTO) {
        Feedback feedback;
        if (feedbackDTO.getId() != null) {
            feedback = idObjectService.getObjectById(Feedback.class, feedbackDTO.getId());
        }
        else {
            feedback = new Feedback();
        }
        FeedbackType feedbackType = idObjectService.getObjectById(FeedbackType.class, feedbackDTO.getFeedbackTypeId());
        feedback.setFeedbackType(feedbackType);
        feedback.setFields(JsonUtils.mapToJson(feedbackDTO.getFields()));

        idObjectService.save(feedback);

        StringBuilder sb = new StringBuilder("??????:\n");
        sb.append(String.format("???: %s\n", feedbackType.getName()));
        for (String key : feedbackDTO.getFields().keySet()) {
            sb.append(String.format("%s: %s\n", key, feedbackDTO.getFields().get(key)));
        }

        if (!StringUtils.isEmpty(feedbackType.getNotifyEmail())) {
            try {
                sender.sendMessage(new Message(feedbackType.getNotifyEmail(), sb.toString()), SendingType.EMAIL);
            } catch (Exception e) {
                logger.error("Failed to send feedback", e);
            }

        }
    }

    @Override
    public EntityListResponse<FeedbackDTO> getFeedbacksPaged(UUID feedbackTypeId, Date startDate, Date endDate, Map<String, String> fields, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        if (!StringUtils.isEmpty(sortField)) {
            //?.?. ? ?????? ?????? ?????? ???????????? ???????? ? ????????? ????????? ???????????? - ??????????? ???????? jpa ????? ? ???????? sql
            if (StringUtils.equalsIgnoreCase(sortField, "el.id")) {
                sortField = "id";
            }
            if (StringUtils.equalsIgnoreCase(sortField, "el.created")) {
                sortField = "created_dt";
            }
            if (StringUtils.equalsIgnoreCase(sortField, "el.changed")) {
                sortField = "changed_dt";
            }
            if (StringUtils.equalsIgnoreCase(sortField, "el.feedbackType.id")) {
                sortField = "feedback_type_id";
            }
        }

        int totalCount = feedbackDao.getFeedbacksCount(feedbackTypeId, startDate, endDate, fields);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<FeedbackDTO> entityListResponse = new EntityListResponse<FeedbackDTO>();
        entityListResponse.setEntity("feedback");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);

        List<Feedback> items = feedbackDao.getFeedbacks(feedbackTypeId, startDate, endDate, fields, sortField, sortDir, startRecord, count);
        entityListResponse.setPartCount(items.size());
        for (Feedback e : items) {
            FeedbackDTO el = FeedbackDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }


}
