package com.gracelogic.platform.db.dao;


import com.gracelogic.platform.db.model.IdObject;

import jakarta.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IdObjectDao {
    <T extends IdObject> T getObjectById(Class<T> clazz, String fetches, Object id);

    Integer checkExist(Class clazz, String fetches, String cause, Map<String, Object> params, Integer maxCount);

    <T extends IdObject> T lockObject(Class<T> clazz, Object id);


    <T extends IdObject> T save(T entity);



    <T> List<T> getList(Class<T> clazz);

    <T> List<T> getList(Class<T> clazz, String fetches, String cause, Map<String, Object> params, String sortFieldWithDirection, Integer startRecord, Integer maxResult);

    void delete(Class clazz, Object id);

    Long getSum(Class clazz, String fieldName, String fetches, String cause, Map<String, Object> params);

    Integer getCount(Class clazz, String column, String fetches, String cause, Map<String, Object> params);


    void offsetFieldValue(Class clazz, Object id, String fieldName, Integer offsetValue);

    void updateFieldValue(Class clazz, Object id, String fieldName, Object val);

    void updateTwoFieldValue(Class clazz, Object id, String field1Name, Object val1, String field2Name, Object val2);


    Integer getMaxInteger(Class clazz, String fieldName, String cause, Map<String, Object> params);

    Date getMaxDate(Class clazz, String fieldName, String cause, Map<String, Object> params);

    void delete(Class clazz, String cause, Map<String, Object> params);



    EntityManager getEntityManager();
}
