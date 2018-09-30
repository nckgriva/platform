package com.gracelogic.platform.content.dao;

import com.gracelogic.platform.content.model.Element;
import com.gracelogic.platform.db.service.IdObjectServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

@Repository
public class ContentDaoImpl extends AbstractContentDaoImpl {
    private static Logger logger = Logger.getLogger(ContentDaoImpl.class);

    @Override
    public Integer getElementsCount(String query, Collection<String> queryFields, Collection<UUID> sectionIds, Boolean active, Date validOnDate, Map<String, String> fields) {
        BigInteger count = null;
        StringBuilder queryStr = new StringBuilder("select count(ID) from {h-schema}cmn_element where 1=1 ");

        Map<String, Object> params = new HashMap<>();
        if (active != null) {
            queryStr.append("and is_active = :active ");
            params.put("active", active);
        }
        if (sectionIds != null && !sectionIds.isEmpty()) {
            queryStr.append(String.format("and section_id in (%s) ", IdObjectServiceImpl.valuesAsString(sectionIds)));
        }
        if (validOnDate != null) {
            queryStr.append("and start_dt <= :validOnDate and end_dt >= :validOnDate ");
            params.put("validOnDate", validOnDate);
        }

        if (fields != null && !fields.isEmpty()) {
            for (String key : fields.keySet()) {
                queryStr.append(String.format("and fields ->> '%s' = '%s' ", key, fields.get(key)));
            }
        }
        if (query != null && !StringUtils.isEmpty(query)) {
            queryStr.append("and lower(name) like :query ");
            if (queryFields != null && !queryFields.isEmpty()) {
                for (String key : queryFields) {
                    queryStr.append(String.format("and lower(fields ->> '%s') like :query ", key));
                }
            }
            params.put("query", "%%" + StringUtils.lowerCase(query) + "%%");
        }

        try {
            Query q = getEntityManager().createNativeQuery(queryStr.toString());

            for (String key : params.keySet()) {
                q.setParameter(key, params.get(key));
            }

            count = (BigInteger) q.getSingleResult();
        } catch (Exception e) {
            logger.error("Failed to get elements count", e);
        }

        return count != null ? count.intValue() : null;
    }

    @Override
    public List<Element> getElements(String query, Collection<String> queryFields, Collection<UUID> sectionIds, Boolean active, Date validOnDate, Map<String, String> fields, String sortField, String sortDir, Integer startRecord, Integer recordsOnPage) {
        List<Element> elements = Collections.emptyList();
        StringBuilder queryStr = new StringBuilder("select * from {h-schema}cmn_element where 1=1 ");

        Map<String, Object> params = new HashMap<>();
        if (active != null) {
            queryStr.append("and is_active = :active ");
            params.put("active", active);
        }
        if (sectionIds != null && !sectionIds.isEmpty()) {
            queryStr.append(String.format("and section_id in (%s) ", IdObjectServiceImpl.valuesAsString(sectionIds)));
        }
        if (validOnDate != null) {
            queryStr.append("and start_dt <= :validOnDate and end_dt >= :validOnDate ");
            params.put("validOnDate", validOnDate);
        }

        if (fields != null && !fields.isEmpty()) {
            for (String key : fields.keySet()) {
                queryStr.append(String.format("and fields ->> '%s' = '%s' ", key, fields.get(key)));
            }
        }
        if (query != null && !StringUtils.isEmpty(query)) {
            queryStr.append("and lower(name) like :query ");
            if (queryFields != null && !queryFields.isEmpty()) {
                for (String key : queryFields) {
                    queryStr.append(String.format("and lower(fields ->> '%s') like :query ", key));
                }
            }
            params.put("query", "%%" + StringUtils.lowerCase(query) + "%%");
        }
        appendSortClause(queryStr, sortField, sortDir);
        appendPaginationClause(queryStr, params, recordsOnPage, startRecord);

        try {
            Query q = getEntityManager().createNativeQuery(queryStr.toString(), Element.class);

            for (String key : params.keySet()) {
                q.setParameter(key, params.get(key));
            }

            elements = q.getResultList();

        } catch (Exception e) {
            logger.error("Failed to get elements", e);
        }

        return elements;
    }
}
