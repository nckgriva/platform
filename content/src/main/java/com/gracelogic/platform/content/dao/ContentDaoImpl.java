package com.gracelogic.platform.content.dao;

import com.gracelogic.platform.content.model.Element;
import com.gracelogic.platform.db.JPAProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

/**
 * Author: Igor Parkhomenko
 * Date: 12.07.12
 * Time: 21:50
 */
@Repository
public class ContentDaoImpl extends AbstractContentDaoImpl {
    private static Logger logger = Logger.getLogger(ContentDaoImpl.class);

    @Override
    public Integer getElementsCount(UUID sectionId, Boolean active, Date validOnDate, Map<String, String> fields) {
        BigInteger count = null;
        StringBuilder queryStr = new StringBuilder(String.format("select count(ID) from %s.cmn_element where 1=1 ", JPAProperties.DEFAULT_SCHEMA));

        Map<String, Object> params = new HashMap<>();
        if (active != null) {
            queryStr.append("and is_active = :active ");
            params.put("active", active);
        }
        if (sectionId != null) {
            queryStr.append("and section_id = :sectionId ");
            params.put("sectionId", sectionId);
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

        try {
            Query query = getEntityManager().createNativeQuery(queryStr.toString());

            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }

            count = (BigInteger) query.getSingleResult();
        } catch (Exception e) {
            logger.error("Failed to get elements count", e);
        }

        return count != null ? count.intValue() : null;
    }

    @Override
    public List<Element> getElements(UUID sectionId, Boolean active, Date validOnDate, Map<String, String> fields, String sortField, String sortDir, Integer startRecord, Integer recordsOnPage) {
        List<Element> elements = Collections.emptyList();
        StringBuilder queryStr = new StringBuilder(String.format("select * from %s.cmn_element where 1=1 ", JPAProperties.DEFAULT_SCHEMA));

        Map<String, Object> params = new HashMap<>();
        if (active != null) {
            queryStr.append("and is_active = :active ");
            params.put("active", active);
        }
        if (sectionId != null) {
            queryStr.append("and section_id = :sectionId ");
            params.put("sectionId", sectionId);
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

        if (!StringUtils.isEmpty(sortField)) {
            queryStr.append(String.format("order by %s ", sortField));
            if (!StringUtils.isEmpty(sortDir)) {
                queryStr.append(String.format("%s ", sortDir));
            }
        }

        if (recordsOnPage != null) {
            queryStr.append("limit :recordsOnPage ");
            params.put("recordsOnPage", recordsOnPage);
            if (startRecord != null) {
                queryStr.append("offset :startRecord ");
                params.put("startRecord", startRecord);
            }
        }

        try {
            Query query = getEntityManager().createNativeQuery(queryStr.toString(), Element.class);

            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }

            elements = query.getResultList();

        } catch (Exception e) {
            logger.error("Failed to get elements", e);
        }

        return elements;
    }
}
