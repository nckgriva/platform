package com.gracelogic.platform.user.service;

import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.SignUpDTO;
import com.gracelogic.platform.user.dto.UserDTO;
import com.gracelogic.platform.user.exception.*;
import com.gracelogic.platform.user.model.User;

import javax.servlet.http.HttpSession;

public interface UserLifecycleService {
    User signUp(SignUpDTO signUpDTO) throws InvalidIdentifierException, InvalidPassphraseException, CustomLocalizedException;

    void signIn(AuthorizedUser authorizedUser, HttpSession httpSession) throws UserBlockedException, TooManyAttemptsException, NotAllowedIPException, UserNotApprovedException, InvalidIdentifierException;

    User save(UserDTO userDTO, boolean mergeRoles, AuthorizedUser executor) throws ObjectNotFoundException;

    void delete(User user);
}
