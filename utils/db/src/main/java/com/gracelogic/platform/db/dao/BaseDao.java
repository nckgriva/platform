package com.gracelogic.platform.db.dao;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

public abstract class BaseDao {
    @PersistenceContext
    private EntityManager entityManager;

    public <T> List<T> getList(Class<T> clazz) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root);
        TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    public <T> List<T> getList(Class<T> clazz, String fetches, String cause, Map<String, Object> params, String sortFieldWithDirection, Integer startRecord, Integer maxResult) {
        if (fetches == null) {
            fetches = "";
        }
        StringBuilder q = new StringBuilder("select el from ").append(clazz.getSimpleName()).append(" el ").append(fetches).append(" ");
        if (!StringUtils.isEmpty(cause)) {
            q.append("where ").append(cause).append(" ");
        }
        if (StringUtils.isEmpty(sortFieldWithDirection)) {
            sortFieldWithDirection = "el.created ASC";
        }
        q.append("order by :sortFieldWithDirection ");

        Query query = entityManager.createQuery(q.toString()).setParameter("sortFieldWithDirection", sortFieldWithDirection);
        if (params != null) {
            for (String paramName : params.keySet()) {
                query.setParameter(paramName, params.get(paramName));
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

    protected void appendSortClause(StringBuilder queryStr, String sortField, String sortDir) {
        if (!StringUtils.isEmpty(sortField)) {
            queryStr.append(String.format("order by %s ", sortField));
            if (!StringUtils.isEmpty(sortDir)) {
                queryStr.append(String.format("%s ", sortDir));
            }
        }
    }

    protected void appendPaginationClause(Query query, Map<String, Object> params, Integer recordsOnPage, Integer startRecord) {
        if (recordsOnPage != null) {
            query.setMaxResults(recordsOnPage);
            if (startRecord != null) {
                query.setFirstResult(startRecord);
            }
        }
    }

}
