package com.gracelogic.platform.user.service;

import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.SignUpDTO;
import com.gracelogic.platform.user.dto.UserDTO;
import com.gracelogic.platform.user.exception.*;
import com.gracelogic.platform.user.model.User;

public abstract class AbstractLifecycleService implements UserLifecycleService {
    protected abstract UserService getUserService();

    @Override
    public User signUp(SignUpDTO signUpDTO) throws InvalidIdentifierException, InvalidPassphraseException, CustomLocalizedException {
        return getUserService().processSignUp(signUpDTO);
    }

    @Override
    public void delete(User user) {
        getUserService().deleteUser(user);
    }

    @Override
    public User save(UserDTO userDTO, boolean mergeRoles, boolean mergeIdentifiers, AuthorizedUser executor) throws ObjectNotFoundException {
        return getUserService().saveUser(userDTO, mergeRoles, mergeIdentifiers, executor);
    }
}
