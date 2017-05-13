package com.gracelogic.platform.user.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.notification.dto.Message;
import com.gracelogic.platform.notification.dto.SendingType;
import com.gracelogic.platform.notification.exception.SendingException;
import com.gracelogic.platform.notification.service.MessageSenderService;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.template.dto.LoadedTemplate;
import com.gracelogic.platform.template.service.TemplateService;
import com.gracelogic.platform.user.dao.UserDao;
import com.gracelogic.platform.user.dto.AuthorizedUser;
import com.gracelogic.platform.user.dto.UserDTO;
import com.gracelogic.platform.user.dto.UserRegistrationDTO;
import com.gracelogic.platform.user.exception.*;
import com.gracelogic.platform.user.model.*;
import com.gracelogic.platform.user.security.AuthenticationToken;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Igor Parkhomenko
 * Date: 23.07.13
 * Time: 13:09
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    private static Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private MessageSenderService messageSenderService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private DictionaryService ds;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserLifecycleService lifecycleService;

    @Autowired
    private TemplateService templateService;

    @Override
    public User getUserByField(String fieldName, Object fieldValue) {
        return userDao.getUserByField(fieldName, fieldValue);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public User login(Object login, String loginField, String password, String remoteAddress, boolean trust) throws UserBlockedException, TooManyAttemptsException, NotAllowedIPException, UserNotActivatedException {
        User user = userDao.getUserByField(loginField, login);
//        logger.info("USER: " + user != null ? user.getId().toString() : "NULL");
        if (user != null) {
            boolean loginTypeVerified = false;
            if (trust) {
                loginTypeVerified = true;
            } else {
                if (loginField.equalsIgnoreCase("email")) {
                    loginTypeVerified = user.getEmailVerified();
                } else if (loginField.equalsIgnoreCase("phone")) {
                    loginTypeVerified = user.getPhoneVerified();
                }
            }

            if (!user.getApproved()) {
                throw new UserNotActivatedException("UserNotActivatedException");
            }
            if (user.getBlocked() != null && user.getBlocked()) {
                throw new UserBlockedException("UserBlockedException");
            }
            if (user.getAllowedAddresses() != null && !user.getAllowedAddresses().contains(remoteAddress)) {
                throw new NotAllowedIPException("NotAllowedIPException");
            }

            if (user.getApproved() && loginTypeVerified &&
                    (user.getAllowedAddresses() == null || user.getAllowedAddresses().contains(remoteAddress))) {
                Long currentTimeMillis = System.currentTimeMillis();
                Date endDate = new Date(currentTimeMillis);
                Date startDate = new Date(currentTimeMillis - propertyService.getPropertyValueAsInteger("user:block_period"));

                Long incorrectLoginAttemptCount = userDao.getIncorrectLoginAttemptCount(user.getId(), startDate, endDate);
                if (incorrectLoginAttemptCount < propertyService.getPropertyValueAsInteger("user:attempts_to_block")) {
                    if (trust || user.getPassword() != null && user.getPassword().equals(DigestUtils.shaHex(password.concat(user.getSalt())))) {
                        user.setLastVisitDt(new Date());
                        user.setLastVisitIP(remoteAddress);
                        user = idObjectService.save(user);
                        return user;
                    } else {
                        IncorrectLoginAttempt incorrectLoginAttempt = new IncorrectLoginAttempt();
                        incorrectLoginAttempt.setUser(user);
                        userDao.saveIncorrectLoginAttempt(incorrectLoginAttempt);
                    }
                } else {
                    throw new TooManyAttemptsException("TooManyAttemptsException");
                }
            }
        }
        return null;
    }

    @Transactional
    @Override
    public void changeUserPassword(UUID userId, String newPassword) {
        User user = idObjectService.getObjectById(User.class, userId);
        user.setSalt(UserServiceImpl.generatePasswordSalt());
        user.setPassword(DigestUtils.shaHex(newPassword.concat(user.getSalt())));
        idObjectService.save(user);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        idObjectService.delete(IncorrectLoginAttempt.class, "el.user.id=:userId", params);
    }

    private static String generatePasswordSalt() {
        Random random = new Random(System.currentTimeMillis());
        return DigestUtils.md5Hex(String.valueOf(random.nextLong()));
    }

    @Override
    public boolean checkPhone(String value, boolean checkAvailability) {
        if (!StringUtils.isEmpty(value) && value.length() > 0) {
            //^7\\d{10}$
            Pattern p = Pattern.compile(propertyService.getPropertyValue("user:phone_validation_exp"));
            Matcher m = p.matcher(value);
            boolean result = m.matches();
            if (checkAvailability) {
                Map<String, Object> params = new HashMap<>();
                params.put("phone", value);

                return result && idObjectService.checkExist(User.class, null, "el.phone=:phone and el.phoneVerified=true", params, 1) == 0;
            }
            return result;
        }
        return false;
    }

    @Override
    public boolean checkEmail(String value, boolean checkAvailability) {
        if (!StringUtils.isEmpty(value) && value.length() > 0) {
            //^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$
            Pattern p = Pattern.compile(propertyService.getPropertyValue("user:email_validation_exp"));
            Matcher m = p.matcher(value);
            boolean result = m.matches();
            if (checkAvailability) {
                Map<String, Object> params = new HashMap<>();
                params.put("email", value);

                return result && idObjectService.checkExist(User.class, null, "el.email=:email and el.emailVerified=true", params, 1) == 0;
            }
            return result;
        }
        return false;
    }

    @Override
    public boolean checkPassword(String value) {
        boolean result = false;
        if (!StringUtils.isEmpty(value) && value.length() > 0) {
            //.+
            Pattern p = Pattern.compile(propertyService.getPropertyValue("user:password_validation_exp"));
            Matcher m = p.matcher(value);
            result = m.matches();
        }
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean verifyLogin(UUID userId, String loginType, String code) {
        String userApproveMethod = propertyService.getPropertyValue("user:approve_method");

        User user = idObjectService.getObjectById(User.class, userId);
        if (user != null) {
            if (loginType.equalsIgnoreCase("phone") && !user.getPhoneVerified()) {
                AuthCode phoneCode = getActualCode(userId, DataConstants.AuthCodeTypes.PHONE_VERIFY.getValue(), false);

                if (phoneCode.getCode().equalsIgnoreCase(code)) {
                    user.setPhoneVerified(true);
                    if (StringUtils.equalsIgnoreCase(userApproveMethod, DataConstants.UserApproveMethod.PHONE_CONFIRMATION.getValue())) {
                        user.setApproved(true);
                    } else if (StringUtils.equalsIgnoreCase(userApproveMethod, DataConstants.UserApproveMethod.EMAIL_AND_PHONE_CONFIRMATION.getValue()) && user.getEmailVerified()) {
                        user.setApproved(true);
                    }


                    idObjectService.save(user);

                    invalidateCodes(userId, DataConstants.AuthCodeTypes.PHONE_VERIFY.getValue());
                    return true;
                }
            } else if (loginType.equalsIgnoreCase("email") && !user.getEmailVerified()) {
                AuthCode emailCode = getActualCode(userId, DataConstants.AuthCodeTypes.EMAIL_VERIFY.getValue(), false);
                if (emailCode.getCode().equalsIgnoreCase(code)) {
                    user.setEmailVerified(true);
                    if (StringUtils.equalsIgnoreCase(userApproveMethod, DataConstants.UserApproveMethod.EMAIL_CONFIRMATION.getValue())) {
                        user.setApproved(true);
                    } else if (StringUtils.equalsIgnoreCase(userApproveMethod, DataConstants.UserApproveMethod.EMAIL_AND_PHONE_CONFIRMATION.getValue()) && user.getPhoneVerified()) {
                        user.setApproved(true);
                    }
                    idObjectService.save(user);

                    invalidateCodes(userId, DataConstants.AuthCodeTypes.EMAIL_VERIFY.getValue());
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserSession updateSessionInfo(HttpSession session, AuthenticationToken authenticationToken, String userAgent, boolean isDestroying) {
        if (!StringUtils.isEmpty(session.getId())) {
            AuthenticationToken authentication = null;
            try {
                authentication = (AuthenticationToken) ((org.springframework.security.core.context.SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT")).getAuthentication();
            } catch (Exception ignored) {
            }

            if (authentication == null) {
                authentication = authenticationToken;
            }

            if (authentication != null && authentication.getDetails() != null && authentication.getDetails() instanceof AuthorizedUser) {
                AuthorizedUser authorizedUser = (AuthorizedUser) authentication.getDetails();
                UserSession userSession = null;

                Map<String, Object> params = new HashMap<>();
                params.put("sessionId", session.getId());

                List<UserSession> userSessions = idObjectService.getList(UserSession.class, null, "el.sessionId=:sessionId", params, null, null, null, 1);
                if (userSessions != null && !userSessions.isEmpty()) {
                    userSession = userSessions.iterator().next();
                }

                if (userSession == null) {
                    userSession = new UserSession();
                    userSession.setSessionId(session.getId());
                    userSession.setUser(idObjectService.setIfModified(User.class, userSession.getUser(), authorizedUser.getId()));
                    userSession.setAuthIp(authentication.getRemoteAddress());
                    userSession.setLoginType(authentication.getLoginType());
                    userSession.setUserAgent(userAgent);
                }
                userSession.setSessionCreatedDt(new Date(session.getCreationTime()));
                userSession.setLastAccessDt(new Date(session.getLastAccessedTime()));
                //userSession.setThisAccessedTime(session.getLastAccessedTime());
                userSession.setMaxInactiveInterval((long) session.getMaxInactiveInterval());
                userSession.setValid(!isDestroying);
                return idObjectService.save(userSession);

            }
        }
        return null;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void sendRepairCode(String login, String loginType, Map<String, String> templateParams) throws ObjectNotFoundException, TooFastOperationException, SendingException {
        User user = userDao.getUserByField(loginType, login);

        if (user != null && user.getApproved()) {
            if (templateParams == null) {
                templateParams = new HashMap<>();
            }
            //templateParams.put("userId", user.getId().toString());
            templateParams.put("loginType", loginType);
            templateParams.put("login", login);
            templateParams.put("baseUrl", propertyService.getPropertyValue("web:base_url"));
            Map<String, String> fields = JsonUtils.jsonToMap(user.getFields());
            for (String key : fields.keySet()) {
                templateParams.put(key, fields.get(key));
            }

            boolean isActualCodeAvailable = isActualCodeAvailable(user.getId(), DataConstants.AuthCodeTypes.PASSWORD_REPAIR.getValue());
            AuthCode authCode = getActualCode(user.getId(), DataConstants.AuthCodeTypes.PASSWORD_REPAIR.getValue(), false);
            if ((System.currentTimeMillis() - authCode.getCreated().getTime() > Long.parseLong(propertyService.getPropertyValue("user:action_delay"))) || !isActualCodeAvailable) {
                if (isActualCodeAvailable) {
                    authCode = getActualCode(user.getId(), DataConstants.AuthCodeTypes.PASSWORD_REPAIR.getValue(), true);
                }
                if (!StringUtils.isEmpty(user.getPhone()) && user.getPhoneVerified()) {
                    try {
                        LoadedTemplate template = templateService.load("sms_repair_code");
                        templateParams.put("code", authCode.getCode());

                        messageSenderService.sendMessage(new Message(propertyService.getPropertyValue("notification:sms_from"), user.getPhone(), template.getSubject(), templateService.apply(template, templateParams)), SendingType.SMS);
                    } catch (IOException e) {
                        logger.error(e);
                        throw new SendingException(e.getMessage());
                    }
                } else if (!StringUtils.isEmpty(user.getEmail()) && user.getEmailVerified()) {
                    try {
                        LoadedTemplate template = templateService.load("email_repair_code");
                        templateParams.put("code", authCode.getCode());

                        messageSenderService.sendMessage(new Message(propertyService.getPropertyValue("notification:smtp_from"), user.getEmail(), template.getSubject(), templateService.apply(template, templateParams)), SendingType.EMAIL);
                    } catch (IOException e) {
                        logger.error(e);
                        throw new SendingException(e.getMessage());
                    }
                }
            } else {
                throw new TooFastOperationException();
            }
        } else {
            throw new ObjectNotFoundException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changePassword(String login, String loginType, String code, String newPassword) throws ObjectNotFoundException, IncorrectAuthCodeException {
        User user = userDao.getUserByField(loginType, login);

        if (user != null && isActualCodeAvailable(user.getId(), DataConstants.AuthCodeTypes.PASSWORD_REPAIR.getValue())) {
            AuthCode authCode = getActualCode(user.getId(), DataConstants.AuthCodeTypes.PASSWORD_REPAIR.getValue(), false);
            invalidateCodes(user.getId(), DataConstants.AuthCodeTypes.PASSWORD_REPAIR.getValue());

            if (code != null && authCode.getCode().equalsIgnoreCase(code) && !StringUtils.isEmpty(newPassword)) {
                changeUserPassword(user.getId(), newPassword);
            } else {
                throw new IncorrectAuthCodeException();
            }
        } else {
            throw new ObjectNotFoundException();
        }
    }

    @Override
    public UserSetting getUserSetting(UUID userId, String key) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("key", key);

        List<UserSetting> userSettings = idObjectService.getList(UserSetting.class, null, "el.user.id=:userId and el.key=:key", params, null, null, null, 1);
        if (!userSettings.isEmpty()) {
            return userSettings.iterator().next();
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserSetting(UUID userId, String key, String value) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("key", key);

        UserSetting userSetting;
        List<UserSetting> userSettings = idObjectService.getList(UserSetting.class, null, "el.user.id=:userId and el.key=:key", params, null, null, null, 1);
        if (!userSettings.isEmpty()) {
            userSetting = userSettings.iterator().next();
        } else {
            userSetting = new UserSetting();
            userSetting.setKey(key);
            userSetting.setUser(idObjectService.getObjectById(User.class, userId));
        }

        userSetting.setValue(value);
        idObjectService.save(userSetting);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuthCode getActualCode(UUID userId, UUID codeTypeId, boolean invalidateImmediately) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("authCodeType", codeTypeId);
        params.put("authCodeState", DataConstants.AuthCodeStates.NEW.getValue());

        if (invalidateImmediately) {
            idObjectService.delete(AuthCode.class, "el.user.id=:userId and el.authCodeType.id=:authCodeType and el.authCodeState.id=:authCodeState", params);
        }

        AuthCode actualAuthCode;
        List<AuthCode> authCodes = getActualCodes(userId, codeTypeId);
        if (authCodes.isEmpty()) {
            actualAuthCode = createCode(userId, codeTypeId);
        } else {
            if (authCodes.size() == 1) {
                actualAuthCode = authCodes.iterator().next();
            } else {
                //Actual codes gt 1
                idObjectService.delete(AuthCode.class, "el.user.id=:userId and el.authCodeType.id=:authCodeType and el.authCodeState.id=:authCodeState", params);
                actualAuthCode = createCode(userId, codeTypeId);
            }
        }
        return actualAuthCode;
    }

    private static Integer generateCode() {
        Random random = new Random();
        int rage = 999999;
        return 100000 + random.nextInt(rage - 100000);
    }

    private List<AuthCode> getActualCodes(UUID userId, UUID codeTypeId) {
        return userDao.findAuthCodes(userId, Arrays.asList(codeTypeId), Arrays.asList(DataConstants.AuthCodeStates.NEW.getValue()));
    }

    private AuthCode createCode(UUID userId, UUID codeTypeId) {
        AuthCode authCode = null;
        User user = idObjectService.getObjectById(User.class, userId);
        if (user != null) {
            String code = null;
            for (int i = 0; i < 5; i++) {
                String tempCode = String.valueOf(generateCode());

                Map<String, Object> params = new HashMap<>();
                params.put("userId", userId);
                params.put("authCodeType", codeTypeId);
                params.put("code", tempCode);

                Integer count = idObjectService.checkExist(AuthCode.class, null, "el.user.id=:userId and el.authCodeType.id=:authCodeType and el.code=:code", params, 1);
                if (count == 0) {
                    code = tempCode;
                    break;
                }
            }

            if (code != null) {
                authCode = new AuthCode();
                authCode.setUser(user);
                authCode.setAuthCodeType(ds.get(AuthCodeType.class, codeTypeId));
                authCode.setAuthCodeState(ds.get(AuthCodeState.class, DataConstants.AuthCodeStates.NEW.getValue()));
                authCode.setCode(code);
                authCode = idObjectService.save(authCode);
            }

        }
        return authCode;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void invalidateCodes(UUID userId, UUID codeTypeId) {
        userDao.invalidateActualAuthCodes(userId, codeTypeId);

    }

    @Override
    public boolean isActualCodeAvailable(UUID userId, UUID codeTypeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("authCodeType", codeTypeId);
        params.put("authCodeState", DataConstants.AuthCodeStates.NEW.getValue());

        Integer count = idObjectService.checkExist(AuthCode.class, null, "el.user.id=:userId and el.authCodeType.id=:authCodeType and el.authCodeState.id=:authCodeState", params, 1);
        return count > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public User register(UserRegistrationDTO userRegistrationDTO, boolean trust) throws InvalidPasswordException, PhoneOrEmailIsNecessaryException, InvalidEmailException, InvalidPhoneException {
        String userApproveMethod = propertyService.getPropertyValue("user:approve_method");

        if (!trust) {
            if (!checkPassword(userRegistrationDTO.getPassword())) {
                throw new InvalidPasswordException();
            }

            if (StringUtils.isEmpty(userRegistrationDTO.getEmail()) && StringUtils.isEmpty(userRegistrationDTO.getPhone())) {
                throw new PhoneOrEmailIsNecessaryException();
            }
        }


        User user = new User();
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setApproved(false);
        user.setBlocked(false);

        if (!StringUtils.isEmpty(userRegistrationDTO.getPhone())) {
            if (!checkPhone(userRegistrationDTO.getPhone(), false)) {
                throw new InvalidPhoneException();
            }

            User anotherUser = userDao.getUserByField("phone", userRegistrationDTO.getPhone());
            if (anotherUser != null) {
                if (!anotherUser.getApproved()) {
                    lifecycleService.delete(anotherUser);
                } else if (!anotherUser.getPhoneVerified()) {
                    idObjectService.updateFieldValue(User.class, anotherUser.getId(), "phone", null);
                } else {
                    throw new InvalidPhoneException();
                }
            }

            user.setPhone(StringUtils.trim(userRegistrationDTO.getPhone()));
            if (trust || StringUtils.equalsIgnoreCase(userApproveMethod, DataConstants.UserApproveMethod.AUTO.getValue())) {
                user.setPhoneVerified(true);
            }
        }

        if (!StringUtils.isEmpty(userRegistrationDTO.getEmail())) {
            if (!checkEmail(userRegistrationDTO.getEmail(), false)) {
                throw new InvalidEmailException();
            }

            User anotherUser = userDao.getUserByField("email", userRegistrationDTO.getEmail());
            if (anotherUser != null) {
                if (!anotherUser.getApproved()) {
                    lifecycleService.delete(anotherUser);
                } else if (!anotherUser.getEmailVerified()) {
                    idObjectService.updateFieldValue(User.class, anotherUser.getId(), "email", null);
                } else {
                    throw new InvalidEmailException();
                }
            }

            user.setEmail(StringUtils.trim(StringUtils.lowerCase(userRegistrationDTO.getEmail())));
            if (trust || StringUtils.equalsIgnoreCase(userApproveMethod, DataConstants.UserApproveMethod.AUTO.getValue())) {
                user.setEmailVerified(true);
            }
        }

        user.setFields(JsonUtils.mapToJson(userRegistrationDTO.getFields()));

        if (!trust) {
            user.setSalt(UserServiceImpl.generatePasswordSalt());
            user.setPassword(DigestUtils.shaHex(userRegistrationDTO.getPassword().concat(user.getSalt())));
        }

        if (trust || StringUtils.equalsIgnoreCase(userApproveMethod, DataConstants.UserApproveMethod.AUTO.getValue())) {
            user.setApproved(true);
        }

        return idObjectService.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(User user) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", user.getId());

        idObjectService.delete(IncorrectLoginAttempt.class, "el.user.id=:userId", params);
        idObjectService.delete(AuthCode.class, "el.user.id=:userId", params);
        idObjectService.delete(UserSession.class, "el.user.id=:userId", params);
        idObjectService.delete(UserRole.class, "el.user.id=:userId", params);
        idObjectService.delete(UserSetting.class, "el.user.id=:userId", params);
        idObjectService.delete(User.class, user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendVerificationCode(User user, String loginType, Map<String, String> templateParams) throws SendingException {
        if (templateParams == null) {
            templateParams = new HashMap<>();
        }
        templateParams.put("userId", user.getId().toString());
        templateParams.put("loginType", loginType);
        templateParams.put("baseUrl", propertyService.getPropertyValue("web:base_url"));
        Map<String, String> fields = JsonUtils.jsonToMap(user.getFields());
        for (String key : fields.keySet()) {
            templateParams.put(key, fields.get(key));
        }

        if (StringUtils.equalsIgnoreCase(loginType, "phone") && !StringUtils.isEmpty(user.getPhone()) && !user.getPhoneVerified()) {
            AuthCode code = getActualCode(user.getId(), DataConstants.AuthCodeTypes.PHONE_VERIFY.getValue(), false);
            if (code != null) {
                try {
                    LoadedTemplate template = templateService.load("sms_validation_code");
                    templateParams.put("code", code.getCode());

                    messageSenderService.sendMessage(new Message(propertyService.getPropertyValue("notification:sms_from"), user.getPhone(), template.getSubject(), templateService.apply(template, templateParams)), SendingType.SMS);
                } catch (IOException e) {
                    logger.error(e);
                    throw new SendingException(e.getMessage());
                }
            }
        } else if (StringUtils.equalsIgnoreCase(loginType, "email") && !StringUtils.isEmpty(user.getEmail()) && !user.getEmailVerified()) {
            AuthCode code = getActualCode(user.getId(), DataConstants.AuthCodeTypes.EMAIL_VERIFY.getValue(), false);
            if (code != null) {
                try {
                    LoadedTemplate template = templateService.load("email_validation_code");
                    templateParams.put("code", code.getCode());

                    messageSenderService.sendMessage(new Message(propertyService.getPropertyValue("notification:smtp_from"), user.getEmail(), template.getSubject(), templateService.apply(template, templateParams)), SendingType.EMAIL);
                } catch (IOException e) {
                    logger.error(e);
                    throw new SendingException(e.getMessage());
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addRoleToUser(User user, Collection<UUID> roleIds) {
        for (UUID roleId : roleIds) {
            Role role = idObjectService.getObjectById(Role.class, roleId);

            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);

            idObjectService.save(userRole);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public User saveUser(UserDTO user, boolean mergeRoles, AuthorizedUser executor) throws ObjectNotFoundException {
        if (user.getId() == null) {
            throw new ObjectNotFoundException();
        }
        User u = idObjectService.getObjectById(User.class, user.getId());
        if (u == null) {
            throw new ObjectNotFoundException();
        }

        u.setFields(JsonUtils.mapToJson(user.getFields()));
        u.setApproved(user.getApproved());


        if (!u.getBlocked() && user.getBlocked()) {
            u.setBlockedByUser(idObjectService.getObjectById(User.class, executor.getId()));
            u.setBlockedDt(new Date());
        }

        u.setBlocked(user.getBlocked());
        if (!u.getBlocked()) {
            u.setBlockedDt(null);
            u.setBlockedByUser(null);
        }

        u = idObjectService.save(u);

        if (mergeRoles) {
            mergeUserRoles(u.getId(), user.getRoles());
        }

        return u;
    }

    @Override
    public List<UserRole> getUserRoles(UUID userId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        return idObjectService.getList(UserRole.class, null, "el.user.id=:userId", params, null, null, null, null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void mergeUserRoles(UUID userId, Collection<UUID> activeRoles) {
        User user = idObjectService.getObjectById(User.class, userId);
        Set<UUID> currentRoles = new HashSet<>();
        for (UserRole userRole : getUserRoles(userId)) {
            currentRoles.add(userRole.getRole().getId());
        }

        //Delete non-active roles
        String query = "el.user.id=:userId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        if (!activeRoles.isEmpty()) {
            query += " and el.role.id not in (:roles)";
            params.put("roles", activeRoles);
        }
        idObjectService.delete(UserRole.class, query, params);

        //Add active roles
        for (UUID roleId : activeRoles) {
            if (!currentRoles.contains(roleId)) {
                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setRole(ds.get(Role.class, roleId));
                idObjectService.save(userRole);
            }
        }
    }

    @Override
    public EntityListResponse<UserDTO> getUsersPaged(String phone, String email, Boolean approved, Boolean blocked, Map<String, String> fields, boolean fetchRoles, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        if (!StringUtils.isEmpty(sortField)) {
            //Т.к. в данном методе запрос используется нативный и требуется сохранить единообразие - транслируем название jpa полей в нативные sql
            if (StringUtils.equalsIgnoreCase(sortField, "el.id")) {
                sortField = "id";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.created")) {
                sortField = "created_dt";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.changed")) {
                sortField = "changed_dt";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.phone")) {
                sortField = "phone";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.phoneVerified")) {
                sortField = "is_phone_verified";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.email")) {
                sortField = "email";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.emailVerified")) {
                sortField = "is_email_verified";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.approved")) {
                sortField = "is_approved";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.blocked")) {
                sortField = "is_blocked";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.blockedDt")) {
                sortField = "blocked_dt";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.blockedByUser")) {
                sortField = "blocked_by_user_id";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.lastVisitDt")) {
                sortField = "last_visit_dt";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.lastVisitIP")) {
                sortField = "last_visit_ip";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.allowedAddresses")) {
                sortField = "allowed_addresses";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.password")) {
                sortField = "password";
            } else if (StringUtils.equalsIgnoreCase(sortField, "el.salt")) {
                sortField = "salt";
            } else if (StringUtils.startsWithIgnoreCase(sortField, "el.fields")) {
                sortField = sortField.substring("el.".length());
            }
        }

        int totalCount = userDao.getUsersCount(phone, email, approved, blocked, fields);
        int totalPages = ((totalCount / count)) + 1;
        int startRecord = page != null ? (page * count) - count : start;

        EntityListResponse<UserDTO> entityListResponse = new EntityListResponse<UserDTO>();
        entityListResponse.setEntity("user");
        entityListResponse.setPage(page);
        entityListResponse.setPages(totalPages);
        entityListResponse.setTotalCount(totalCount);


        List<User> items = userDao.getUsers(phone, email, approved, blocked, fields, sortField, sortDir, startRecord, count);
        List<UserRole> userRoles = Collections.emptyList();
        if (fetchRoles) {
            Set<UUID> userIds = new HashSet<>();
            for (User u : items) {
                userIds.add(u.getId());
            }
            Map<String, Object> params = new HashMap<>();
            params.put("userIds", userIds);
            userRoles = idObjectService.getList(UserRole.class, null, "el.user.id in (:userIds)", params, null, null, null, null);
        }

        entityListResponse.setPartCount(items.size());
        for (User e : items) {
            UserDTO el = UserDTO.prepare(e);
            for (UserRole ur : userRoles) {
                if (ur.getUser().getId().equals(e.getId())) {
                    el.getRoles().add(ur.getRole().getId());
                }
            }
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Override
    public UserDTO getUser(UUID userId, boolean fetchRoles) throws ObjectNotFoundException {
        User user = idObjectService.getObjectById(User.class, userId);
        if (user == null) {
            throw new ObjectNotFoundException();
        }

        UserDTO el = UserDTO.prepare(user);
        if (fetchRoles) {
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            List<UserRole> userRoles = idObjectService.getList(UserRole.class, null, "el.user.id=:userId", params, null, null, null, null);
            for (UserRole ur : userRoles) {
                if (ur.getUser().getId().equals(userId)) {
                    el.getRoles().add(ur.getRole().getId());
                }
            }
        }
        return el;
    }
}
