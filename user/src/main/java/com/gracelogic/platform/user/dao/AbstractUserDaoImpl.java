package com.gracelogic.platform.user.dao;

import com.gracelogic.platform.dao.dao.BaseDao;
import com.gracelogic.platform.user.model.AuthCode;
import com.gracelogic.platform.user.model.IncorrectLoginAttempt;
import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.user.service.DataConstants;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.*;

/**
 * Author: Igor Parkhomenko
 * Date: 12.07.12
 * Time: 21:44
 */
public abstract class AbstractUserDaoImpl extends BaseDao implements UserDao {
    private static Logger logger = Logger.getLogger(AbstractUserDaoImpl.class);


    @Override
    public User getUserByField(String fieldName, String fieldValue) {
        User user = null;
        String query = String.format("select user from User user left join fetch user.userRoles where user.%s = :fieldValue", fieldName);
        try {
            user = getEntityManager().createQuery(query, User.class)
                    .setParameter("fieldValue", fieldValue)
                    .getSingleResult();
        } catch (Exception e) {
            logger.debug(String.format("Failed to get user by field: %s", fieldName));
        }
        return user;
    }

    @Override
    public Long getIncorrectLoginAttemptCount(UUID userId, Date startDate, Date endDate) {
        Long result = null;
        String query = "select count(incorrectLoginAttempt) from IncorrectLoginAttempt incorrectLoginAttempt where incorrectLoginAttempt.user.id = :userId " +
                "and incorrectLoginAttempt.created >= :startDate and incorrectLoginAttempt.created <= :endDate";

        try {
            result = (Long) getEntityManager().createQuery(query)
                    .setParameter("userId", userId)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("Failed to get incorrect login attempt count", e);
        }
        return result != null ? result : 0L;
    }

    @Override
    public IncorrectLoginAttempt saveIncorrectLoginAttempt(IncorrectLoginAttempt incorrectLoginAttempt) {
        if (incorrectLoginAttempt.getId() == null) {
            persistEntity(incorrectLoginAttempt);
            return incorrectLoginAttempt;
        } else {
            return mergeEntity(incorrectLoginAttempt);
        }
    }

    @Override
    public void invalidateActualAuthCodes(UUID userId, UUID codeTypeId) {
        String sql = "update AuthCode set authCodeState.id = :deletedLiveStateId where user.id = :userId and authCodeType.id = :codeTypeId and authCodeState.id = :activeStateId";
        try {
            getEntityManager().createQuery(sql)
                    .setParameter("deletedLiveStateId", DataConstants.AuthCodeStates.DELETED.getValue())
                    .setParameter("userId", userId)
                    .setParameter("codeTypeId", codeTypeId)
                    .setParameter("activeStateId", DataConstants.AuthCodeStates.NEW.getValue())
                    .executeUpdate();
        }
        catch (Exception e) {
            logger.error("Failed to invalidate actual auth codes", e);
        }
    }

    @Override
    public List<AuthCode> findAuthCodes(UUID userId, Collection<UUID> codeTypeIds, Collection<UUID> codeStateIds) {
        List<AuthCode> result = Collections.emptyList();
        String query = "select code from AuthCode code where code.user.id = :userId";
        if (!CollectionUtils.isEmpty(codeTypeIds)) {
            query += " and code.authCodeType.id in (:codeTypeIds)";
        }
        if (!CollectionUtils.isEmpty(codeStateIds)) {
            query += " and code.authCodeState.id in (:codeStateIds)";
        }
        try {
            TypedQuery<AuthCode> q = getEntityManager().createQuery(query, AuthCode.class);
            q.setParameter("userId", userId);
            if (!CollectionUtils.isEmpty(codeTypeIds)) {
                q.setParameter("codeTypeIds", codeTypeIds);
            }
            if (!CollectionUtils.isEmpty(codeStateIds)) {
                q.setParameter("codeStateIds", codeStateIds);
            }
            result = q.getResultList();
        } catch (Exception e) {
            logger.error("Failed to find auth codes", e);
        }
        return result;
    }

}
