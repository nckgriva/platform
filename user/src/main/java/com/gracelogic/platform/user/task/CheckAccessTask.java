package com.gracelogic.platform.user.task;

import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.user.model.Token;
import com.gracelogic.platform.user.model.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("checkAccessTask")
public class CheckAccessTask {

    private static Logger logger = Logger.getLogger(CheckAccessTask.class);

    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private PropertyService propertyService;

    @Transactional
    public void execute(String parameter) {
        try {

            // block users
            String cause = " el.blocked = false and el.blockAfterDt < :currentDateTime ";
            Map<String, Object> param = new HashMap<>();
            param.put("currentDateTime", new Date());
            List<User> users = idObjectService.getList(User.class, null, cause, param, null, null, null);
            for (User user : users) {
                user.setBlocked(true);
                user.setBlockedDt(new Date());
                idObjectService.save(user);
            }

            // block tokens
            Long lifeTime = propertyService.getPropertyValueAsLong("user:token_lifetime");
            if (lifeTime != null) {
                cause = " el.active = true ";
                List<Token> activeTokens = idObjectService.getList(Token.class, null, cause, null, null, null, null);
                for (Token token : activeTokens) {
                    if (token.getLastRequest().getTime() + lifeTime < new Date().getTime()) {
                        token.setActive(false);
                        idObjectService.save(token);
                    }
                }
            }

        }
        catch (Exception e) {
            logger.error("Failed check access task", e);
        }
    }
}
