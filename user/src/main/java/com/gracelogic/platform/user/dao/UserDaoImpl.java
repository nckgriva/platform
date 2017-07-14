package com.gracelogic.platform.user.dao;

import com.gracelogic.platform.user.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImpl extends AbstractUserDaoImpl {
    private static Logger logger = Logger.getLogger(UserDaoImpl.class);

    @Override
    public Integer getUsersCount(String phone, String email, Boolean approved, Boolean blocked, Map<String, String> fields) {
        BigInteger count = null;
        StringBuilder queryStr = new StringBuilder("select count(ID) from {h-schema}cmn_user where 1=1 ");

        Map<String, Object> params = new HashMap<>();
        if (!StringUtils.isEmpty(phone)) {
            queryStr.append("and phone = :phone ");
            params.put("phone", phone);
        }
        if (!StringUtils.isEmpty(email)) {
            queryStr.append("and email = :email ");
            params.put("email", email);
        }
        if (approved != null) {
            queryStr.append("and is_approved = :approved ");
            params.put("approved", approved);
        }
        if (blocked != null) {
            queryStr.append("and is_blocked = :blocked ");
            params.put("blocked", blocked);
        }
        if (fields != null && !fields.isEmpty()) {
            for (String key : fields.keySet()) {
                queryStr.append(String.format("and fields ->> '%s' = '%s' ", key, fields.get(key)));
            }
        }

        try {
            Query query = getEntityManager().createNativeQuery(queryStr.toString());

            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }

            count = (BigInteger) query.getSingleResult();
        } catch (Exception e) {
            logger.error("Failed to get users count", e);
        }

        return count != null ? count.intValue() : null;
    }

    @Override
    public List<User> getUsers(String phone, String email, Boolean approved, Boolean blocked, Map<String, String> fields, String sortField, String sortDir, Integer startRecord, Integer recordsOnPage) {
        List<User> users = Collections.emptyList();
        StringBuilder queryStr = new StringBuilder("select * from {h-schema}cmn_user where 1=1 ");

        Map<String, Object> params = new HashMap<>();
        if (!StringUtils.isEmpty(phone)) {
            queryStr.append("and phone = :phone ");
            params.put("phone", phone);
        }
        if (!StringUtils.isEmpty(email)) {
            queryStr.append("and email = :email ");
            params.put("email", email);
        }
        if (approved != null) {
            queryStr.append("and is_approved = :approved ");
            params.put("approved", approved);
        }
        if (blocked != null) {
            queryStr.append("and is_blocked = :blocked ");
            params.put("blocked", blocked);
        }
        if (fields != null && !fields.isEmpty()) {
            for (String key : fields.keySet()) {
                queryStr.append(String.format("and fields ->> '%s' = '%s' ", key, fields.get(key)));
            }
        }

        if (!StringUtils.isEmpty(sortField)) {
            queryStr.append(String.format("order by %s ", sortField));
            if (!StringUtils.isEmpty(sortDir)) {
                queryStr.append(String.format("%s ", sortDir));
            }
        }

        if (recordsOnPage != null) {
            queryStr.append("limit :recordsOnPage ");
            params.put("recordsOnPage", recordsOnPage);
            if (startRecord != null) {
                queryStr.append("offset :startRecord ");
                params.put("startRecord", startRecord);
            }
        }

        try {
            Query query = getEntityManager().createNativeQuery(queryStr.toString(), User.class);

            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }

            users = query.getResultList();

        } catch (Exception e) {
            logger.error("Failed to get users", e);
        }

        return users;
    }
}
