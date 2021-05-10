package net.shortninja.staffplus.core.domain.webui;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcsqlmigrations.SqlConnectionProvider;
import net.shortninja.staffplus.core.common.config.Options;
import net.shortninja.staffplus.core.common.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@IocBean
public class SqlWebUiRegistrationRepository implements WebUiRegistrationRepository {

    private final SqlConnectionProvider sqlConnectionProvider;
    protected final Options options;

    public SqlWebUiRegistrationRepository(SqlConnectionProvider sqlConnectionProvider, Options options) {
        this.sqlConnectionProvider = sqlConnectionProvider;
        this.options = options;
    }

    protected Connection getConnection() {
        return sqlConnectionProvider.getConnection();
    }

    @Override
    public void addRegistrationRequest(UUID playerUuid, String authenticationKey, String role) {
        try (Connection sql = getConnection();
             PreparedStatement insert = sql.prepareStatement("INSERT INTO sp_web_ui_registration(player_uuid, authentication_key, role, timestamp) " +
                 "VALUES(?, ?, ?, ?);")) {
            insert.setString(1, playerUuid.toString());
            insert.setString(2, authenticationKey);
            insert.setString(3, role);
            insert.setLong(4, System.currentTimeMillis());
            insert.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}