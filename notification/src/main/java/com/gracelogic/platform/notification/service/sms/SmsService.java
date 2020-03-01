package com.gracelogic.platform.notification.service.sms;

import com.gracelogic.platform.notification.dto.Message;

public interface SmsService {
    boolean sendSms(Message message);
}
