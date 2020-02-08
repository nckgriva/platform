package com.gracelogic.platform.user.dao;

import com.gracelogic.platform.db.dao.BaseDao;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.user.model.AuthCode;
import com.gracelogic.platform.user.model.Identifier;
import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.user.service.DataConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.*;

public abstract class AbstractUserDaoImpl extends BaseDao implements UserDao {
    private static Logger logger = Logger.getLogger(AbstractUserDaoImpl.class);


//    @Override
//    public User getUserByField(String fieldName, Object fieldValue) {
//        if (StringUtils.equalsIgnoreCase(fieldName, IdObject.ID) && fieldValue != null && fieldValue instanceof String) {
//            try {
//                fieldValue = UUID.fromString((String) fieldValue);
//            }
//            catch (Exception ignored) {}
//        }
//
//        User user = null;
//        String query = String.format("select user from User user " +
//                "left join fetch user.userRoles ur " +
//                "left join fetch ur.role rl " +
//                "left join fetch rl.roleGrants rg " +
//                "left join fetch rg.grant gr " +
//                "where user.%s = :fieldValue", fieldName);
//        try {
//            user = getEntityManager().createQuery(query, User.class)
//                    .setParameter("fieldValue", fieldValue)
//                    .getSingleResult();
//        } catch (Exception e) {
//            logger.debug(String.format("Failed to get user by field: %s", fieldName), e);
//        }
//        return user;
//    }

    @Override
    public Identifier findIdentifier(UUID identifierTypeId, String identifierValue, boolean enrich) {
        String query = "select identifier from Identifier user " +
                (enrich ?
                        ("left join fetch el.user user " +
                        "left join fetch user.userRoles ur " +
                        "left join fetch ur.role rl " +
                        "left join fetch rl.roleGrants rg " +
                        "left join fetch rg.grant gr") :

                        ("left join fetch el.user")
                ) + "where el.identifierTypeId=:identifierTypeId and el.value=:value";

        logger.info(query);

        try {
            return (Identifier) getEntityManager().createNativeQuery(query, Identifier.class)
                    .setParameter("identifierTypeId", identifierTypeId)
                    .setParameter("value", identifierValue).getSingleResult();
        } catch (Exception e) {
            logger.debug(String.format("Failed to get identifier by value: %s", identifierValue), e);
        }
        return null;
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
