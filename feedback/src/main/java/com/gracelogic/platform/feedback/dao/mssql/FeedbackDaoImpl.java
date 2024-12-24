package com.gracelogic.platform.feedback.dao.mssql;

import com.gracelogic.platform.db.condition.OnMSSQLServerConditional;
import com.gracelogic.platform.feedback.dao.AbstractFeedbackDaoImpl;
import com.gracelogic.platform.feedback.model.Feedback;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Query;
import java.math.BigInteger;
import java.util.*;

@Repository
@Conditional(OnMSSQLServerConditional.class)
public class FeedbackDaoImpl extends AbstractFeedbackDaoImpl {
    private static Log logger = LogFactory.getLog(FeedbackDaoImpl.class);

    @Override
    public Integer getFeedbacksCount(UUID feedbackTypeId, Date startDate, Date endDate, Map<String, String> fields) {
        Integer count = null;
        StringBuilder queryStr = new StringBuilder("select count(ID) from {h-schema}cmn_feedback el where 1=1 ");

        Map<String, Object> params = new HashMap<>();
        if (feedbackTypeId != null) {
            queryStr.append("and el.feedback_type_id = :feedbackTypeId ");
            params.put("feedbackTypeId", feedbackTypeId);
        }
        if (startDate != null) {
            queryStr.append("and el.created_dt >= :startDate ");
            params.put("startDate", startDate);
        }
        if (endDate != null) {
            queryStr.append("and el.created_dt <= :endDate ");
            params.put("endDate", endDate);
        }
        if (fields != null && !fields.isEmpty()) {
            int i = 0;
            for (String key : fields.keySet()) {
                i++;
                queryStr.append(String.format("and JSON_VALUE(el.fields, :key_%d) like val_%d ", i, i));
//                queryStr.append(String.format("and JSON_VALUE(el.fields,'$.%s') like '%s' ", key, fields.get(key)));

                params.put("key_" + i, "$." + key);
                params.put("val_" + i, fields.get(key));
            }
        }

        try {
            Query query = getEntityManager().createNativeQuery(queryStr.toString());

            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }

            count = (Integer) query.getSingleResult();
        } catch (Exception e) {
            logger.error("Failed to get feedbacks count", e);
        }

        return count != null ? count.intValue() : null;
    }

    @Override
    public List<Feedback> getFeedbacks(UUID feedbackTypeId, Date startDate, Date endDate, Map<String, String> fields, String sortField, String sortDir, Integer startRecord, Integer recordsOnPage) {
        List<String> availableSortingFields = Arrays.asList("el.id", "el.created_dt", "el.changed_dt", "el.feedback_type_id");
        if (!availableSortingFields.contains(StringUtils.lowerCase(sortField))) {
            throw new RuntimeException("Required sort field is unavailable");
        }

        List<Feedback> feedbacks = Collections.emptyList();
        StringBuilder queryStr = new StringBuilder("select * from {h-schema}cmn_feedback el where 1=1 ");

        Map<String, Object> params = new HashMap<>();
        if (feedbackTypeId != null) {
            queryStr.append("and el.feedback_type_id = :feedbackTypeId ");
            params.put("feedbackTypeId", feedbackTypeId);
        }
        if (startDate != null) {
            queryStr.append("and el.created_dt >= :startDate ");
            params.put("startDate", startDate);
        }
        if (endDate != null) {
            queryStr.append("and el.created_dt <= :endDate ");
            params.put("endDate", endDate);
        }
        if (fields != null && !fields.isEmpty()) {
            int i = 0;
            for (String key : fields.keySet()) {
                i++;
                queryStr.append(String.format("and JSON_VALUE(el.fields, :key_%d) like val_%d ", i, i));
//                queryStr.append(String.format("and JSON_VALUE(el.fields,'$.%s') like '%s' ", key, fields.get(key)));

                params.put("key_" + i, "$." + key);
                params.put("val_" + i, fields.get(key));
            }
        }

        appendSortClause(queryStr, sortField, sortDir);

        try {
            Query query = getEntityManager().createNativeQuery(queryStr.toString(), Feedback.class);
            appendPaginationClause(query, params, recordsOnPage, startRecord);

            for (String key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }

            feedbacks = query.getResultList();

        } catch (Exception e) {
            logger.error("Failed to get feedbacks", e);
        }

        return feedbacks;
    }
}
