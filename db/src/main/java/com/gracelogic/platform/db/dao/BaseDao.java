package com.gracelogic.platform.db.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

/**
 * Author: Igor Parkhomenko
 * Date: 23.03.12
 * Time: 9:55
 */
public abstract class BaseDao {
    @PersistenceContext
    private EntityManager entityManager;

    public <T> List<T> getList(Class<T> clazz) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root);
        //criteriaQuery.where(getCasePredicate(filter, cb, root));
        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    public <T> List<T> getList(Class<T> clazz, String fetches, String cause, Map<String, Object> params, String sortField, String sortDirection, Integer startRecord, Integer maxResult) {
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

    public void persistEntity(Object entity) {
        entityManager.persist(entity);
    }

    public <T> T mergeEntity(T entity) {
        return entityManager.merge(entity);
    }


    public EntityManager getEntityManager() {
        return entityManager;
    }

}
