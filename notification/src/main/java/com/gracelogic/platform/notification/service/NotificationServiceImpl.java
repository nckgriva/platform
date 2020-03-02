package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.notification.dto.NotificationSenderResult;
import com.gracelogic.platform.notification.model.Notification;
import com.gracelogic.platform.notification.model.NotificationMethod;
import com.gracelogic.platform.notification.model.NotificationState;
import com.gracelogic.platform.notification.service.method.EmailNotificationSender;
import com.gracelogic.platform.notification.service.method.InternalNotificationSender;
import com.gracelogic.platform.notification.service.method.SmsNotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public Future<Notification> send(final UUID notificationMethodId, final String source, final String destination, final String content, final String preview, final Integer priority) {
        return executorService.submit(new Callable<Notification>() {
            @Override
            public Notification call() throws Exception {
                //Create and save notification in separate transaction
                Notification notification = new Notification();
                notification.setNotificationMethod(ds.get(NotificationMethod.class, notificationMethodId));
                notification.setNotificationState(ds.get(NotificationState.class, DataConstants.NotificationStates.QUEUED.getValue()));
                notification.setContent(content);
                notification.setSource(source);
                notification.setDestination(destination);
                notification.setPriority(priority);
                notification = notificationService.saveNotification(notification);

                //Send notification
                NotificationSenderResult result;
                try {
                    if (notificationMethodId.equals(DataConstants.NotificationMethods.EMAIL.getValue())) {
                        result = emailNotificationSender.send(source, destination, content, preview);
                    } else if (notificationMethodId.equals(DataConstants.NotificationMethods.SMS.getValue())) {
                        result = smsNotificationSender.send(source, destination, content, preview);
                    } else if (notificationMethodId.equals(DataConstants.NotificationMethods.INTERNAL.getValue())) {
                        result = internalNotificationSender.send(source, destination, content, preview);
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
}
