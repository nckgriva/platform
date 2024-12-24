package com.gracelogic.platform.user.dao;

import com.gracelogic.platform.db.dao.BaseDao;
import com.gracelogic.platform.user.model.Identifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public abstract class AbstractUserDaoImpl extends BaseDao implements UserDao {
    private static Log logger = LogFactory.getLog(AbstractUserDaoImpl.class);

    @Override
    public Identifier findIdentifier(UUID identifierTypeId, String identifierValue, boolean enrich) {
        String query = "select el from Identifier el " +
                (enrich ? "left join fetch el.user user " : " ") +
                "where el.identifierType.id=:identifierTypeId and el.value=:val order by el.verified desc";

        try {
            List<Identifier> identifiers = getEntityManager().createQuery(query, Identifier.class)
                    .setParameter("identifierTypeId", identifierTypeId)
                    .setParameter("val", identifierValue).setMaxResults(1).getResultList();
            if (!identifiers.isEmpty()) {
                return identifiers.iterator().next();
            }
            else {
                return null;
            }
        } catch (Exception e) {
            logger.debug("Failed to get identifier by value: %s".formatted(identifierValue), e);
        }
        return null;
    }
}
