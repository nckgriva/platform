package com.gracelogic.platform.user.service;

import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.UserDTO;
import com.gracelogic.platform.user.dto.UserRegistrationDTO;
import com.gracelogic.platform.user.exception.*;
import com.gracelogic.platform.user.model.User;

import javax.servlet.http.HttpSession;

public interface UserLifecycleService {
    User register(UserRegistrationDTO userRegistrationDTO, boolean trust) throws InvalidPasswordException, PhoneOrEmailIsNecessaryException, InvalidEmailException, InvalidPhoneException, CustomLocalizedException;

    void delete(User user);

    User save(UserDTO userDTO, boolean mergeRoles, AuthorizedUser executor) throws ObjectNotFoundException;

    void login(AuthorizedUser authorizedUser, HttpSession httpSession);
}
