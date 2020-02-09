package com.gracelogic.platform.user.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.notification.exception.SendingException;
import com.gracelogic.platform.user.dto.*;
import com.gracelogic.platform.user.exception.*;
import com.gracelogic.platform.user.model.*;
import com.gracelogic.platform.user.security.AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

public interface UserService {
    void changeUserPassword(UUID userId, String newPassword);

    UserSession updateSessionInfo(HttpSession session, AuthenticationToken authenticationToken, String userAgent, boolean isDestroying);

    void changePasswordViaVerificationCode(UUID identifierTypeId, String identifierValue, String verificationCode, String newPassword) throws ObjectNotFoundException, InvalidPassphraseException;

    UserSetting getUserSetting(UUID userId, String key);

    void updateUserSetting(UUID userId, String key, String value);

    void deleteUser(User user);

    void addRoleToUser(User user, Collection<UUID> roleIds);

    User saveUser(UserDTO user, boolean mergeRoles, AuthorizedUser executor) throws ObjectNotFoundException;

    List<UserRole> getUserRoles(UUID userId);

    void mergeUserRoles(UUID userId, Collection<UUID> activeRoles);

    EntityListResponse<UserDTO> getUsersPaged(String identifierValue, Boolean approved, Boolean blocked, Map<String, String> fields, boolean fetchRoles, Integer count, Integer page, Integer start, String sortField, String sortDir);

    UserDTO getUser(UUID userId, boolean fetchRoles) throws ObjectNotFoundException;

    EntityListResponse<RoleDTO> getRolesPaged(String code, String name, boolean fetchGrants, Integer count, Integer page, Integer start, String sortField, String sortDir);

    Role saveRole(RoleDTO dto) throws ObjectNotFoundException;

    RoleDTO getRole(UUID roleId, boolean fetchGrants) throws ObjectNotFoundException;

    void deleteRole(UUID roleId);

    EntityListResponse<UserSessionDTO> getSessionsPaged(UUID userId, String authIp, Date startDate, Date endDate, boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir);

    String translateUserSortFieldToNative(String sortFieldInJPAFormat);

    void changeLocale(HttpServletRequest request, AuthorizedUser authorizedUser, String locale) throws IllegalArgumentException;

    boolean isIdentifierValid(UUID identifierTypeId, String identifierValue);

    UUID resolveIdentifierTypeId(String identifierValue) throws InvalidIdentifierException;

    boolean isPassphraseValueValid(Passphrase passphrase, String value);

    Passphrase getActualPassphrase(User user, UUID passphraseTypeId, UUID referenceObjectId, boolean archiveExpiredPassphrase);

    Identifier findIdentifier(UUID identifierTypeId, String value, boolean enrich);

    List<Identifier> createIdentifiers(List<IdentifierDTO> identifierDTOs, User user, boolean throwExceptionIfAlreadyAttached) throws InvalidIdentifierException;

    void archiveActualPassphrases(UUID userId, UUID passphraseTypeId, UUID referenceObjectId);

    Passphrase updatePassphrase(User user, String value, UUID passphraseTypeId, UUID referenceObjectId, boolean archiveOtherPassphrases) throws InvalidPassphraseException;

    Identifier processSignIn(UUID identifierTypeId, String identifierValue, String password, String remoteAddress) throws UserBlockedException, TooManyAttemptsException, NotAllowedIPException, UserNotApprovedException, InvalidIdentifierException;

    void sendIdentifierVerificationCode(UUID identifierId, Map<String, String> templateParams) throws SendingException;

    Passphrase getActualVerificationCode(User user, UUID referenceObjectId, boolean createNewIfNotExist);

    User processSignUp(SignUpDTO signUpDTO) throws InvalidIdentifierException, InvalidPassphraseException;

    void sendVerificationCodeForPasswordChanging(UUID identifierTypeId, String identifierValue, Map<String, String> templateParams) throws ObjectNotFoundException, TooFastOperationException, SendingException;

    boolean processIdentifierVerificationViaVerificationCode(UUID identifierId, String verificationCode);

}
