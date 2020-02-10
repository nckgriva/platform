package com.gracelogic.platform.user.dao;

import com.gracelogic.platform.db.dao.BaseDao;
import com.gracelogic.platform.user.model.Identifier;import org.apache.log4j.Logger;

import java.util.*;

public abstract class AbstractUserDaoImpl extends BaseDao implements UserDao {
    private static Logger logger = Logger.getLogger(AbstractUserDaoImpl.class);

    @Override
    public Identifier findIdentifier(UUID identifierTypeId, String identifierValue, boolean enrich) {
        //TODO: сделать оптимальнее
        String query = "select el from Identifier el " +
                (enrich ? "left join fetch el.user user " : " ") +
                "where el.identifierType.id=:identifierTypeId and el.value=:val";

        logger.info(query);

        try {
            return (Identifier) getEntityManager().createQuery(query, Identifier.class)
                    .setParameter("identifierTypeId", identifierTypeId)
                    .setParameter("val", identifierValue).getSingleResult();
        } catch (Exception e) {
            logger.debug(String.format("Failed to get identifier by value: %s", identifierValue), e);
        }
        return null;
    }
}
