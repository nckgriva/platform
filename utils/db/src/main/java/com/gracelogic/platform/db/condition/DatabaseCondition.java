package com.gracelogic.platform.db.condition;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.io.InputStream;
import java.util.Properties;

public abstract class DatabaseCondition implements Condition {

    protected Logger logger = Logger.getLogger(this.getClass());
    private Properties dbProperties;

    public DatabaseCondition() {
        try (final InputStream inputStream = getClass().getResourceAsStream("/db.properties")) {
            dbProperties = new Properties();
            dbProperties.load(inputStream);
        } catch (Exception e) {
            logger.error("Cannot load db.properties file", e);
        }
    }

    public abstract String databaseName();

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return databaseName().equalsIgnoreCase(dbProperties.getProperty("dialect"));
    }
}