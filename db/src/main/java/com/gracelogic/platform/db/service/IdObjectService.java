package com.gracelogic.platform.db.service;

import com.gracelogic.platform.db.model.IdObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Igor Parkhomenko
 * Date: 18.12.14
 * Time: 12:37
 */
public interface IdObjectService {

    <T extends IdObject> T lockObject(Class<T> clazz, Object id);

    <T extends IdObject> T getObjectById(Class<T> clazz, Object id);

    <T extends IdObject> T getObjectById(Class<T> clazz, String fetches, Object id);

    <T extends IdObject> T save(T entity);

    Integer checkExist(Class clazz, String fetches, String cause, HashMap<String, Object> params, Integer maxCount);

    <T> T saveOrUpdate(T entity);

    <T> List<T> getList(Class<T> clazz);

    void delete(Class clazz, Object id);

    void delete(Class clazz, String cause);

    Integer getCount(Class clazz, String cause);

    Long getSum(Class clazz, String fieldName, String cause);

    Long getSum(Class clazz, String fieldName, String cause, HashMap<String, Object> params);

    Long getSum(Class clazz, String fieldName, String fetches, String cause, HashMap<String, Object> params);

    Integer getCount(Class clazz, String fetches, String cause);

    Integer getCount(Class clazz, String fetches, String cause, HashMap<String, Object> params);

    Integer getCount(Class clazz, String column, String fetches, String cause, HashMap<String, Object> params);

    <T extends IdObject> T setIfModified(Class<T> clazz, T oldObject, Object newId);

    <T> List<T> getList(Class<T> clazz, String fieldName, String search);

    <T> List<T> getListByFieldId(Class<T> clazz, String fieldName, Integer id);

    <T> List<T> getListByFieldId(Class<T> clazz, String fieldName, Integer id, Integer pageSize);

    void offsetFieldValue(Class clazz, Object id, String fieldName, Integer offsetValue);

    <T> List<T> getList(Class<T> clazz, String cause, String sortField, String sortDirection, Integer startRecord, Integer maxResult);

    <T> List<T> getList(Class<T> clazz, String fetches, String cause, String sortField, String sortDirection, Integer startRecord, Integer maxResult);

    <T> List<T> getList(Class<T> clazz, String fetches, String cause, HashMap<String, Object> params, String sortField, String sortDirection, Integer startRecord, Integer maxResult);

    Integer getMaxInteger(Class clazz, String fieldName, String cause);

    Date getMaxDate(Class clazz, String fieldName, String cause);

    void updateFieldValue(Class clazz, Object id, String fieldName, Object val);

    void updateTwoFieldValue(Class clazz, Object id, String field1Name, Object val1, String field2Name, Object val2);

}
