package com.gracelogic.platform.db.dao;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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
        q.append("order by ").append(sortFieldWithDirection).append(" ");

        Query query = entityManager.createQuery(q.toString());
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
            queryStr.append("order by ").append(sortField).append(" ");
            if (!StringUtils.isEmpty(sortDir)) {
                queryStr.append(StringUtils.equalsIgnoreCase(sortDir, "asc") ? "asc" : "desc").append(" ");
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
