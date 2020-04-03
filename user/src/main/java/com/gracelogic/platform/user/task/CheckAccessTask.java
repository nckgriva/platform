package com.gracelogic.platform.user.task;

import com.gracelogic.platform.user.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("checkAccessTask")
public class CheckAccessTask {
    private static Logger logger = Logger.getLogger(CheckAccessTask.class);

    @Autowired
    private UserService userService;

    @Transactional
    public void execute(String parameter) {
        try {
            userService.blockExpiredUsers();
        }
        catch (Exception e) {
            logger.error("Failed check access task", e);
        }
    }
}
