package com.gracelogic.platform.db.service;

import com.gracelogic.platform.db.dao.IdObjectDao;
import com.gracelogic.platform.db.model.IdObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Igor Parkhomenko
 * Date: 18.12.14
 * Time: 12:37
 */
@Service
public class IdObjectServiceImpl implements IdObjectService {

    @Autowired
    private IdObjectDao idObjectDao;

    @Override
    public <T extends IdObject> T lockObject(Class<T> clazz, Object id) {
        return idObjectDao.lockObject(clazz, id);
    }

    @Override
    public <T extends IdObject> T getObjectById(Class<T> clazz, Object id) {
        if (id == null) {
            return null;
        }

        return idObjectDao.getObjectById(clazz, null, id);
    }

    @Override
    public <T extends IdObject> T getObjectById(Class<T> clazz, String fetches, Object id) {
        if (id == null) {
            return null;
        }

        return idObjectDao.getObjectById(clazz, fetches, id);
    }

    @Override
    public Integer checkExist(Class clazz, String fetches, String cause, HashMap<String, Object> params, Integer maxCount) {
        return idObjectDao.checkExist(clazz, fetches, cause, params, maxCount);
    }

    @Override
    public <T extends IdObject> T save(T entity) {
        return idObjectDao.save(entity);
    }

    @Override
    public <T> T saveOrUpdate(T entity) {
        return idObjectDao.saveOrUpdate(entity);
    }

    @Override
    public <T> List<T> getList(Class<T> clazz) {
        return idObjectDao.getList(clazz);
    }

    @Override
    public void delete(Class clazz, Object id) {
        idObjectDao.delete(clazz, id);
    }

    @Override
    public void delete(Class clazz, String cause) {
        idObjectDao.delete(clazz, cause);
    }

    @Override
    public Integer getCount(Class clazz, String cause) {
        return idObjectDao.getCount(clazz, null, null, cause, null);
    }

    @Override
    public Long getSum(Class clazz, String fieldName, String cause) {
        return idObjectDao.getSum(clazz, fieldName, null, cause, null);
    }

    @Override
    public Long getSum(Class clazz, String fieldName, String cause, HashMap<String, Object> params) {
        return idObjectDao.getSum(clazz, fieldName, null, cause, params);
    }

    @Override
    public Long getSum(Class clazz, String fieldName, String fetches, String cause, HashMap<String, Object> params) {
        return idObjectDao.getSum(clazz, fieldName, fetches, cause, params);
    }

    @Override
    public Integer getCount(Class clazz, String fetches, String cause) {
        return idObjectDao.getCount(clazz, null, fetches, cause, null);
    }

    @Override
    public Integer getCount(Class clazz, String fetches, String cause, HashMap<String, Object> params) {
        return idObjectDao.getCount(clazz, null, fetches, cause, params);
    }

    @Override
    public Integer getCount(Class clazz, String column, String fetches, String cause, HashMap<String, Object> params) {
        return idObjectDao.getCount(clazz, column, fetches, cause, params);
    }

    @Override
    public <T extends IdObject> T setIfModified(Class<T> clazz, T oldObject, Object newId) {
        if (oldObject != null && isEquals(oldObject.getId(), newId)) {
            return oldObject;
        }
        else {
            if (newId != null) {
                return getObjectById(clazz, newId);
            }
            return null;
        }
    }

    @Override
    public <T> List<T> getList(Class<T> clazz, String fieldName, String search) {
        return idObjectDao.getList(clazz, fieldName, search);
    }

    @Override
    public <T> List<T> getListByFieldId(Class<T> clazz, String fieldName, Integer id) {
        return idObjectDao.getListByFieldId(clazz, fieldName, id);
    }

    @Override
    public <T> List<T> getListByFieldId(Class<T> clazz, String fieldName, Integer id, Integer pageSize) {
        return idObjectDao.getListByFieldId(clazz, fieldName, id, pageSize);
    }

    @Override
    public void offsetFieldValue(Class clazz, Object id, String fieldName, Integer offsetValue) {
        idObjectDao.offsetFieldValue(clazz, id, fieldName, offsetValue);
    }

    @Override
    public <T> List<T> getList(Class<T> clazz, String cause, String sortField, String sortDirection, Integer startRecord, Integer maxResult) {
        return idObjectDao.getList(clazz, null, cause, null, sortField, sortDirection, startRecord, maxResult);
    }

    @Override
    public <T> List<T> getList(Class<T> clazz, String fetches, String cause, String sortField, String sortDirection, Integer startRecord, Integer maxResult) {
        return idObjectDao.getList(clazz, fetches, cause, null, sortField, sortDirection, startRecord, maxResult);
    }

    @Override
    public <T> List<T> getList(Class<T> clazz, String fetches, String cause, HashMap<String, Object> params, String sortField, String sortDirection, Integer startRecord, Integer maxResult) {
        return idObjectDao.getList(clazz, fetches, cause, params, sortField, sortDirection, startRecord, maxResult);
    }

    @Override
    public Integer getMaxInteger(Class clazz, String fieldName, String cause) {
        return idObjectDao.getMaxInteger(clazz, fieldName, cause);
    }

    @Override
    public Date getMaxDate(Class clazz, String fieldName, String cause) {
        return idObjectDao.getMaxDate(clazz, fieldName, cause);
    }

    @Override
    public void updateFieldValue(Class clazz, Object id, String fieldName, Object val) {
        idObjectDao.updateFieldValue(clazz, id, fieldName, val);
    }

    @Override
    public void updateTwoFieldValue(Class clazz, Object id, String field1Name, Object val1, String field2Name, Object val2) {
        idObjectDao.updateTwoFieldValue(clazz, id, field1Name, val1, field2Name, val2);
    }

    public static boolean isEquals(Object first, Object second) {
        if (first == null) {
            return second == null;
        }
        return first.equals(second);
    }

    public static String valuesAsString(Collection values) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (Object val : values) {
            if (!first) {
                stringBuilder.append(",");
            }
            else {
                first = false;
            }
            stringBuilder.append("'");
            stringBuilder.append(val.toString());
            stringBuilder.append("'");
        }
        return stringBuilder.toString();
    }
}
