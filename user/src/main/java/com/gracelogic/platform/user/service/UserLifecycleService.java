package com.gracelogic.platform.user.service;

import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.UserDTO;
import com.gracelogic.platform.user.exception.IllegalParameterException;
import com.gracelogic.platform.user.model.User;

/**
 * Author: Igor Parkhomenko
 * Date: 17.07.2016
 * Time: 22:27
 */
public interface UserLifecycleService {
    User register(UserDTO user, boolean trust) throws IllegalParameterException;

    void delete(User user);
}
