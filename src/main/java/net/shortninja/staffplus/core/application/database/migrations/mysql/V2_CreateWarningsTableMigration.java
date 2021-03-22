package net.shortninja.staffplus.core.application.database.migrations.mysql;

import be.garagepoort.mcsqlmigrations.Migration;

public class V2_CreateWarningsTableMigration implements Migration {
    @Override
    public String getStatement() {
        return "CREATE TABLE IF NOT EXISTS sp_warnings (ID INT NOT NULL AUTO_INCREMENT,  Reason VARCHAR(255) NULL,  Warner_UUID VARCHAR(36) NULL,  Player_UUID VARCHAR(36) NOT NULL,  PRIMARY KEY (ID)) ENGINE = InnoDB;";
    }

    @Override
    public int getVersion() {
        return 2;
    }
}
