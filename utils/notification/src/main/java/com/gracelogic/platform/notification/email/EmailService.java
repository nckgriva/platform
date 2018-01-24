package com.gracelogic.platform.notification.email;

import com.gracelogic.platform.notification.dto.Message;

public interface EmailService {
    boolean sendMessage(Message message);
}
