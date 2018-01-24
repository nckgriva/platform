package com.gracelogic.platform.notification.sms;

import com.gracelogic.platform.notification.dto.Message;

public interface SmsService {
    boolean sendSms(Message message);
}
