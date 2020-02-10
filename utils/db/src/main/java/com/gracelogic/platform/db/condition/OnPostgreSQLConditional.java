package com.gracelogic.platform.db.condition;

public class OnPostgreSQLConditional extends DatabaseCondition {

    @Override
    public String databaseName() {
        return "postgres";
    }
}