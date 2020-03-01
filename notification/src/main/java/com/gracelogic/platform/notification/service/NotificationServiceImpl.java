package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.notification.dto.NotificationDTO;
import com.gracelogic.platform.notification.model.Notification;
import com.gracelogic.platform.notification.model.NotificationMethod;
import com.gracelogic.platform.notification.model.NotificationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

public class NotificationServiceImpl implements NotificationService{
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private IdObjectService idObjectService;

    @Override
    public boolean send(String source, String destination, String content, UUID notificationMethodId) {
        NotificationDTO dto = new NotificationDTO(source, destination, content, null, DataConstants.NotificationStates.QUEUED.getValue(), notificationMethodId);

        try {
            saveNotification(dto);
            return true;
        } catch (ObjectNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean supports(UUID notificationMethodId) {
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification saveNotification(NotificationDTO dto) throws ObjectNotFoundException {
        Notification notification;
        if (dto.getId() != null) {
            notification = idObjectService.getObjectById(Notification.class, dto.getId());
            if (notification == null) {
                throw new ObjectNotFoundException();
            }
        }
        else {
            notification = new Notification();
        }

        notification.setSource(dto.getSource());
        notification.setDestination(dto.getDestination());
        notification.setContent(dto.getContent());
        notification.setPriority(dto.getPriority());
        notification.setNotificationState(idObjectService.getObjectById(NotificationState.class, dto.getNotificationStateId()));
        notification.setNotificationMethod(idObjectService.getObjectById(NotificationMethod.class, dto.getNotificationMethodId()));

        notification = idObjectService.save(notification);

        return notification;
    }
}
