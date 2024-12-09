package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.db.JsonUtils;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.notification.dto.Content;
import com.gracelogic.platform.notification.dto.NotificationDTO;
import com.gracelogic.platform.notification.dto.NotificationSenderResult;
import com.gracelogic.platform.notification.model.Notification;
import com.gracelogic.platform.notification.model.NotificationMethod;
import com.gracelogic.platform.notification.model.NotificationState;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private DictionaryService ds;

    @Autowired
    @Qualifier("emailNotificationSender")
    private NotificationSender emailNotificationSender;

    @Autowired
    @Qualifier("smsNotificationSender")
    private NotificationSender smsNotificationSender;

    @Autowired
    @Qualifier("internalNotificationSender")
    private NotificationSender internalNotificationSender;

    @Autowired
    @Qualifier("pushNotificationSender")
    private NotificationSender pushNotificationSender;

    @Autowired
    @Qualifier("telegramNotificationSender")
    private NotificationSender telegramNotificationSender;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public Future<Notification> send(final UUID notificationMethodId, final String source, final String destination, final Content content, final Integer priority, final UUID referenceObjectId) {
        return executorService.submit(new Callable<Notification>() {
            @Override
            public Notification call() throws Exception {
                //Create and save notification in separate transaction
                Notification notification = new Notification();
                notification.setNotificationMethod(ds.get(NotificationMethod.class, notificationMethodId));
                notification.setNotificationState(ds.get(NotificationState.class, DataConstants.NotificationStates.QUEUED.getValue()));
                notification.setTitle(content.getTitle());
                notification.setBody(content.getBody());
                notification.setFields(JsonUtils.mapToJson(content.getFields()));
                notification.setSource(source);
                notification.setDestination(destination);
                notification.setPriority(priority);
                notification.setReferenceObjectId(referenceObjectId);
                notification = saveNotification(notification);

                //Send notification
                NotificationSenderResult result;
                try {
                    if (notificationMethodId.equals(DataConstants.NotificationMethods.EMAIL.getValue())) {
                        result = emailNotificationSender.send(source, destination, content);
                    } else if (notificationMethodId.equals(DataConstants.NotificationMethods.SMS.getValue())) {
                        result = smsNotificationSender.send(source, destination, content);
                    } else if (notificationMethodId.equals(DataConstants.NotificationMethods.INTERNAL.getValue())) {
                        result = internalNotificationSender.send(source, destination, content);
                    } else if (notificationMethodId.equals(DataConstants.NotificationMethods.PUSH.getValue())) {
                        result = pushNotificationSender.send(source, destination, content);
                    } else if (notificationMethodId.equals(DataConstants.NotificationMethods.TELEGRAM.getValue())) {
                        result = telegramNotificationSender.send(source, destination, content);
                    } else {
                        result = new NotificationSenderResult(false, "Method is not implemented");
                    }
                } catch (Exception e) {
                    result = new NotificationSenderResult(false, e.getMessage());
                }

                notification.setNotificationState(result.isSuccess() ?
                        ds.get(NotificationState.class, DataConstants.NotificationStates.SENT.getValue()) :
                        ds.get(NotificationState.class, DataConstants.NotificationStates.ERROR.getValue()));
                notification.setErrorDescription(result.getErrorDescription());
                return saveNotification(notification);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification saveNotification(Notification notification) {
        return idObjectService.save(notification);
    }

    @Override
    public EntityListResponse<NotificationDTO> getNotificationsPaged(String name, String destination, UUID notificationMethodId, UUID notificationStateId, UUID referenceObjectId, boolean enrich,
                                                                     boolean calculate, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = enrich ? "left join fetch el.notificationState left join fetch el.notificationMethod" : "";
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();


        if (!StringUtils.isEmpty(name)) {
            params.put("name", "%%" + StringUtils.lowerCase(name) + "%%");
            cause += " and lower(el.name) like :name";
        }

        if (!StringUtils.isEmpty(destination)) {
            params.put("destination", "%%" + StringUtils.lowerCase(destination) + "%%");
            cause += " and lower(el.destination) like :destination";
        }

        if (notificationMethodId != null) {
            params.put("notificationMethodId", notificationMethodId);
            cause += " and el.notificationMethod.id=:notificationMethodId";
        }

        if (notificationStateId != null) {
            params.put("notificationStateId", notificationStateId);
            cause += " and el.notificationState.id=:notificationStateId";
        }

        if (referenceObjectId != null) {
            params.put("referenceObjectId", referenceObjectId);
            cause += " and el.referenceObjectId=:referenceObjectId";
        }

        Integer totalCount = calculate ? idObjectService.getCount(Notification.class, null, countFetches, cause, params) : null;

        EntityListResponse<NotificationDTO> entityListResponse = new EntityListResponse<>(totalCount, count, page, start);

        List<Notification> items = idObjectService.getList(Notification.class, fetches, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);
        for (Notification e : items) {
            NotificationDTO el = NotificationDTO.prepare(e);
            if (enrich) {
                NotificationDTO.enrich(el, e);
            }
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }
}
