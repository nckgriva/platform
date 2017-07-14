package com.gracelogic.platform.user.service;

import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.UserDTO;
import com.gracelogic.platform.user.dto.UserRegistrationDTO;
import com.gracelogic.platform.user.exception.*;
import com.gracelogic.platform.user.model.User;

public abstract class AbstractLifecycleService implements UserLifecycleService {
    protected abstract UserService getUserService();

    @Override
    public User register(UserRegistrationDTO userRegistrationDTO, boolean trust) throws InvalidPasswordException, PhoneOrEmailIsNecessaryException, InvalidEmailException, InvalidPhoneException, CustomLocalizedException {
        return getUserService().register(userRegistrationDTO, trust);
    }

    @Override
    public void delete(User user) {
        getUserService().deleteUser(user);
    }

    @Override
    public User save(UserDTO userDTO, boolean mergeRoles, AuthorizedUser executor) throws ObjectNotFoundException {
        return getUserService().saveUser(userDTO, mergeRoles, executor);
    }
}
