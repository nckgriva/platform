package com.gracelogic.platform.db.dao;


import com.gracelogic.platform.db.model.IdObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Igor Parkhomenko
 * Date: 09.11.14
 * Time: 15:21
 */
public interface IdObjectDao {
    <T extends IdObject> T getObjectById(Class<T> clazz, String fetches, Object id);

    <T extends IdObject> T save(T entity);

    <T extends IdObject> T lockObject(Class<T> clazz, Object id);

    Integer checkExist(Class clazz, String fetches, String cause, HashMap<String, Object> params, Integer maxCount);

    <T> T saveOrUpdate(T entity);

    <T> List<T> getList(Class<T> clazz);

    <T> List<T> getList(Class<T> clazz, String fetches, String cause, HashMap<String, Object> params, String sortField, String sortDirection, Integer startRecord, Integer maxResult);

    <T> List<T> getList(Class<T> clazz, String fieldName, String search);

    void delete(Class clazz, Object id);

    Long getSum(Class clazz, String fieldName, String fetches, String cause, HashMap<String, Object> params);

    Integer getCount(Class clazz, String column, String fetches, String cause, HashMap<String, Object> params);

    <T> List<T> getListByFieldId(Class<T> clazz, String fieldName, Integer id);

    <T> List<T> getListByFieldId(Class<T> clazz, String fieldName, Integer id, Integer pageSize);

    void offsetFieldValue(Class clazz, Object id, String fieldName, Integer offsetValue);

    void updateFieldValue(Class clazz, Object id, String fieldName, Object val);

    void updateTwoFieldValue(Class clazz, Object id, String field1Name, Object val1, String field2Name, Object val2);

    Integer getMaxInteger(Class clazz, String fieldName, String cause);

    Date getMaxDate(Class clazz, String fieldName, String cause);

    void delete(Class clazz, String cause);
}
