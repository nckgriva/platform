package com.gracelogic.platform.user.service;

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
import com.gracelogic.platform.user.dto.GrantDTO;
import com.gracelogic.platform.user.dto.RoleDTO;
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
    public User getUserByField(String fieldName, String fieldValue) {
        return userDao.getUserByField(fieldName, fieldValue);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public User login(String login, String loginField, String password, String remoteAddress, boolean trust) throws UserBlockedException, TooManyAttemptsException, NotAllowedIPException, UserNotActivatedException {
        User user = userDao.getUserByField(loginField, login.toLowerCase().trim());

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
            if (loginField.equalsIgnoreCase("email")) {
                loginTypeVerified = user.getEmailVerified();
            } else if (loginField.equalsIgnoreCase("phone")) {
                loginTypeVerified = user.getPhoneVerified();
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
                    String passwordAndSalt = password.concat(user.getSalt());
                    if (user.getPassword().equals(DigestUtils.shaHex(passwordAndSalt))) {
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

        idObjectService.delete(IncorrectLoginAttempt.class, String.format("el.user.id='%s'", userId.toString()));
    }

    public static String generatePasswordSalt() {
        Random random = new Random(System.currentTimeMillis());
        return DigestUtils.md5Hex(String.valueOf(random.nextLong()));
    }

    @Override
    public boolean checkPhone(String phone, boolean fullCheck) {
        if (!StringUtils.isEmpty(phone) && phone.length() > 5) {
            Pattern p = Pattern.compile("^7\\d{10}$");
            Matcher m = p.matcher(phone.trim());
            boolean result = m.find();
            if (fullCheck) {
                return result && idObjectService.checkExist(User.class, null, String.format("el.phone='%s' and el.phoneVerified=true", phone.trim()), null, 1) == 0;
            }
            return result;
        }
        return false;
    }

    @Override
    public boolean checkEmail(String email, boolean fullCheck) {
        if (!StringUtils.isEmpty(email) && email.length() > 5 && email.length() < 128) {
            Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher m = p.matcher(email.trim());
            boolean result = m.find();
            if (fullCheck) {
                return result && idObjectService.checkExist(User.class, null, String.format("el.email='%s' and el.emailVerified=true", email.trim()), null, 1) == 0;
            }
            return result;
        }
        return false;
    }

    @Override
    public boolean checkPassword(String password) {
        return !StringUtils.isEmpty(password) && password.length() > 5;
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

                List<UserSession> userSessions = idObjectService.getList(UserSession.class, String.format("el.sessionId='%s'", session.getId()), null, null, null, null);
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
    public void sendRepairCode(String login, String loginType) throws IllegalParameterException, SendingException {
        User user = getUserByField(loginType, login);
        if (user != null && user.getApproved()) {
            boolean isActualCodeAvailable = isActualCodeAvailable(user.getId(), DataConstants.AuthCodeTypes.PASSWORD_REPAIR.getValue());
            AuthCode authCode = getActualCode(user.getId(), DataConstants.AuthCodeTypes.PASSWORD_REPAIR.getValue(), false);
            if ((System.currentTimeMillis() - authCode.getCreated().getTime() > Long.parseLong(propertyService.getPropertyValue("user:action_delay"))) || !isActualCodeAvailable) {
                if (isActualCodeAvailable) {
                    authCode = getActualCode(user.getId(), DataConstants.AuthCodeTypes.PASSWORD_REPAIR.getValue(), true);
                }
                if (!StringUtils.isEmpty(user.getPhone()) && user.getPhoneVerified()) {
                    try {
                        LoadedTemplate template = templateService.load("sms_repair_code");
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("code", authCode.getCode());

                        messageSenderService.sendMessage(new Message(propertyService.getPropertyValue("notification:sms_from"), user.getPhone(), template.getSubject(), templateService.apply(template, params)), SendingType.SMS);
                    } catch (IOException e) {
                        logger.error(e);
                        throw new SendingException(e.getMessage());
                    }
                } else if (StringUtils.isEmpty(user.getEmail()) && user.getEmailVerified()) {
                    try {
                        LoadedTemplate template = templateService.load("email_repair_code");
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("code", authCode.getCode());

                        messageSenderService.sendMessage(new Message(propertyService.getPropertyValue("notification:smtp_from"), user.getEmail(), template.getSubject(), templateService.apply(template, params)), SendingType.EMAIL);
                    } catch (IOException e) {
                        logger.error(e);
                        throw new SendingException(e.getMessage());
                    }
                }
            } else {
                throw new IllegalParameterException("common.TOO_FAST_OPERATION");
            }
        } else {
            throw new IllegalParameterException("common.USER_NOT_FOUND");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changePassword(String login, String loginType, String code, String newPassword) throws IllegalParameterException {
        User user = getUserByField(loginType, login);

        if (user != null && isActualCodeAvailable(user.getId(), DataConstants.AuthCodeTypes.PASSWORD_REPAIR.getValue())) {
            AuthCode authCode = getActualCode(user.getId(), DataConstants.AuthCodeTypes.PASSWORD_REPAIR.getValue(), false);
            invalidateCodes(user.getId(), DataConstants.AuthCodeTypes.PASSWORD_REPAIR.getValue());

            if (code != null && authCode.getCode().equalsIgnoreCase(code) && !StringUtils.isEmpty(newPassword)) {
                changeUserPassword(user.getId(), newPassword);
            } else {
                throw new IllegalParameterException("common.AUTH_CODE_IS_INCORRECT");
            }
        } else {
            throw new IllegalParameterException("common.USER_NOT_FOUND");
        }
    }

    @Override
    public String getUserSetting(UUID userId, String key) {
        List<UserSetting> userSettings = idObjectService.getList(UserSetting.class, String.format("el.user.id='%s' and el.key='%s'", userId.toString(), key), null, null, null, 1);
        if (!userSettings.isEmpty()) {
            return userSettings.iterator().next().getValue();
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserSetting(UUID userId, String key, String value) {
        UserSetting userSetting;
        List<UserSetting> userSettings = idObjectService.getList(UserSetting.class, String.format("el.user.id='%s' and el.key='%s'", userId.toString(), key), null, null, null, 1);
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
        if (invalidateImmediately) {
            idObjectService.delete(AuthCode.class, String.format("el.user.id='%s' and el.authCodeType.id='%s' and el.authCodeState.id='%s'", userId.toString(), codeTypeId.toString(), DataConstants.AuthCodeStates.NEW.getValue()));
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
                idObjectService.delete(AuthCode.class, String.format("el.user.id='%s' and el.authCodeType.id='%s' and el.authCodeState.id='%s'", userId.toString(), codeTypeId.toString(), DataConstants.AuthCodeStates.NEW.getValue()));
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
                Integer count = idObjectService.checkExist(AuthCode.class, null, String.format("el.user.id='%s' and el.authCodeType.id='%s' and el.code='%s'", userId.toString(), codeTypeId.toString(), tempCode), null, 1);
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
        Integer count = idObjectService.checkExist(AuthCode.class, null, String.format("el.user.id='%s' and el.authCodeType.id='%s' and el.authCodeState.id='%s'", userId, codeTypeId, DataConstants.AuthCodeStates.NEW.getValue()), null, 1);
        return count > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public User register(AuthorizedUser userModel, boolean trust) throws IllegalParameterException {
        String userApproveMethod = propertyService.getPropertyValue("user:approve_method");
        boolean existingUser = false;

        if (!trust && !checkPassword(userModel.getPassword())) {
            throw new IllegalParameterException("register.INVALID_PASSWORD");
        }

        if (!trust) {
            if (StringUtils.isEmpty(userModel.getEmail()) && StringUtils.isEmpty(userModel.getPhone())) {
                throw new IllegalParameterException("register.PHONE_OR_EMAIL_IS_NECESSARY");
            }
        }

        if (!StringUtils.isEmpty(userModel.getPhone()) && !checkPhone(userModel.getPhone(), true)) {
            throw new IllegalParameterException("register.INVALID_PHONE");
        }

        User user = null;
        if (!StringUtils.isEmpty(userModel.getPhone())) {
            user = getUserByField("phone", userModel.getPhone());
        }

        if (user != null) {
            if (user.getApproved()) {
                throw new IllegalParameterException("register.INVALID_PHONE");
            }
            existingUser = true;
        } else {
            user = new User();
        }

        if (!StringUtils.isEmpty(userModel.getEmail()) && !checkEmail(userModel.getEmail(), true)) {
            throw new IllegalParameterException("register.INVALID_EMAIL");
        }

        //CHECK EMAIL ON ANOTHER ACCOUNT
        if (!StringUtils.isEmpty(userModel.getEmail())) {
            User anotherUser = getUserByField("email", userModel.getEmail());
            if (anotherUser != null) {
                if (existingUser && !user.getId().equals(anotherUser.getId()) || !existingUser) {
                    if (!anotherUser.getApproved()) {
                        lifecycleService.delete(anotherUser);
                    } else if (!anotherUser.getEmailVerified()) {
                        idObjectService.updateFieldValue(User.class, anotherUser.getId(), "email", null);
                    }
                }
            }
        }

        //CHECK PHONE ON ANOTHER ACCOUNT
        if (!StringUtils.isEmpty(userModel.getPhone())) {
            User anotherUser = getUserByField("phone", userModel.getPhone());
            if (anotherUser != null) {
                if (existingUser && !user.getId().equals(anotherUser.getId()) || !existingUser) {
                    if (!anotherUser.getApproved()) {
                        lifecycleService.delete(anotherUser);
                    } else if (!anotherUser.getPhoneVerified()) {
                        idObjectService.updateFieldValue(User.class, anotherUser.getId(), "phone", null);
                    }
                }
            }
        }

        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setApproved(false);
        user.setBlocked(false);

        if (trust) {
            user.setApproved(true);
        } else if (StringUtils.equalsIgnoreCase(userApproveMethod, DataConstants.UserApproveMethod.AUTO.getValue())) {
            user.setApproved(true);
        }

        user.setSurname(userModel.getSurname());
        user.setName(userModel.getName());
        user.setPatronymic(userModel.getPatronymic());

        if (!StringUtils.isEmpty(userModel.getEmail())) {
            user.setEmail(userModel.getEmail().toLowerCase().trim());
            if (trust) {
                user.setEmailVerified(true);
            }
        }
        if (!StringUtils.isEmpty(userModel.getPhone())) {
            user.setPhone(userModel.getPhone());
            if (trust) {
                user.setPhoneVerified(true);
            }
        }

        user.setSalt(UserServiceImpl.generatePasswordSalt());
        if (!trust) {
            user.setPassword(DigestUtils.shaHex(userModel.getPassword().concat(user.getSalt())));
        }

        user = idObjectService.save(user);

        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(User user) {
        idObjectService.delete(IncorrectLoginAttempt.class, String.format("el.user.id='%s'", user.getId()));
        idObjectService.delete(AuthCode.class, String.format("el.user.id='%s'", user.getId()));
        idObjectService.delete(UserSession.class, String.format("el.user.id='%s'", user.getId()));
        idObjectService.delete(UserRole.class, String.format("el.user.id='%s'", user.getId()));
        idObjectService.delete(User.class, String.format("el.id='%s'", user.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendVerificationCode(User user, String loginType, Map<String, String> templateParams) throws IllegalParameterException, SendingException {
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
        }
        else if (StringUtils.equalsIgnoreCase(loginType, "email") && !StringUtils.isEmpty(user.getEmail()) && !user.getEmailVerified()) {
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
}
