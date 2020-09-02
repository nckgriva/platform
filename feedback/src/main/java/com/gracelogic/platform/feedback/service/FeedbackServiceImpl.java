package com.gracelogic.platform.feedback.service;

import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.feedback.dao.FeedbackDao;
import com.gracelogic.platform.feedback.dto.FeedbackDTO;
import com.gracelogic.platform.feedback.model.Feedback;
import com.gracelogic.platform.feedback.model.FeedbackType;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.notification.dto.Content;
import com.gracelogic.platform.notification.service.DataConstants;
import com.gracelogic.platform.notification.service.NotificationService;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.db.JsonUtils;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private static Logger logger = LoggerFactory.getLogger(FeedbackServiceImpl.class);

    @Autowired
    private FeedbackDao feedbackDao;

    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PropertyService propertyService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Feedback saveFeedback(FeedbackDTO feedbackDTO) {
        Feedback feedback;
        if (feedbackDTO.getId() != null) {
            feedback = idObjectService.getObjectById(Feedback.class, feedbackDTO.getId());
        } else {
            feedback = new Feedback();
        }
        FeedbackType feedbackType = idObjectService.getObjectById(FeedbackType.class, feedbackDTO.getFeedbackTypeId());
        feedback.setFeedbackType(feedbackType);
        feedback.setFields(JsonUtils.mapToJson(feedbackDTO.getFields()));

        feedback = idObjectService.save(feedback);

        StringBuilder sb = new StringBuilder("Feedback:\n");
        sb.append(String.format("Type: %s\n", feedbackType.getName()));
        for (String key : feedbackDTO.getFields().keySet()) {
            sb.append(String.format("%s: %s\n", key, feedbackDTO.getFields().get(key)));
        }

        if (!StringUtils.isEmpty(feedbackType.getNotifyEmail())) {
            try {
                Content content = new Content();
                content.setBody(sb.toString());
                content.setTitle(feedbackType.getName());

                notificationService.send(DataConstants.NotificationMethods.EMAIL.getValue(), propertyService.getPropertyValue("notification:smtp_from"), feedbackType.getNotifyEmail(), content, 0, null);
            } catch (Exception e) {
                logger.error("Failed to send feedback", e);
            }

        }

        return feedback;
    }

    @Override
    public FeedbackDTO getFeedback(UUID id, AuthorizedUser user) throws ObjectNotFoundException {
        Feedback entity = idObjectService.getObjectById(Feedback.class, id);

        if (entity == null) {
            throw new ObjectNotFoundException();
        }

        FeedbackDTO dto = FeedbackDTO.prepare(entity);

        return dto;
    }

    @Override
    public EntityListResponse<FeedbackDTO> getFeedbacksPaged(UUID feedbackTypeId, Date startDate, Date endDate, Map<String, String> fields, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        if (!StringUtils.isEmpty(sortField)) {
            if (StringUtils.equalsIgnoreCase(sortField, "el.id")) {
                sortField = "el.id";
            }
            if (StringUtils.equalsIgnoreCase(sortField, "el.created")) {
                sortField = "el.created_dt";
            }
            if (StringUtils.equalsIgnoreCase(sortField, "el.changed")) {
                sortField = "el.changed_dt";
            }
            if (StringUtils.equalsIgnoreCase(sortField, "el.feedbackType.id")) {
                sortField = "el.feedback_type_id";
            }
        }

        int totalCount = feedbackDao.getFeedbacksCount(feedbackTypeId, startDate, endDate, fields);

        EntityListResponse<FeedbackDTO> entityListResponse = new EntityListResponse<FeedbackDTO>(totalCount, count, page, start);

        List<Feedback> items = feedbackDao.getFeedbacks(feedbackTypeId, startDate, endDate, fields, sortField, sortDir, entityListResponse.getStartRecord(), count);
        for (Feedback e : items) {
            FeedbackDTO el = FeedbackDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }


}
