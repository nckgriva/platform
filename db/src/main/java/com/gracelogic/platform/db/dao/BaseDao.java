package com.gracelogic.platform.db.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 23.03.12
 * Time: 9:55
 */
public abstract class BaseDao {
    @PersistenceContext
    private EntityManager entityManager;

    private <T> CriteriaQuery<T> getCriteriaQuery(Class T) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(T);
        return criteriaQuery;
    }

    public <T> TypedQuery<T> getTypedQuery(Class T) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(T);
        Root<T> root = criteriaQuery.from(T);
        criteriaQuery.select(root);
        //criteriaQuery.where(getCasePredicate(filter, cb, root));
        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery;
    }

    public <T> List<T> getList(Class<T> clazz) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root);
        //criteriaQuery.where(getCasePredicate(filter, cb, root));
        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    public <T> List<T> getList(Class<T> clazz, String fieldName, String search) {
        String query = new String("select el from " + clazz.getName() + " el where upper(el." + fieldName + ") like upper(:search)");

        return entityManager.createQuery(query).setParameter("search", search + "%").getResultList();
    }

    public <T> List<T> getListByFieldId(Class<T> clazz, String fieldName, Integer id, Integer pageSize) {
        String query = new String("select el from " + clazz.getSimpleName() + " el where el." + fieldName + " = :id");

        return entityManager.createQuery(query).setParameter("id", id)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public <T> List<T> getList(Class<T> clazz, String fetches, String cause, HashMap<String, Object> params, String sortField, String sortDirection, Integer startRecord, Integer maxResult) {
        if (fetches == null) {
            fetches = "";
        }
        String queryStr = new String("select el from " + clazz.getSimpleName() + " el " + fetches + " ");
        if (cause != null && !cause.isEmpty()) {
            queryStr += String.format(" where %s ", cause);
        }
        if (sortField == null || sortField.isEmpty()) {
            sortField = "el.id";
            sortDirection = "DESC";
        }

        if (sortDirection == null || sortDirection.isEmpty()) {
            sortDirection = "ASC";
        }

        queryStr += String.format(" order by %s %s ", sortField, sortDirection);

        Query query = entityManager.createQuery(queryStr);
        if (params != null) {
            for (String paramName : params.keySet()) {
                Object paramValue = params.get(paramName);
                query.setParameter(paramName, paramValue);
            }
        }
        if (startRecord != null) {
            query.setFirstResult(startRecord);
        }

        if (maxResult != null) {
            query.setMaxResults(maxResult);
        }

        return query.getResultList();
    }

    public <T> List<T> getListByFieldId(Class<T> clazz, String fieldName, Integer id) {
        return getListByFieldId(clazz, fieldName, id, 50);
    }

    public <T> T getEntity(Class T, String field, String code) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Metamodel metamodel = entityManager.getMetamodel();
        EntityType<T> metaT = metamodel.entity(T);
        CriteriaQuery<T> criteriaQuery = cb.createQuery(T);
        Root<T> root = criteriaQuery.from(T);
        criteriaQuery.select(root);
        Path<String> codePath = root.get(metaT.getSingularAttribute(field, String.class));
        Predicate codePredicate = cb
                .like(cb.lower(codePath), code.toLowerCase());
        criteriaQuery.where(codePredicate);
        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getSingleResult();

    }

    public void persistEntity(Object entity) {
        entityManager.persist(entity);
    }

    public <T> T mergeEntity(T entity) {
        return entityManager.merge(entity);
    }

    public <T> T findEntity(Class T, int id) {
        return (T) entityManager.find(T, id);
    }

    public <T> T findEntity(Class T, UUID id) {
        return (T) entityManager.find(T, id);
    }

    public void removeEntity(Object entity) {
        entityManager.remove(entity);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public <T> T saveOrUpdate(T entity) {
        return entityManager.merge(entity);
    }
}
