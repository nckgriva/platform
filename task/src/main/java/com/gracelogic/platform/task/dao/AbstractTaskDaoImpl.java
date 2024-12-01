package com.gracelogic.platform.task.dao;

import com.gracelogic.platform.db.dao.BaseDao;
import com.gracelogic.platform.task.DataConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.Query;

public abstract class AbstractTaskDaoImpl extends BaseDao implements TaskDao {
    private static Logger logger = LoggerFactory.getLogger(AbstractTaskDaoImpl.class);

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
