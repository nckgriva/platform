package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.notification.dto.Message;
import com.gracelogic.platform.notification.dto.SendingType;
import com.gracelogic.platform.notification.email.EmailService;
import com.gracelogic.platform.notification.exception.SendingException;
import com.gracelogic.platform.notification.service.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class MessageSenderServiceImpl implements MessageSenderService {

    @Autowired
    private SmsService smsService;

    @Autowired
    private EmailService emailService;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Future<Boolean> sendMessage(Message message, SendingType type) throws SendingException {
        try {
            switch (type) {
                case EMAIL:
                    return sendEmail(message);
                case SMS:
                    return sendSms(message);
            }
        } catch (Exception e) {
            throw new SendingException("Failed to send message");
        }
        throw new UnsupportedOperationException("Unknown sending type " + type);
    }

    private Future<Boolean> sendEmail(final Message message) throws ExecutionException, InterruptedException {
        return executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return emailService.sendMessage(message);
            }
        });
    }

    private Future<Boolean> sendSms(final Message message) throws ExecutionException, InterruptedException {
        return executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return smsService.sendSms(message);
            }
        });
    }

}
