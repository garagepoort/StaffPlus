package net.shortninja.staffplus.core.application.database.migrations.sqlite;

import be.garagepoort.mcsqlmigrations.Migration;

public class V28_CreateSessionsTableMigration implements Migration {
    @Override
    public String getStatement() {
        return "CREATE TABLE IF NOT EXISTS sp_sessions (  " +
            "ID integer PRIMARY KEY,  " +
            "player_uuid VARCHAR(36) NOT NULL,  " +
            "vanish_type VARCHAR(36) NOT NULL DEFAULT 'NONE',  " +
            "in_staff_mode boolean NOT NULL DEFAULT false" +
            ");";
    }

    @Override
    public int getVersion() {
        return 28;
    }
}
