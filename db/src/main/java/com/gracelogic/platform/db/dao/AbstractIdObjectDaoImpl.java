package com.gracelogic.platform.db.dao;

import com.gracelogic.platform.db.model.IdObject;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Igor Parkhomenko
 * Date: 12.07.12
 * Time: 21:44
 */
public abstract class AbstractIdObjectDaoImpl extends BaseDao implements IdObjectDao {
    private static Logger logger = Logger.getLogger(AbstractIdObjectDaoImpl.class);

    public <T extends IdObject> T getObjectById(Class<T> clazz, String fetches, Object id) {
        if (StringUtils.isEmpty(fetches)) {
            return getEntityManager().find(clazz, id);
        }
        else {
            String query = "select el from %s el %s where el.id = :id";
            query = String.format(query, clazz.getSimpleName(), fetches);

            try {
                return getEntityManager().createQuery(query, clazz).setParameter("id", id).getSingleResult();
            } catch (Exception e) {
                logger.warn("Failed to get IdObject with id:" + id.toString(), e);
                return null;
            }
        }
    }

    @Override
    public <T extends IdObject> T lockObject(Class<T> clazz, Object id) {
        return getEntityManager().find(clazz, id, LockModeType.PESSIMISTIC_WRITE);
    }

    public <T extends IdObject> T save(T entity) {
        if (entity.getId() == null) {
            persistEntity(entity);
            return entity;
        } else {
            return mergeEntity(entity);
        }
    }

    public Integer checkExist(Class clazz, String fetches, String cause, HashMap<String, Object> params, Integer maxCount) {
        if (fetches == null) {
            fetches = "";
        }
        if (maxCount == null) {
            maxCount = 1;
        }

        List result = Collections.emptyList();

        String query = "select el.id from %s el " + fetches + " ";
        query = String.format(query, clazz.getSimpleName());
        if (cause != null && !cause.isEmpty()) {
            query += "where " + cause;
        }

        try {
            Query query1 = getEntityManager().createQuery(query);
            query1.setMaxResults(maxCount);

            if (params != null) {
                for (String paramName : params.keySet()) {
                    Object paramValue = params.get(paramName);
                    query1.setParameter(paramName, paramValue);
                }
            }

            result = query1.getResultList();
        } catch (Exception e) {
            logger.error("Failed to check exist", e);
        }

        return result.size();
    }

    public void delete(Class clazz, Object id) {
        String query = "delete from %s where id = :id";
        query = String.format(query, clazz.getSimpleName());

        try{
            getEntityManager().createQuery(query).setParameter("id", id).executeUpdate();
        }
        catch (Exception e) {
            logger.error("Failed to delete IdObject with id:" + id.toString(), e);
        }
    }

    public void offsetFieldValue(Class clazz, Object id, String fieldName, Integer offsetValue) {
        String query = "update %s el set el.%s = el.%s + :offsetValue where el.id = :id";
        query = String.format(query, clazz.getSimpleName(), fieldName, fieldName);

        try {
            getEntityManager().createQuery(query).setParameter("offsetValue", offsetValue).setParameter("id", id).executeUpdate();
        }
        catch (Exception e) {
            logger.error("Failed to offset field value", e);
        }
    }

    public void updateFieldValue(Class clazz, Object id, String fieldName, Object val) {
        String query = "update %s el set el.%s = :val where el.id = :id";
        query = String.format(query, clazz.getSimpleName(), fieldName);

        try {
            getEntityManager().createQuery(query).setParameter("val", val).setParameter("id", id).executeUpdate();
        }
        catch (Exception e) {
            logger.error("Failed to update field value", e);
        }
    }

    public void updateTwoFieldValue(Class clazz, Object id, String field1Name, Object val1, String field2Name, Object val2) {
        String query = "update %s el set el.%s = :val1, el.%s = :val2 where el.id = :id";
        query = String.format(query, clazz.getSimpleName(), field1Name, field2Name);

        try {
            getEntityManager().createQuery(query).setParameter("val1", val1).setParameter("val2", val2).setParameter("id", id).executeUpdate();
        }
        catch (Exception e) {
            logger.error("Failed to update field value", e);
        }
    }

    public Long getSum(Class clazz, String fieldName, String fetches, String cause, HashMap<String, Object> params) {
        if (fetches == null) {
            fetches = "";
        }
        Long count = null;
        String query = "select sum(%s) from %s el " + fetches + " ";
        query = String.format(query, fieldName, clazz.getSimpleName());
        if (cause != null && !cause.isEmpty()) {
            query += "where " + cause;
        }

        try{

            Query query1 = getEntityManager().createQuery(query);
            if (params != null) {
                for (String paramName : params.keySet()) {
                    Object paramValue = params.get(paramName);
                    query1.setParameter(paramName, paramValue);
                }
            }
            count = (Long) query1.getSingleResult();
        }
        catch (Exception e) {
            logger.error("Failed to get count", e);
        }

        if (count != null) {
            return count;
        } else {
            return 0L;
        }
    }

    public Integer getCount(Class clazz, String column, String fetches, String cause, HashMap<String, Object> params) {
        if (fetches == null) {
            fetches = "";
        }
        Long count = null;
        String query = "select count(" + (column != null ? column : "*") + ") from %s el " + fetches + " ";
        query = String.format(query, clazz.getSimpleName());
        if (cause != null && !cause.isEmpty()) {
            query += "where " + cause;
        }


        try{
            Query query1 = getEntityManager().createQuery(query);
            if (params != null) {
                for (String paramName : params.keySet()) {
                    Object paramValue = params.get(paramName);
                    query1.setParameter(paramName, paramValue);
                }
            }
            count = (Long) query1.getSingleResult();
        }
        catch (Exception e) {
            logger.error("Failed to get count", e);
        }

        if (count != null) {
            return count.intValue();
        } else {
            return 0;
        }
    }

    @Override
    public Integer getMaxInteger(Class clazz, String fieldName, String cause) {
        Integer count = null;
        String query = "select max(%s) from %s ";
        query = String.format(query, fieldName, clazz.getSimpleName());
        if (cause != null && !cause.isEmpty()) {
            query += "where " + cause;
        }


        try{
            count = (Integer) getEntityManager().createQuery(query).getSingleResult();
        }
        catch (Exception e) {
            logger.error("Failed to get max int", e);
        }

        if (count != null) {
            return count;
        } else {
            return 0;
        }
    }

    @Override
    public Date getMaxDate(Class clazz, String fieldName, String cause) {
        Date date = null;
        String query = "select max(%s) from %s ";
        query = String.format(query, fieldName, clazz.getSimpleName());
        if (cause != null && !cause.isEmpty()) {
            query += "where " + cause;
        }


        try{
            date = (Date) getEntityManager().createQuery(query).getSingleResult();
        }
        catch (Exception e) {
            logger.error("Failed to get max date", e);
        }

        return date;
    }

    public void delete(Class clazz, String cause) {
        if (cause == null) {
            cause = "";
        }
        String query = "delete from %s el where %s ";
        query = String.format(query, clazz.getSimpleName(), cause);

        getEntityManager().createQuery(query).executeUpdate();
    }

}


