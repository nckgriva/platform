package com.gracelogic.platform.user.service;

import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.notification.dto.Message;
import com.gracelogic.platform.notification.service.MessageSenderService;
import com.gracelogic.platform.notification.exception.SendingException;
import com.gracelogic.platform.notification.dto.SendingType;
import com.gracelogic.platform.property.service.PropertyService;
import com.gracelogic.platform.user.dao.UserDao;
import com.gracelogic.platform.user.dto.AuthorizedUser;
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
            }
            else if (loginField.equalsIgnoreCase("phone")) {
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
                Date startDate = new Date(currentTimeMillis - Constants.INVALID_LOGIN_ATTEMPT_PERIOD);

                Long incorrectLoginAttemptCount = userDao.getIncorrectLoginAttemptCount(user.getId(), startDate, endDate);
                if (incorrectLoginAttemptCount < Constants.INVALID_LOGIN_ATTEMPT_TO_BLOCK) {
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
                }
                else {
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
        User user = idObjectService.getObjectById(User.class, userId);
        if (user != null) {
            if (loginType.equalsIgnoreCase("phone") && !user.getPhoneVerified()) {
                AuthCode phoneCode = getActualCode(userId, DataConstants.AuthCodeTypes.ACTIVATION.getValue(), false);
                if (phoneCode.getCode().equalsIgnoreCase(code)) {
                    user.setPhoneVerified(true);
                    user.setApproved(true);
                    idObjectService.save(user);

                    invalidateCodes(userId, DataConstants.AuthCodeTypes.ACTIVATION.getValue());
                    return true;
                }
            }
            else if (loginType.equalsIgnoreCase("email") && !user.getEmailVerified()) {
                AuthCode emailCode = getActualCode(userId, DataConstants.AuthCodeTypes.EMAIL_VERIFY.getValue(), false);
                if (emailCode.getCode().equalsIgnoreCase(code)) {
                    user.setEmailVerified(true);
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
            }
            catch (Exception ignored) {}

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
            if ((System.currentTimeMillis() - authCode.getCreated().getTime() > Long.parseLong(propertyService.getPropertyValue("sms_delay"))) || !isActualCodeAvailable) {
                if (isActualCodeAvailable) {
                    authCode = getActualCode(user.getId(), DataConstants.AuthCodeTypes.PASSWORD_REPAIR.getValue(), true);
                }
                if (!StringUtils.isEmpty(user.getPhone()) && user.getPhoneVerified()) {
                    String smsTemplate = "Код восстановления пароля: %s";
                    messageSenderService.sendMessage(new Message(null, user.getPhone(), null, String.format(smsTemplate, authCode.getCode())), SendingType.SMS);
                }
                else if (StringUtils.isEmpty(user.getEmail()) && user.getEmailVerified()) {
                    messageSenderService.sendMessage(new Message(propertyService.getPropertyValue("smtp_from_address"), user.getEmail(), "Код восстановления пароля", authCode.getCode()), SendingType.EMAIL);
                }
            } else {
                throw new IllegalParameterException("TOO_FAST");
            }
        }
        else {
            throw new SendingException("Пользователь не найден");
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
            }
            else {
                throw new IllegalParameterException("Code is incorrect or new password is empty");
            }
        }
        else {
            throw new IllegalParameterException("User is null");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void sendEmailVerification(UUID userId) {
        User user = idObjectService.getObjectById(User.class, userId);
        if (!StringUtils.isEmpty(user.getEmail()) && !user.getEmailVerified()) {
            AuthCode emailCode = getActualCode(user.getId(), DataConstants.AuthCodeTypes.EMAIL_VERIFY.getValue(), false);
            if (emailCode != null) {
                String text = String.format("Для подтверждения Вашего e-mail перейдите по ссылке:\n" +
                        "%s/registration?action=verifyEmail&id=%s&code=%s", propertyService.getPropertyValue("base_url"), user.getId().toString(), emailCode.getCode());
                try {
                    messageSenderService.sendMessage(new Message("no-reply@leofinance.com", user.getEmail(), "Подтверждение e-mail", text), SendingType.EMAIL);
                } catch (SendingException ignored) {
                }
            }
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
        }
        else {
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

}
