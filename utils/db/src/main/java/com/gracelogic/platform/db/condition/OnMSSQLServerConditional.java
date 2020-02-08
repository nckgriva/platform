package com.gracelogic.platform.db.condition;

public class OnMSSQLServerConditional extends DatabaseCondition {

    @Override
    public String databaseName() {
        return "mssql";
    }
}