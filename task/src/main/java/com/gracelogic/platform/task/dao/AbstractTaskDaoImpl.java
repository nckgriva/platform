package com.gracelogic.platform.task.dao;

import com.gracelogic.platform.db.dao.BaseDao;
import com.gracelogic.platform.task.DataConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jakarta.persistence.Query;

public abstract class AbstractTaskDaoImpl extends BaseDao implements TaskDao {
    private static Log logger = LogFactory.getLog(AbstractTaskDaoImpl.class);

    public void resetAllTasks() {
        Query query = getEntityManager().createQuery("UPDATE TaskExecutionLog l SET l.state.id = :failState where l.state.id = :inProgressState");
        query.setParameter("inProgressState", DataConstants.TaskExecutionStates.IN_PROGRESS.getValue());
        query.setParameter("failState", DataConstants.TaskExecutionStates.FAIL.getValue());

        try {
            query.executeUpdate();
        }
        catch (Exception e) {
            logger.error("Failed to reset tasks", e);
        }
    }
}
