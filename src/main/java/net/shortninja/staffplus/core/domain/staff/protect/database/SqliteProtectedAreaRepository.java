package net.shortninja.staffplus.core.domain.staff.protect.database;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcsqlmigrations.SqlConnectionProvider;
import net.shortninja.staffplus.core.application.config.Options;
import net.shortninja.staffplus.core.domain.location.LocationRepository;

@IocBean(conditionalOnProperty = "storage.type=sqlite")
public class SqliteProtectedAreaRepository extends AbstractSqlProtectedAreaRepository {

    public SqliteProtectedAreaRepository(LocationRepository locationRepository, SqlConnectionProvider sqlConnectionProvider, Options options) {
        super(locationRepository, sqlConnectionProvider, options);
    }
}
