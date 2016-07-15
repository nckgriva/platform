package com.gracelogic.platform.user.dao;


import com.gracelogic.platform.user.model.AuthCode;
import com.gracelogic.platform.user.model.IncorrectLoginAttempt;
import com.gracelogic.platform.user.model.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
}
