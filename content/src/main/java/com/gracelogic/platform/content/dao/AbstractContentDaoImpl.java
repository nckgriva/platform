package com.gracelogic.platform.content.dao;

import com.gracelogic.platform.db.dao.BaseDao;
import com.gracelogic.platform.user.model.AuthCode;
import com.gracelogic.platform.user.model.IncorrectLoginAttempt;
import com.gracelogic.platform.user.model.User;
import com.gracelogic.platform.user.service.DataConstants;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.*;

/**
 * Author: Igor Parkhomenko
 * Date: 12.07.12
 * Time: 21:44
 */
public abstract class AbstractContentDaoImpl extends BaseDao implements ContentDao {
    private static Logger logger = Logger.getLogger(AbstractContentDaoImpl.class);

}
