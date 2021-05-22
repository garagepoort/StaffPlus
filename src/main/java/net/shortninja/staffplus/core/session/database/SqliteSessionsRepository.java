package net.shortninja.staffplus.core.session.database;

import be.garagepoort.mcioc.IocBean;
import be.garagepoort.mcsqlmigrations.SqlConnectionProvider;
import net.shortninja.staffplus.core.common.exceptions.DatabaseException;

import java.sql.*;

@IocBean(conditionalOnProperty = "storage.type=sqlite")
public class SqliteSessionsRepository extends AbstractSqlSessionsRepository {

    public SqliteSessionsRepository(SqlConnectionProvider sqlConnectionProvider) {
        super(sqlConnectionProvider);
    }

    @Override
    public int addSession(SessionEntity sessionEntity) {
        try (Connection sql = getConnection();
             PreparedStatement insert = sql.prepareStatement("INSERT INTO sp_sessions(player_uuid, vanish_type, in_staff_mode, muted_staff_chat_channels, staff_mode_name) " +
                 "VALUES(?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            sql.setAutoCommit(false);
            insert.setString(1, sessionEntity.getPlayerUuid().toString());
            insert.setString(2, sessionEntity.getVanishType().toString());
            insert.setBoolean(3, sessionEntity.getStaffMode());
            insert.setString(4, String.join(";", sessionEntity.getMutedStaffChatChannels()));
            if (sessionEntity.getStaffModeName() == null) {
                insert.setNull(5, Types.VARCHAR);
            } else {
                insert.setString(5, sessionEntity.getStaffModeName());
            }
            insert.executeUpdate();

            Statement statement = sql.createStatement();
            ResultSet generatedKeys = statement.executeQuery("SELECT last_insert_rowid()");
            int generatedKey = -1;
            if (generatedKeys.next()) {
                generatedKey = generatedKeys.getInt(1);
            }
            sql.commit(); // Commits transaction.
            return generatedKey;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

}
