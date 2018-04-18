package com.gracelogic.platform.feedback.dao;

import com.gracelogic.platform.db.dao.BaseDao;
import org.apache.log4j.Logger;

public abstract class AbstractFeedbackDaoImpl extends BaseDao implements FeedbackDao {
    private static Logger logger = Logger.getLogger(AbstractFeedbackDaoImpl.class);
}
