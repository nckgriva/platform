package com.gracelogic.platform.notification.email;

import com.gracelogic.platform.notification.dto.Message;

/**
 * Author: Igor Parkhomenko
 * Date: 13.11.12
 * Time: 20:57
 */
public interface EmailService {
    boolean sendMessage(Message message);
}
