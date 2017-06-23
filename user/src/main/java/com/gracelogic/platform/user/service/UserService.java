package com.gracelogic.platform.user.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.notification.exception.SendingException;
import com.gracelogic.platform.user.dto.*;
import com.gracelogic.platform.user.exception.*;
import com.gracelogic.platform.user.model.*;
import com.gracelogic.platform.user.security.AuthenticationToken;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Author: Igor Parkhomenko
 * Date: 23.07.13
 * Time: 13:09
 */
public interface UserService {
    User getUserByField(String fieldName, Object fieldValue);

    User login(Object login, String loginField, String password, String remoteAddress, boolean trust);

    void changeUserPassword(UUID userId, String newPassword);

    boolean checkPhone(String phone, boolean checkAvailability);

    boolean checkEmail(String email, boolean checkAvailability);

    boolean checkPassword(String value);

    boolean verifyLogin(UUID userId, String loginType, String code);

    UserSession updateSessionInfo(HttpSession session, AuthenticationToken authenticationToken, String userAgent, boolean isDestroying);

    void sendRepairCode(String login, String loginType, Map<String, String> templateParams) throws ObjectNotFoundException, TooFastOperationException, SendingException;

    void changePassword(String login, String loginType, String code, String newPassword) throws ObjectNotFoundException, IncorrectAuthCodeException;

    UserSetting getUserSetting(UUID userId, String key);

    void updateUserSetting(UUID userId, String key, String value);

    AuthCode getActualCode(UUID userId, UUID codeTypeId, boolean invalidateImmediately);

    void invalidateCodes(UUID userId, UUID codeTypeId);

    boolean isActualCodeAvailable(UUID userId, UUID codeTypeId);

    User register(UserRegistrationDTO userRegistrationDTO, boolean trust) throws InvalidPasswordException, PhoneOrEmailIsNecessaryException, InvalidEmailException, InvalidPhoneException;

    void deleteUser(User user);

    void sendVerificationCode(User user, String loginType, Map<String, String> templateParams) throws SendingException;

    void addRoleToUser(User user, Collection<UUID> roleIds);

    User saveUser(UserDTO user, boolean mergeRoles, AuthorizedUser executor) throws ObjectNotFoundException;

    List<UserRole> getUserRoles(UUID userId);

    void mergeUserRoles(UUID userId, Collection<UUID> activeRoles);

    EntityListResponse<UserDTO> getUsersPaged(String phone, String email, Boolean approved, Boolean blocked, Map<String, String> fields, boolean fetchRoles, Integer count, Integer page, Integer start, String sortField, String sortDir);

    UserDTO getUser(UUID userId, boolean fetchRoles) throws ObjectNotFoundException;

    EntityListResponse<RoleDTO> getRolesPaged(String code, String name, Collection<UUID> grantsIds, Integer count, Integer page, Integer start, String sortField, String sortDir);

    Role saveRole(RoleDTO dto) throws ObjectNotFoundException;

    RoleDTO getRole(UUID roleId) throws ObjectNotFoundException;

    void deleteRole(UUID roleId);
}
