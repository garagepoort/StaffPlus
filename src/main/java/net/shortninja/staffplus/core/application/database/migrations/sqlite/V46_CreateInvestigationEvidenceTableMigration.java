package net.shortninja.staffplus.core.application.database.migrations.sqlite;

import be.garagepoort.mcsqlmigrations.Migration;

public class V46_CreateInvestigationEvidenceTableMigration implements Migration {
    @Override
    public String getStatement() {
        return "CREATE TABLE IF NOT EXISTS sp_investigation_evidence (  " +
            "ID integer PRIMARY KEY,  " +
            "investigation_id integer NOT NULL,  " +
            "evidence_id integer NOT NULL,  " +
            "evidence_type VARCHAR(36) NOT NULL,  " +
            "linked_by_uuid VARCHAR(36) NOT NULL,  " +
            "timestamp BIGINT NOT NULL, " +
            "FOREIGN KEY (investigation_id) REFERENCES sp_investigations(id) ON DELETE CASCADE" +
            ");";
    }

    @Override
    public int getVersion() {
        return 46;
    }
}
