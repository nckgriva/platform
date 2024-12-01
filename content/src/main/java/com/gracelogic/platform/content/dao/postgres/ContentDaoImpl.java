package com.gracelogic.platform.content.dao.postgres;

import com.gracelogic.platform.content.dao.AbstractContentDaoImpl;
import com.gracelogic.platform.content.model.Element;
import com.gracelogic.platform.db.condition.OnPostgreSQLConditional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Query;
import java.math.BigInteger;
import java.util.*;

@Repository
@Conditional(OnPostgreSQLConditional.class)
public class ContentDaoImpl extends AbstractContentDaoImpl {
    private static Logger logger = LoggerFactory.getLogger(ContentDaoImpl.class);

    @Override
    public Integer getElementsCount(String query, Collection<String> queryFields, Collection<String> sectionIds, Boolean active, Date validOnDate, Map<String, String> fields) {
        BigInteger count = null;
        StringBuilder queryStr = new StringBuilder("select count(el.ID) from {h-schema}cmn_element el where 1=1 ");

        Map<String, Object> params = new HashMap<>();
        if (active != null) {
            queryStr.append("and el.is_active = :active ");
            params.put("active", active);
        }
        if (sectionIds != null && !sectionIds.isEmpty()) {
            queryStr.append("and el.section_id\\:\\:text in (:sectionIds) ");
            params.put("sectionIds", sectionIds);

        }
        if (validOnDate != null) {
            queryStr.append("and el.start_dt <= :validOnDate and el.end_dt >= :validOnDate ");
            params.put("validOnDate", validOnDate);
        }

        if (fields != null && !fields.isEmpty()) {
            int i = 0;
            for (String key : fields.keySet()) {
                i++;
                queryStr.append(String.format("and el.fields ->> :key_%d = :val_%d ", i, i));
                params.put("key_" + i, key);
                params.put("val_" + i, fields.get(key));
            }
        }
        if (query != null && !StringUtils.isEmpty(query)) {
            queryStr.append("and ( lower(el.name) like :query ");
            if (queryFields != null && !queryFields.isEmpty()) {
                int i = 0;
                for (String key : queryFields) {
                    i++;
                    queryStr.append(String.format("or lower(el.fields ->> :q_%d) like :query ", i));
                    params.put("q_" + i, key);
                }
            }
            queryStr.append(") ");
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
    public List<Element> getElements(String query, Collection<String> queryFields, Collection<String> sectionIds, Boolean active, Date validOnDate, Map<String, String> fields, String sortField, String sortDir, Integer startRecord, Integer recordsOnPage) {
        List<String> availableSortingFields = Arrays.asList("el.id", "el.created_dt", "el.changed_dt", "el.section_id", "el.is_active", "el.start_dt", "el.end_dt", "el.element_dt", "el.name", "el.sort_order", "el.external_id");
        if (!availableSortingFields.contains(StringUtils.lowerCase(sortField))) {
            throw new RuntimeException("Required sort field is unavailable");
        }

        List<Element> elements = Collections.emptyList();
        StringBuilder queryStr = new StringBuilder("select * from {h-schema}cmn_element el where 1=1 ");

        Map<String, Object> params = new HashMap<>();
        if (active != null) {
            queryStr.append("and el.is_active = :active ");
            params.put("active", active);
        }
        if (sectionIds != null && !sectionIds.isEmpty()) {
            queryStr.append("and el.section_id\\:\\:text in (:sectionIds) ");
            params.put("sectionIds", sectionIds);

        }
        if (validOnDate != null) {
            queryStr.append("and el.start_dt <= :validOnDate and el.end_dt >= :validOnDate ");
            params.put("validOnDate", validOnDate);
        }

        if (fields != null && !fields.isEmpty()) {
            int i = 0;
            for (String key : fields.keySet()) {
                i++;
                queryStr.append(String.format("and el.fields ->> :key_%d = :val_%d ", i, i));
                params.put("key_" + i, key);
                params.put("val_" + i, fields.get(key));
            }
        }
        if (query != null && !StringUtils.isEmpty(query)) {
            queryStr.append("and ( lower(el.name) like :query ");
            if (queryFields != null && !queryFields.isEmpty()) {
                int i = 0;
                for (String key : queryFields) {
                    i++;
                    queryStr.append(String.format("or lower(el.fields ->> :q_%d) like :query ", i));
                    params.put("q_" + i, key);
                }
            }
            queryStr.append(") ");
            params.put("query", "%%" + StringUtils.lowerCase(query) + "%%");
        }
        appendSortClause(queryStr, sortField, sortDir);

        try {
            Query q = getEntityManager().createNativeQuery(queryStr.toString(), Element.class);
            appendPaginationClause(q, params, recordsOnPage, startRecord);

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
