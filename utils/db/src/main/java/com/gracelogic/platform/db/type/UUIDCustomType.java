package com.gracelogic.platform.db.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

import java.io.IOException;
import java.util.Properties;

public class UUIDCustomType extends AbstractSingleColumnStandardBasicType {

    private static final long serialVersionUID = 902830399800029445L;

    private static final SqlTypeDescriptor SQL_DESCRIPTOR;
    private static final JavaTypeDescriptor TYPE_DESCRIPTOR;

    static {
        SQL_DESCRIPTOR = PostgresUUIDType.PostgresUUIDSqlTypeDescriptor.INSTANCE;
        System.err.println("UUID for: PostgreSQL");

        TYPE_DESCRIPTOR = UUIDTypeDescriptor.INSTANCE;
    }

    public UUIDCustomType() {
        super(SQL_DESCRIPTOR, TYPE_DESCRIPTOR);
    }

    @Override
    public String getName() {
        return "com.gracelogic.platform.db.type.UUIDCustomType";
    }
}