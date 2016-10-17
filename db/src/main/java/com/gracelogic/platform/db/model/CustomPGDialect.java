package com.gracelogic.platform.db.model;

import org.hibernate.dialect.PostgreSQL9Dialect;

import java.sql.Types;

public class CustomPGDialect extends PostgreSQL9Dialect {
    public CustomPGDialect() {
        super();

        this.registerColumnType(Types.JAVA_OBJECT, "json");
    }
}
