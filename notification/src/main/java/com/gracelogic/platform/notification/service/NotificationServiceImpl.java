package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.notification.dto.Content;
import com.gracelogic.platform.notification.dto.NotificationDTO;
import com.gracelogic.platform.notification.dto.NotificationSenderResult;
import com.gracelogic.platform.notification.method.push.PushNotificationSender;
import com.gracelogic.platform.notification.model.Notification;
import com.gracelogic.platform.notification.model.NotificationMethod;
import com.gracelogic.platform.notification.model.NotificationState;
import com.gracelogic.platform.notification.method.email.EmailNotificationSender;
import com.gracelogic.platform.notification.method.internal.InternalNotificationSender;
import com.gracelogic.platform.notification.method.sms.SmsNotificationSender;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class NotificationServiceImpl implements NotificationService{
    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    private DictionaryService ds;

    @Autowired
    private EmailNotificationSender emailNotificationSender;

    @Autowired
    private SmsNotificationSender smsNotificationSender;

    @Autowired
    private InternalNotificationSender internalNotificationSender;

    @Autowired
    private PushNotificationSender pushNotificationSender;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public Future<Notification> send(final UUID notificationMethodId, final String source, final String destination, final Content content, final Integer priority) {
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
                notification = notificationService.saveNotification(notification);

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
                return notificationService.saveNotification(notification);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification saveNotification(Notification notification) {
        return idObjectService.save(notification);
    }

    @Override
    public EntityListResponse<NotificationDTO> getNotificationsPaged(String name, UUID notificationMethodId, UUID notificationStateId, boolean enrich,
                                                                     Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = "";
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (!StringUtils.isEmpty(name)) {
            params.put("name", "%%" + StringUtils.lowerCase(name) + "%%");
            cause += " and lower(el.name) like :name";
        }

        if (notificationMethodId != null) {
            params.put("notificationMethodId", notificationMethodId);
            cause += " and el.notificationMethod.id=:notificationMethodId";
        }

        if (notificationStateId != null) {
            params.put("notificationStateId", notificationStateId);
            cause += " and el.notificationState.id=:notificationStateId";
        }


        int totalCount = idObjectService.getCount(Notification.class, null, countFetches, cause, params);

        EntityListResponse<NotificationDTO> entityListResponse = new EntityListResponse<>(totalCount, count, page, start);

        List<Notification> items = idObjectService.getList(Notification.class, fetches, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);
        for (Notification e : items) {
            NotificationDTO el = NotificationDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }
}
