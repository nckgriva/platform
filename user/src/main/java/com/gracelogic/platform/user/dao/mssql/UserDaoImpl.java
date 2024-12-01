package com.gracelogic.platform.user.dao.mssql;

import com.gracelogic.platform.db.condition.OnMSSQLServerConditional;
import com.gracelogic.platform.user.dao.AbstractUserDaoImpl;
import com.gracelogic.platform.user.model.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Query;
import java.util.*;

@Repository
@Conditional(OnMSSQLServerConditional.class)
public class UserDaoImpl extends AbstractUserDaoImpl {
    private static Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public Integer getUsersCount(String identifierValue, Boolean approved, Boolean blocked, Map<String, String> fields) {
        Integer count = null;
        StringBuilder queryStr = new StringBuilder("select count(ID) from {h-schema}cmn_user el " +
                "inner join {h-schema}cmn_identifier iden on el.id = iden.user_id where 1=1 ");

        Map<String, Object> params = new HashMap<>();
        if (!StringUtils.isEmpty(identifierValue)) {
            queryStr.append("and iden.value = :identifierValue ");
            params.put("identifierValue", identifierValue);
        }
        if (approved != null) {
            queryStr.append("and el.is_approved = :approved ");
            params.put("approved", approved);
        }
        if (blocked != null) {
            queryStr.append("and el.is_blocked = :blocked ");
            params.put("blocked", blocked);
        }
        if (fields != null && !fields.isEmpty()) {
            int i = 0;
            for (String key : fields.keySet()) {
                i++;
                queryStr.append(String.format("and JSON_VALUE(el.fields, :key_%d) like val_%d ", i, i));
//                queryStr.append(String.format("and JSON_VALUE(el.fields,'$.%s') like '%s' ", key, fields.get(key)));

                params.put("key_" + i, "$." + key);
                params.put("val_" + i, fields.get(key));
            }
        }
        try {
            Query query = getEntityManager().createNativeQuery(queryStr.toString());

            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }

            count = (Integer) query.getSingleResult();
        } catch (Exception e) {
            logger.error("Failed to get users count", e);
        }

        return count != null ? count.intValue() : null;
    }

    @Override
    //public List<User> getUsers(String phone, String email, Boolean approved, Boolean blocked, Map<String, String> fields, String sortField, String sortDir, Integer startRecord, Integer recordsOnPage) {
    public List<User> getUsers(String identifierValue, Boolean approved, Boolean blocked, Map<String, String> fields, String sortField, String sortDir, Integer startRecord, Integer recordsOnPage) {
        List<String> availableSortingFields = Arrays.asList("el.id", "el.created_dt", "el.changed_dt", "el.is_active", "el.is_blocked", "el.is_approved", "el.blocked_dt", "el.blocked_by_user_id", "el.last_visit_dt", "el.last_visit_ip");
        if (!availableSortingFields.contains(StringUtils.lowerCase(sortField))) {
            throw new RuntimeException("Required sort field is unavailable");
        }

        List<User> users = Collections.emptyList();
        StringBuilder queryStr = new StringBuilder("select * from {h-schema}cmn_user el " +
                "inner join {h-schema}cmn_identifier iden on el.id = iden.user_id where 1=1 ");

        Map<String, Object> params = new HashMap<>();
        if (!StringUtils.isEmpty(identifierValue)) {
            queryStr.append("and iden.value = :identifierValue ");
            params.put("identifierValue", identifierValue);
        }
        if (approved != null) {
            queryStr.append("and el.is_approved = :approved ");
            params.put("approved", approved);
        }
        if (blocked != null) {
            queryStr.append("and el.is_blocked = :blocked ");
            params.put("blocked", blocked);
        }
        if (fields != null && !fields.isEmpty()) {
            int i = 0;
            for (String key : fields.keySet()) {
                i++;
                queryStr.append(String.format("and JSON_VALUE(el.fields, :key_%d) like val_%d ", i, i));
//                queryStr.append(String.format("and JSON_VALUE(el.fields,'$.%s') like '%s' ", key, fields.get(key)));

                params.put("key_" + i, "$." + key);
                params.put("val_" + i, fields.get(key));
            }
        }

        appendSortClause(queryStr, sortField, sortDir);

        try {
            Query query = getEntityManager().createNativeQuery(queryStr.toString(), User.class);

            appendPaginationClause(query, params, recordsOnPage, startRecord);

            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }

            users = query.getResultList();

        } catch (Exception e) {
            logger.error("Failed to get users", e);
        }

        return users;
    }

    @Override
    public List<Object[]> getLastActiveUsersSessions() {
        List<Object[]> result = Collections.emptyList();

        String queryStr =
                "SELECT " +
                "  CAST(x.user_id AS text) as user_id, x.session_id " +
                "FROM ( " +
                "  SELECT " +
                "    ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY created_dt desc) AS r, " +
                "    t.* " +
                "  FROM " +
                "    {h-schema}cmn_user_session t where t.is_valid = 1) x " +
                "WHERE " +
                "  x.r <= 1";

        try {
            Query query = getEntityManager().createNativeQuery(queryStr);
            result = query.getResultList();

        } catch (Exception e) {
            logger.error("Failed to get last active users sessions", e);
        }

        return result;
    }
}
