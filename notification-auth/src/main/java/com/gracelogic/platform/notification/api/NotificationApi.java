package com.gracelogic.platform.notification.api;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.notification.Path;
import com.gracelogic.platform.notification.dto.NotificationDTO;
import com.gracelogic.platform.notification.service.NotificationService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_NOTIFICATION)
public class NotificationApi extends AbstractAuthorizedController {
    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private NotificationService notificationService;

    @PreAuthorize("hasAuthority('NOTIFICATION:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getNotifications(@RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "destination", required = false) String destination,
                                           @RequestParam(value = "notificationMethodId", required = false) UUID notificationMethodId,
                                           @RequestParam(value = "notificationStateId", required = false) UUID notificationStateId,
                                           @RequestParam(value = "referenceObjectId", required = false) UUID referenceObjectId,
                                           @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                           @RequestParam(value = "calculate", required = false, defaultValue = "false") Boolean calculate,
                                           @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                           @RequestParam(value = "page", required = false) Integer page,
                                           @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                           @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                           @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        EntityListResponse<NotificationDTO> notifications = notificationService.getNotificationsPaged(name, destination, notificationMethodId, notificationStateId, referenceObjectId, enrich, calculate, count, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<NotificationDTO>>(notifications, HttpStatus.OK);
    }
}

