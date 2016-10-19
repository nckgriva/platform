package com.gracelogic.platform.user.dao;


import com.gracelogic.platform.user.model.AuthCode;
import com.gracelogic.platform.user.model.IncorrectLoginAttempt;
import com.gracelogic.platform.user.model.User;

import java.util.*;

/**
 * Author: Igor Parkhomenko
 * Date: 20.07.13
 * Time: 17:10
 */
public interface UserDao {
    User getUserByField(String fieldName, String fieldValue);

    Long getIncorrectLoginAttemptCount(UUID userId, Date startDate, Date endDate);

    IncorrectLoginAttempt saveIncorrectLoginAttempt(IncorrectLoginAttempt incorrectLoginAttempt);

    void invalidateActualAuthCodes(UUID userId, UUID codeTypeId);

    List<AuthCode> findAuthCodes(UUID userId, Collection<UUID> codeTypeIds, Collection<UUID> codeStateIds);

    Integer getUsersCount(String phone, String email, Boolean approved, Boolean blocked, Map<String, String> fields);

    List<User> getUsers(String phone, String email, Boolean approved, Boolean blocked, Map<String, String> fields, String sortField, String sortDir, Integer startRecord, Integer recordsOnPage);
}
