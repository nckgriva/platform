package com.gracelogic.platform.user.service;

import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.UserDTO;
import com.gracelogic.platform.user.dto.UserRegistrationDTO;
import com.gracelogic.platform.user.exception.*;
import com.gracelogic.platform.user.model.User;

/**
 * Author: Igor Parkhomenko
 * Date: 17.07.2016
 * Time: 22:27
 */
public interface UserLifecycleService {
    User register(UserRegistrationDTO userRegistrationDTO, boolean trust) throws InvalidPasswordException, PhoneOrEmailIsNecessaryException, InvalidEmailException, InvalidPhoneException, CustomLocalizedException;

    void delete(User user);

    User save(UserDTO userDTO, boolean mergeRoles, AuthorizedUser executor) throws ObjectNotFoundException;
}
