package com.gracelogic.platform.user.service;

import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.exception.IllegalParameterException;
import com.gracelogic.platform.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: Igor Parkhomenko
 * Date: 17.07.2016
 * Time: 22:47
 */
public class AbstractLifecycleService implements UserLifecycleService {
    @Autowired
    private UserService userService;

    @Override
    public User register(AuthorizedUser user, boolean trust) throws IllegalParameterException {
        return userService.register(user, trust);
    }

    @Override
    public void delete(User user) {
        userService.deleteUser(user);
    }
}
