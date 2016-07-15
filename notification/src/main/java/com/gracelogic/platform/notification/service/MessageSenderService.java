package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.notification.exception.SendingException;
import com.gracelogic.platform.notification.dto.SendingType;
import com.gracelogic.platform.notification.dto.Message;

import java.util.concurrent.Future;

public interface MessageSenderService {
    Future<Boolean> sendMessage(Message message, SendingType type) throws SendingException;
}
