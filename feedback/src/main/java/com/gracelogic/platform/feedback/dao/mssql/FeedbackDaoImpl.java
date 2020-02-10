package com.gracelogic.platform.feedback.dao.mssql;

import com.gracelogic.platform.db.condition.OnMSSQLServerConditional;
import com.gracelogic.platform.feedback.dao.AbstractFeedbackDaoImpl;
import com.gracelogic.platform.feedback.model.Feedback;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Conditional(OnMSSQLServerConditional.class)
public class FeedbackDaoImpl extends AbstractFeedbackDaoImpl {
    private static Logger logger = Logger.getLogger(FeedbackDaoImpl.class);

    @Override
    public Integer getFeedbacksCount(UUID feedbackTypeId, Date startDate, Date endDate, Map<String, String> fields) {
        Integer count = null;
        StringBuilder queryStr = new StringBuilder("select count(ID) from {h-schema}cmn_feedback el where 1=1 ");

        Map<String, Object> params = new HashMap<String, Object>();
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
            for (String key : fields.keySet()) {
                queryStr.append(String.format("and JSON_VALUE(el.fields,'$.%s') like '%s' ", key, fields.get(key)));
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
            for (String key : fields.keySet()) {
                queryStr.append(String.format("and JSON_VALUE(el.fields,'$.%s') like '%s' ", key, fields.get(key)));
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
